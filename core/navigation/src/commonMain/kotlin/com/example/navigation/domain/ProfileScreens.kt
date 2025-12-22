package com.example.navigation.domain

sealed class ProfileScreens: Screens {
    class ProfileDetails(
        val name: String,
    ) : ProfileScreens()
}