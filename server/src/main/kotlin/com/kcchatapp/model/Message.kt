package model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val text: String,
    val username: String,
    val timestamp: Instant = Clock.System.now(),
)