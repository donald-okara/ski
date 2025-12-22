package com.example.chat.navigation

import com.example.chat.screens.ChatDetailsScreen
import com.example.chat.screens.ChatListScreen
import com.example.navigation.di.navigateBack
import com.example.navigation.di.navigateToScreen
import com.example.navigation.di.navigationModuleHelper
import com.example.navigation.domain.ChatScreens
import com.example.navigation.domain.ProfileScreens
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.navigation3.navigation


@OptIn(KoinExperimentalAPI::class)
val chatNavigationModule = navigationModuleHelper{
    navigation<ChatScreens.ChatList> { entry ->
        ChatListScreen(
            onChatItemClick = { (chatId, name) ->
                navigateToScreen(ChatScreens.ChatDetails(chatId, name))
            }
        )
    }

    navigation<ChatScreens.ChatDetails> { entry ->
        ChatDetailsScreen(
            profileName = entry.name,
            onProfileClick = { name ->
                navigateToScreen(
                    ProfileScreens.ProfileDetails(name)
                )
            },
            onBack = { navigateBack() }
        )
    }
}