package ui

import kotlinx.datetime.*
import model.Message

fun Message.timeText(): String {
    val localDateTime = timestamp
        .toLocalDateTime(TimeZone.currentSystemDefault())
    val time = localDateTime.run { LocalTime(hour, minute) }
    val date = localDateTime.date
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    return if (date == today) "$time" else "$date $time"
}