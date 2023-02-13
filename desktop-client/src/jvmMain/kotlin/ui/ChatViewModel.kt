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

    private val typingEventsMutex = Mutex()
    private val _typingEvents: MutableStateFlow<Set<TypingEvent>> = MutableStateFlow(setOf())
    val typingUsers: Flow<Set<String>>
        get() = _typingEvents
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
                            _eventFlow.update { listOf(event) + it }
                            typingEventsMutex.withLock {
                                _typingEvents.update { events ->
                                    events.filter { it.username != event.username }.toSet()
                                }
                            }
                        }
                        is TypingEvent -> {
                            typingEventsMutex.withLock {
                                if (event.username != username) {
                                    _typingEvents.update { it + event }
                                }
                            }
                            scope.launch {
                                delay(3000)
                                typingEventsMutex.withLock {
                                    _typingEvents.update { it - event }
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