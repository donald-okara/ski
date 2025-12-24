package com.example.profile.navigation

import androidx.navigation3.ui.NavDisplay
import com.example.navigation.di.navigateBackOrStart
import com.example.navigation.di.navigationModuleHelper
import com.example.navigation.domain.ProfileScreens
import com.example.navigation.transitions.verticalSlideIn
import com.example.navigation.transitions.verticalSlideOut
import com.example.profile.screens.ProfileScreen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val profileNavigationModule = navigationModuleHelper {
    navigation<ProfileScreens.ProfileDetails>(metadata = NavDisplay.transitionSpec {
        verticalSlideIn()
    } + NavDisplay.popTransitionSpec {
        verticalSlideOut()
    } + NavDisplay.predictivePopTransitionSpec {
        verticalSlideOut()
    }) { entry ->
        ProfileScreen(
            name = entry.name, onBack = {
                navigateBackOrStart()
            })
    }
}

