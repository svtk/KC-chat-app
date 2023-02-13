package com.kcchatapp.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface ChatEvent {
    val persistent: Boolean get() = true
}

@Serializable
data class MessageSent(val message: Message): ChatEvent

@Serializable
sealed interface UserEvent: ChatEvent {
    val name: String
}

@Serializable
data class UserJoined(override val name: String): UserEvent

@Serializable
data class UserLeft(override val name: String): UserEvent

@Serializable
data class UserIsTyping(
    override val name: String,
    override val persistent: Boolean = false
): UserEvent