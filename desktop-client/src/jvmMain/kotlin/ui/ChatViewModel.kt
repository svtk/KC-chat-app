package ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.kcchatapp.model.*
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
    private val _typingUserEvents: MutableStateFlow<Set<TypingEvent>> = MutableStateFlow(setOf())
    val typingUsers: Flow<Set<String>>
        get() = _typingUserEvents
            .map { it.map(TypingEvent::username).toSet() }

    fun connectToChat(username: String) {
        _username.value = username
        scope.launch {
            chatService.openSession(username)
            chatService.sendEvent(UserEvent(username = username, statusChange = UserStatusChange.USER_JOINED))
            chatService.observeEvents()
                .onEach { event ->
                    when (event) {
                        is ChatEvent -> {
                            _eventFlow.value = listOf(event) + _eventFlow.value
                            mutex.withLock {
                                _typingUserEvents.value = _typingUserEvents.value
                                    .filter { it.username != event.username }.toSet()
                            }
                        }
                        is TypingEvent -> {
                            mutex.withLock {
                                _typingUserEvents.value = _typingUserEvents.value + event
                            }
                            scope.launch {
                                delay(5000)
                                mutex.withLock {
                                    _typingUserEvents.value = _typingUserEvents.value - event
                                }
                            }
                        }
                    }
                }
                .launchIn(scope)
        }
    }

    fun disconnect() {
        scope.launch {
            username.value?.let { name ->
                chatService.sendEvent(UserEvent(username = name, statusChange = UserStatusChange.USER_LEFT))
            }
            chatService.closeSession()
        }
    }

    fun sendMessage(message: String) {
        scope.launch {
            username.value?.let { name ->
                chatService.sendEvent(MessageEvent(username = name, Message(text = message)))
            }
        }
    }

    fun startTyping() {
        scope.launch {
            username.value?.let { name ->
                chatService.sendEvent(TypingEvent(username = name))
            }
        }
    }
}