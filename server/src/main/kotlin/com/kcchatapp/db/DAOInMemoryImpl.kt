package com.kcchatapp.db

import com.kcchatapp.model.ChatEvent
import com.kcchatapp.model.TypingEvent
import java.util.*

class DAOInMemoryImpl: DAOFacade {
    private val _messageEvents =
        Collections.synchronizedList(mutableListOf<ChatEvent>())

    override suspend fun saveChatEvent(chatEvent: ChatEvent) {
        if (chatEvent is TypingEvent) return
        _messageEvents += chatEvent
    }

    override val chatEvents: List<ChatEvent>
        get() = _messageEvents
}