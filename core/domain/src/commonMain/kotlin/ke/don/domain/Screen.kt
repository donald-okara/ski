package ke.don.domain

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen(
    val label: String,
    val showHeader: Boolean = true,
    val showFooter: Boolean = true,
    val isTitleScreen: Boolean = false,
    val transitionFromPrevious: ScreenTransition = ScreenTransition.Horizontal
){
    @Serializable
    data object Introduction: Screen(
        label = "Introduction",
        showHeader = true,
        showFooter = true,
        isTitleScreen = true,
        transitionFromPrevious = ScreenTransition.Fade
    )

    data object ExampleScreen: Screen(
        label = "Example",
    )

    fun index(): Int {
        return getScreens().indexOf(this)
    }

    companion object {
        fun getScreens(): List<Screen> =
            listOf(
                Introduction,
                ExampleScreen
            )
    }
}

@Serializable
sealed interface ScreenTransition {
    object None : ScreenTransition
    object Fade : ScreenTransition
    object Horizontal : ScreenTransition
    object Vertical : ScreenTransition
}

@Serializable
enum class NavDirection {
    Forward, Backward
}