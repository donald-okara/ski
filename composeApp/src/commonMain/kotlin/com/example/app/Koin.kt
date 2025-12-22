package com.example.app

import com.example.chat.navigation.chatNavigationModule
import com.example.navigation.di.navigationModule
import com.example.profile.navigation.profileNavigationModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            //Navigation
            profileNavigationModule, chatNavigationModule,
        )
    }
}