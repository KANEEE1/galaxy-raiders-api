package galaxyraiders.core.score

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class GameScore(val finalScore: Double, val asteroidsDestroyed: Int) {
    val timestamp: String =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSD")
                    .withZone(ZoneOffset.UTC)
                    .format(Instant.now())
}
