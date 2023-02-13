package com.kcchatapp.db

import com.kcchatapp.model.ChatEvent

interface DAOFacade {
    suspend fun saveChatEvent(chatEvent: ChatEvent)

    val chatEvents: List<ChatEvent>
}