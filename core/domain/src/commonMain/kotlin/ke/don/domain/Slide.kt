package ke.don.domain

import kotlinx.serialization.Serializable

@Serializable
sealed class Slide(
    val label: String,
    val showHeader: Boolean = true,
    val showFooter: Boolean = true,
    val isTitleScreen: Boolean = false,
    val transitionFromPrevious: ScreenTransition = ScreenTransition.Horizontal
){
    @Serializable
    data object Introduction: Slide(
        label = "Introduction",
        showHeader = true,
        showFooter = true,
        isTitleScreen = true,
        transitionFromPrevious = ScreenTransition.Fade
    )

    data object ExampleScreen: Slide(
        label = "Example",
    )

    data object KodeViewer: Slide(
        label = "Kode Viewer",
    )

    data object HorizontalSegmentsDemo: Slide(
        label = "Horizontal Segments Demo",
    )

    data object VerticalSegmentsDemo: Slide(
        label = "Vertical Segments Demo",
    )

    fun index(): Int {
        return getScreens().indexOf(this)
    }

    companion object {
        fun getScreens(): List<Slide> =
            listOf(
                Introduction,
                ExampleScreen,
                KodeViewer,
                VerticalSegmentsDemo,
                HorizontalSegmentsDemo
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