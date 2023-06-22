package galaxyraiders.core.score

import com.beust.klaxon.Klaxon
import java.io.File
import java.io.FileWriter
import java.io.IOException
import kotlin.math.min
import galaxyraiders.core.score.GameScore

abstract class BoardFile(private val fileName: String) {

    data class Board(var games: List<GameScore>)

    val board = this.read()

    private fun read(): Board {
        val file = File(fileName)
        if (!file.exists()) {
            return Board(emptyList())
        }
        val jsonText = file.readText(Charsets.UTF_8)
        return Klaxon().parse<Board>(jsonText)!!
    }

    abstract fun add(lastGame: GameScore)

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

    fun update(lastGame: GameScore) {
        this.add(lastGame)
        this.write()
    }
}

class ScoreboardFile:
    BoardFile("galaxyraiders/core/score/Scoreboard.json") {
    override fun add(lastGame: GameScore) {
        this.board.games += lastGame
    }
}

class LeaderboardFile:
    BoardFile("galaxyraiders/core/score/Leaderboard.json") {
    override fun add(lastGame: GameScore) {
        this.board.games += lastGame
        this.board.games = this.board.games.sortedByDescending { it.finalScore }
        this.board.games = this.board.games.subList(0,min(3,this.board.games.size))
    }
}