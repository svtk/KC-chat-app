package com.kcchatapp.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
sealed interface Event {
    val username: String
}

@Serializable
sealed interface ChatEvent: Event

@Serializable
data class MessageEvent(
    override val username: String,
    val message: Message,
): ChatEvent

@Serializable
data class Message(
    val text: String,
    val timestamp: Instant = Clock.System.now(),
)

@Serializable
data class UserEvent(
    override val username: String,
    val statusChange: UserStatusChange
): ChatEvent

enum class UserStatusChange { USER_JOINED, USER_LEFT }

@Serializable
data class TypingEvent(override val username: String): Event