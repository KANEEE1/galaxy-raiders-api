package galaxyraiders.core.score

import com.beust.klaxon.Klaxon
import java.io.File
import java.io.FileWriter
import java.io.IOException
import galaxyraiders.core.score.GameScore

abstract class BoardFile(private val fileName: String) {

    data class Board(var games: MutableList<GameScore>)

    val board: Board

    init {
        board = this.read()
        this.firstTimeAdd(GameScore(0.0, 0))
        this.write()
    }

    private fun read(): Board {
        val file = File(fileName)
        if (!file.exists()) {
            return Board(mutableListOf<GameScore>())
        }
        val jsonText = file.readText(Charsets.UTF_8)
        return Klaxon().parse<Board>(jsonText)!!
    }

    abstract fun firstTimeAdd(gameScore: GameScore)

    abstract fun add(gameScore: GameScore)

    private fun write() {
        val jsonText = Klaxon()
            .toJsonString(this.board)

        val writer = FileWriter(this.fileName)
        try {
            writer.write(jsonText)
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun update(gameScore: GameScore) {
        this.add(gameScore)
        this.write()
    }
}

class ScoreboardFile:
    BoardFile("./src/main/kotlin/Scoreboard.json") {

    override fun firstTimeAdd(gameScore: GameScore) {
        this.board.games.add(gameScore)
    }

    override fun add(gameScore: GameScore) {
        this.board.games.removeLast()
        this.board.games.add(gameScore)
    }
}

class LeaderboardFile:
    BoardFile("./src/main/kotlin/Leaderboard.json") {
    private var previousIndexOfSameGame = -1

    override fun firstTimeAdd(gameScore: GameScore) {
        /* Won't put in the leaderboard games with 0 asteroids destroyed */
    }

    override fun add(gameScore: GameScore) {
        if (previousIndexOfSameGame != -1) {
            this.board.games.removeAt(previousIndexOfSameGame)
        }
        val i = indexToAdd(gameScore)
        previousIndexOfSameGame = i
        if (i != -1) {
            this.board.games.add(i, gameScore)
        }
    }

    private fun indexToAdd(gameScore: GameScore): Int {
        var i = 0
        while (i < this.board.games.size &&
                gameScore.finalScore < this.board.games.elementAt(i).finalScore) {
            i++
        }
        if (i == 3) {
            return -1
        }
        return i
    }
}