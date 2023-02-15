package ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.kcchatapp.model.MessageEvent
import com.kcchatapp.model.TypingEvent
import data.remote.ChatService
import data.remote.ChatServiceImpl
import kotlinx.collections.immutable.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import model.Message
import model.toMessage

class ChatViewModel(
    private val scope: CoroutineScope
) {
    private val chatService: ChatService = ChatServiceImpl()
    private var _username = mutableStateOf<String?>(null)
    val username: State<String?> = _username

    private val _eventFlow: MutableStateFlow<PersistentList<Message>> = MutableStateFlow(persistentListOf())
    val messagesFlow: StateFlow<ImmutableList<Message>> get() = _eventFlow

    private val _typingEvents: MutableStateFlow<Set<TypingEvent>> = MutableStateFlow(setOf())
    val typingUsers: Flow<ImmutableSet<String>>
        get() = _typingEvents
            .map { it.map(TypingEvent::username).toImmutableSet() }

    fun connectToChat(username: String) {
        _username.value = username
        scope.launch {
            chatService.openSession(username)
            chatService.observeEvents()
                .onEach { event ->
                    when (event) {
                        is MessageEvent -> {
                            _eventFlow.update { persistentListOf(event.toMessage()) + it }
                            _typingEvents.update { events ->
                                events.filter { it.username != event.username }.toSet()
                            }
                        }

                        is TypingEvent -> {
                            if (event.username != username) {
                                _typingEvents.update { it + event }
                            }
                            scope.launch {
                                delay(3000)
                                _typingEvents.update { it - event }
                            }
                        }
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
            username.value?.let { name ->
                chatService.sendEvent(
                    MessageEvent(username = name, messageText = message, timestamp = Clock.System.now())
                )
            }
        }
    }

    fun startTyping() {
        scope.launch {
            username.value?.let { name ->
                chatService.sendEvent(
                    TypingEvent(username = name, timestamp = Clock.System.now())
                )
            }
        }
    }
}