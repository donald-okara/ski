package ke.don.ski.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ke.don.design.theme.dimens
import ke.don.domain.NavDirection
import ke.don.domain.ScreenTransition
import ke.don.ski.domain.SlideConfig
import ke.don.ski.navigation.DeckNavigator

@Composable
fun DeckHost(
    slides: List<SlideConfig>,
    navigator: DeckNavigator = remember { DeckNavigator(slides) },
    modifier: Modifier = Modifier
) {
    val slide = navigator.currentSlide

    AnimatedContent(
        targetState = navigator.currentIndex,
        transitionSpec = {
            transitionFor(
                slide = slide,
                direction = navigator.direction
            )
        },
        modifier = modifier
            .padding(MaterialTheme.dimens.mediumPadding)
            .fillMaxSize()
    ) { index ->
        slides[index].content()
    }
}

fun transitionFor(
    slide: SlideConfig,
    direction: NavDirection,
): ContentTransform {
    val duration = 500
    return when (slide.transition) {

        ScreenTransition.None ->
            EnterTransition.None togetherWith ExitTransition.None

        ScreenTransition.Fade ->
            fadeIn(
                animationSpec = tween(durationMillis = duration)
            ) togetherWith fadeOut(
                animationSpec = tween(durationMillis = duration)
            )

        ScreenTransition.Horizontal ->
            if (direction == NavDirection.Forward) {
                slideInHorizontally(
                    animationSpec = tween(durationMillis = duration)
                ) { it } togetherWith ExitTransition.None
            } else {
                slideInHorizontally(
                    animationSpec = tween(durationMillis = duration)
                ) { -it } togetherWith ExitTransition.None
            }

        ScreenTransition.Vertical ->
            if (direction == NavDirection.Forward) {
                slideInVertically(
                    animationSpec = tween(durationMillis = duration)
                ) { it } togetherWith ExitTransition.None
            } else {
                slideInVertically(
                    animationSpec = tween(durationMillis = duration)
                ) { -it } togetherWith ExitTransition.None
            }
    }
}
