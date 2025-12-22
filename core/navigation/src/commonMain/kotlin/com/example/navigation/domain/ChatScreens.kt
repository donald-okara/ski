package com.example.navigation.domain

sealed class ChatScreens: Screens {
    object ChatList : ChatScreens()

    class ChatDetails(
        val chatId: String,
        val name: String,
    ) : ChatScreens()
}