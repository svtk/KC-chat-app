package data.remote

import kotlinx.coroutines.flow.*
import model.ChatEvent

interface ChatService {
    suspend fun openSession(username: String)
    fun observeChatEvents(): Flow<ChatEvent>

    suspend fun sendMessage(message: String)

    suspend fun closeSession()

    companion object {
        const val CHAT_HOST = "0.0.0.0"
        const val CHAT_PORT = 9010
        const val CHAT_WS_PATH = "/chat"
    }
}