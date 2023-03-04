package model

import androidx.compose.runtime.Immutable
import com.kcchatapp.model.MessageEvent
import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Immutable
@Serializable
data class Message(
    val username: String,
    val text: String,
    val localDateTime: LocalDateTime
)

val MessageEvent.message
    get() = Message(
        username = username,
        text = messageText,
        localDateTime = timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
    )

fun Message.timeText(): String {
    val time = localDateTime.run { LocalTime(hour, minute) }
    val date = localDateTime.date
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    return if (date == today) "$time" else "$date $time"
}

fun Message.elapsedText(moment: Instant = Clock.System.now()): String {
    val elapsed = moment - localDateTime.toInstant(TimeZone.currentSystemDefault())
    val text = "${
        elapsed.toComponents { days, hours, minutes, seconds, _ ->
            when {
                elapsed < 1.minutes -> "${seconds}s"
                elapsed < 1.hours -> "${minutes}m ${seconds}s"
                elapsed < 1.days -> "${hours}h ${minutes}m"
                else -> "${days}d ${hours}h ${minutes}m"
            }
        }
    } ago"

    return text
}