package model

import androidx.compose.runtime.Immutable
import com.kcchatapp.model.MessageEvent
import kotlinx.datetime.*
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Message(
    val username: String,
    val text: String,
    val localDateTime: LocalDateTime
)

fun MessageEvent.toMessage() =
    Message(
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