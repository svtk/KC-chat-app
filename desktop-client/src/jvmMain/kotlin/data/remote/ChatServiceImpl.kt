package data.remote

import com.kcchatapp.model.ChatEvent
import com.kcchatapp.model.UserJoined
import com.kcchatapp.model.UserLeft
import data.remote.ChatService.Companion.CHAT_HOST
import data.remote.ChatService.Companion.CHAT_PORT
import data.remote.ChatService.Companion.CHAT_WS_PATH
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.json.Json

class ChatServiceImpl : ChatService {
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
    }
    private var socket: DefaultClientWebSocketSession? = null
    private lateinit var username: String

    override suspend fun openSession(username: String) {
        try {
            this.username = username
            socket = client.webSocketSession(
                method = HttpMethod.Get,
                host = CHAT_HOST,
                port = CHAT_PORT,
                path = CHAT_WS_PATH
            )
            sendChatEvent(UserJoined(name = username))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun observeChatEvents(): Flow<ChatEvent> {
        return socket
            ?.incoming
            ?.receiveAsFlow()
            ?.mapNotNull {
                socket?.converter?.deserialize<ChatEvent>(it)
            }
            ?: flowOf()
    }

    override suspend fun sendChatEvent(chatEvent: ChatEvent) {
        try {
            socket?.sendSerialized(chatEvent)
        } catch (e: Exception) {
            println("Error while sending: " + e.localizedMessage)
        }
    }

    override suspend fun closeSession() {
        sendChatEvent(UserLeft(name = username))
        socket?.close()
    }
}