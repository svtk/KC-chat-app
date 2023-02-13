package data.remote

import kotlinx.coroutines.flow.*
import com.kcchatapp.model.Event

interface ChatService {
    suspend fun openSession(username: String)

    fun observeEvents(): Flow<Event>

    suspend fun sendEvent(event: Event)

    suspend fun closeSession()

    companion object {
        const val CHAT_HOST = "0.0.0.0"
        const val CHAT_PORT = 9010
        const val CHAT_WS_PATH = "/chat"
    }
}