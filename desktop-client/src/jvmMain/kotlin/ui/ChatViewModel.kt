package ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import data.remote.ChatService
import data.remote.ChatServiceImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import model.*

class ChatViewModel(
    private val scope: CoroutineScope
) {
    private val chatService: ChatService = ChatServiceImpl()
    private var _username = mutableStateOf<String?>(null)
    val username: State<String?> = _username

    private val _eventFlow: MutableStateFlow<List<ChatEvent>> = MutableStateFlow(listOf())
    val eventFlow: StateFlow<List<ChatEvent>> get() = _eventFlow

    fun connectToChat(username: String) {
        _username.value = username
        scope.launch {
            chatService.openSession(username)
            chatService.observeChatEvents()
                .onEach { chatEvent ->
                    _eventFlow.value = listOf(chatEvent) + _eventFlow.value
                }
                .launchIn(scope)
        }
    }

    fun disconnect() {
        scope.launch {
            chatService.closeSession()
        }
    }

    fun sendMessage(message: String) {
        scope.launch {
            chatService.sendMessage(message)
        }
    }
}