package com.example.navigation.transitions

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.navigation3.scene.Scene

/**
 * Defines a vertical slide-out exit transition.
 *
 * The content will slide out of view by animating its vertical offset from its initial
 * position to the full height of the container.
 *
 * @return An [EnterTransition] that slides content in vertically.
 */
fun verticalSlideOut(): ContentTransform =
    EnterTransition.None togetherWith slideOutVertically(
        targetOffsetY = { it }, animationSpec = tween(1000)
    )

/**
 * Defines a vertical slide-in enter transition for a navigation destination.
 * The content will slide in from the bottom of the screen to its final position.
 *
 * @return An [EnterTransition] that slides content in vertically.
 */
fun AnimatedContentTransitionScope<Scene<*>>.verticalSlideIn(): ContentTransform =
    slideInVertically(
        initialOffsetY = { it }, animationSpec = tween(1000)
    ) togetherWith ExitTransition.KeepUntilTransitionsFinished

/**
 * A navigation transition that slides the new screen in from the side horizontally.
 * The old screen remains stationary.

 * @return An `EnterTransition` that slides content in horizontally.
 */
fun horizontalSideIn(reverse: Boolean = false): ContentTransform = slideInHorizontally(
    initialOffsetX = { if (reverse) -it else it }
) togetherWith slideOutHorizontally(
    targetOffsetX = { if (reverse) it else-it }
)
