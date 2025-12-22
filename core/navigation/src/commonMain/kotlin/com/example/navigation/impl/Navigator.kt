package com.example.navigation.impl

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.navigation.domain.ChatScreens
import com.example.navigation.domain.Screens

/**
 * Manages the navigation state and back stack for the application.
 *
 * This class is responsible for handling screen transitions, such as navigating to a new screen,
 * going back to the previous one, and replacing the current screen. It maintains a
 * [backStack] of [com.example.navigation.domain.Screens] to keep track of the user's navigation history.
 *
 * @property startDestination The initial screen that should be displayed when the navigation flow starts.
 *
 */
class Navigator() {
    private val startDestination: Screens = ChatScreens.ChatList

    /**
     * The current stack of screens being displayed.
     *
     * This is a read-only snapshot of the navigation back stack. The last element in the list
     * represents the current, topmost screen visible to the user.
     * Observing this list allows UI components to react to navigation changes.
     */
    val backStack : SnapshotStateList<Screens> = mutableStateListOf(startDestination)

    /**
     * Navigates to the specified screen.
     *
     * This function is responsible for handling the navigation logic to display a new screen.
     * It should update the navigation state, typically by adding the new screen to the
     * back stack or replacing the current screen.
     *
     * @param screen The [Screens] to navigate to. This object contains all the necessary
     * information to render the destination screen.
     */
    fun navigateToScreen(screen: Screens){
        backStack.add(screen)
    }
    /**
     * Navigates to the previous screen in the back stack.
     *
     * @return `true` if the navigation was successful (i.e., there was a screen to go back to),
     *         `false` otherwise.
     */
    fun back(){
        backStack.removeLastOrNull()
    }
    /**
     * Navigates back to the previous screen in the back stack.
     * If the back stack is empty or contains only one entry, it navigates to the start destination,
     * effectively resetting the navigation flow. This is useful for handling up-navigation from a top-level screen.
     */
    fun backOrStart(){
        if(backStack.size > 1){
            back()
        }else{
            navigateToScreen(startDestination)
        }
    }
}