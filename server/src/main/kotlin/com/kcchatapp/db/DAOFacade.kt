package com.kcchatapp.db

import com.kcchatapp.model.MessageEvent

interface DAOFacade {
    suspend fun saveMessageEvent(messageEvent: MessageEvent)

    val messageEvents: List<MessageEvent>
}