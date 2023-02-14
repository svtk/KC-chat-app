package com.kcchatapp.db

import com.kcchatapp.model.MessageEvent
import java.util.*

class DAOInMemoryImpl: DAOFacade {
    private val _messageEvents = Collections.synchronizedList(mutableListOf<MessageEvent>())

    override suspend fun saveMessageEvent(messageEvent: MessageEvent) {
        _messageEvents += messageEvent
    }

    override val messageEvents: List<MessageEvent>
        get() = _messageEvents
}