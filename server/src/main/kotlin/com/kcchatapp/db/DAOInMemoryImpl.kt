package com.kcchatapp.db

import com.kcchatapp.model.ChatEvent
import java.util.*

class DAOInMemoryImpl: DAOFacade {
    private val _chatEvents = Collections.synchronizedList(mutableListOf<ChatEvent>())

    override suspend fun saveChatEvent(chatEvent: ChatEvent) {
        _chatEvents += chatEvent
    }

    override val chatEvents: List<ChatEvent>
        get() = _chatEvents
}