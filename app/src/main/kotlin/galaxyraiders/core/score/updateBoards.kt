package galaxyraiders.core.score

import com.beust.klaxon.Klaxon
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

const val scoreboardFileName: String = "Scoreboard.json"

data class GameScore(val timestamp: String,
                     var finalScore: Double, var asteroidsDestroyed: Int)

data class Scoreboard(var games: List<GameScore>)

fun updateScoreboard(game1: GameScore) {
    val file = File(scoreboardFileName)
    val scoreboard: Scoreboard


    if (!file.exists()) {
        scoreboard = Scoreboard(emptyList())
    } else {
        val jsonText = file.readText(Charsets.UTF_8)
        scoreboard = Klaxon().parse<Scoreboard>(jsonText)!!
        println(scoreboard)
    }

    scoreboard.games += game1
    val jsonText = Klaxon()
        .toJsonString(scoreboard)

    val writer = FileWriter(scoreboardFileName)
    try {
        writer.write(jsonText)
        writer.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun getTime(): String {
    return (DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss.SSSD")
            .withZone(ZoneOffset.UTC)
            .format(Instant.now()))
}