package com.example.app.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.app.navigation.ContainerState
import com.example.components.frames.AnimatedSnakeFramedCard
import com.example.navigation.domain.NavDirection
import com.example.navigation.domain.Screen
import com.example.navigation.domain.ScreenTransition

@Composable
fun MainContainer(
    state: ContainerState,
    modifier: Modifier = Modifier,
    content: @Composable (Screen) -> Unit
) {
    AnimatedSnakeFramedCard(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        header = if(state.screen.showHeader) {
            {
                Row(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "🎉",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        "DevFest",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "🎉",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        } else null,
        footer = if(state.screen.showFooter) {
            {
                Row(
                    modifier = Modifier.background(
                            MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp)
                        ).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🎯", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        state.screen.label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else null
    ) {
        LookaheadScope{
            AnimatedContent(
                targetState = state.screen,
                transitionSpec = {
                    transitionFor(
                        screen = targetState,
                        direction = state.direction
                    )
                },
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) { screen ->
                content(screen)
            }
        }
    }

}

private fun transitionFor(
    screen: Screen,
    direction: NavDirection
): ContentTransform {
    val duration = 500
    return when (screen.transitionFromPrevious) {

        ScreenTransition.None ->
            EnterTransition.None togetherWith  ExitTransition.None

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
