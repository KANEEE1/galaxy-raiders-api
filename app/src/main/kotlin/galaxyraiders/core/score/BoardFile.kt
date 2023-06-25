package galaxyraiders.core.score

import com.beust.klaxon.Klaxon
import java.io.File
import java.io.FileWriter
import java.io.IOException
import galaxyraiders.core.score.GameScore

abstract class BoardFile(private val fileName: String) {
    data class Board(var games: MutableList<GameScore>)

    val board: Board
    var boardHasCurrentGame = false

    init {
        board = this.read()
        this.update(GameScore(0.0, 0))
    }

    private fun read(): Board {
        val file = File(fileName)
        if (!file.exists()) {
            return Board(mutableListOf<GameScore>())
        }
        val jsonText = file.readText(Charsets.UTF_8)
        return Klaxon().parse<Board>(jsonText)!!
    }

    abstract fun removeCurrentGame()

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
        if (this.boardHasCurrentGame) {
            this.removeCurrentGame()
        }
        this.add(gameScore)
        this.boardHasCurrentGame = true
        this.write()
    }
}

class ScoreboardFile:
    BoardFile("./src/main/kotlin/Scoreboard.json") {
    override fun removeCurrentGame() {
        this.board.games.removeLast()
    }

    override fun add(gameScore: GameScore) {
        this.board.games.add(gameScore)
    }

}

class LeaderboardFile:
    BoardFile("./src/main/kotlin/Leaderboard.json") {
    private var lastIndexOfCurrentGame = -1

    override fun removeCurrentGame() {
        if (lastIndexOfCurrentGame != -1) {
            this.board.games.removeAt(lastIndexOfCurrentGame)
        }
    }

    override fun add(gameScore: GameScore) {
        val i = indexToAdd(gameScore)
        lastIndexOfCurrentGame = i
        if (i != -1) {
            this.board.games.add(i, gameScore)
            if (this.board.games.size == 4) {
                this.board.games.removeLast()
            }
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