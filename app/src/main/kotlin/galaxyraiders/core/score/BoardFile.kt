package galaxyraiders.core.score

import com.beust.klaxon.Klaxon
import java.io.File
import java.io.FileWriter
import java.io.IOException
import galaxyraiders.core.score.GameScore

abstract class BoardFile(private val fileName: String) {

    data class Board(var games: MutableList<GameScore>)

    val board = this.read()

    var useLastGame = false

    private fun read(): Board {
        val file = File(fileName)
        if (!file.exists()) {
            return Board(mutableListOf<GameScore>())
        }
        val jsonText = file.readText(Charsets.UTF_8)
        return Klaxon().parse<Board>(jsonText)!!
    }

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
        if (useLastGame) {
            this.board.games.removeLast()
        } else {
            useLastGame = true
        }
        this.add(gameScore)
        this.write()
    }
}

class ScoreboardFile:
    BoardFile("./src/main/kotlin/galaxyraiders/core/score/Scoreboard.json") {
    override fun add(gameScore: GameScore) {
        this.board.games.add(gameScore)
    }
}

class LeaderboardFile:
    BoardFile("./src/main/kotlin/galaxyraiders/core/score/Leaderboard.json") {
    override fun add(gameScore: GameScore) {
        this.board.games.add(gameScore)
        this.board.games.sortByDescending { it.finalScore }
        if (this.board.games.size > 3) {
            this.board.games.removeLast()
        }
    }
}
