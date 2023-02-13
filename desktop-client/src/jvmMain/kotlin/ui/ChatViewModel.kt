package ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.kcchatapp.model.ChatEvent
import com.kcchatapp.model.Message
import com.kcchatapp.model.MessageSent
import com.kcchatapp.model.UserIsTyping
import data.remote.ChatService
import data.remote.ChatServiceImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ChatViewModel(
    private val scope: CoroutineScope
) {
    private val chatService: ChatService = ChatServiceImpl()
    private var _username = mutableStateOf<String?>(null)
    val username: State<String?> = _username

    private val _eventFlow: MutableStateFlow<List<ChatEvent>> = MutableStateFlow(listOf())
    val eventFlow: StateFlow<List<ChatEvent>> get() = _eventFlow

    private val mutex = Mutex()
    private val _typingUserEvents: MutableStateFlow<Set<UserIsTyping>> = MutableStateFlow(setOf())
    val typingUsers: Flow<Set<String>>
        get() = _typingUserEvents
            .map { it.map(UserIsTyping::name).toSet() }

    fun connectToChat(username: String) {
        _username.value = username
        scope.launch {
            chatService.openSession(username)
            chatService.observeChatEvents()
                .onEach { chatEvent ->
                    if (chatEvent is UserIsTyping) {
                        mutex.withLock {
                            _typingUserEvents.value = _typingUserEvents.value + chatEvent
                        }
                        scope.launch {
                            delay(5000)
                            mutex.withLock {
                                _typingUserEvents.value = _typingUserEvents.value - chatEvent
                            }
                        }
                    } else {
                        _eventFlow.value = listOf(chatEvent) + _eventFlow.value
                    }
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
            chatService.sendChatEvent(MessageSent(Message(text = message, username = username.value!!)))
        }
    }

    fun startTyping() {
        scope.launch {
            chatService.sendChatEvent(UserIsTyping(name = username.value!!))
        }
    }
}