@file:Suppress("unused")
package model

import kotlinx.serialization.Serializable

@Serializable
sealed interface ChatEvent

@Serializable
data class MessageSent(val message: Message): ChatEvent

@Serializable
sealed interface UserEvent: ChatEvent

@Serializable
data class UserJoined(val name: String): UserEvent

@Serializable
data class UserLeft(val name: String): UserEvent