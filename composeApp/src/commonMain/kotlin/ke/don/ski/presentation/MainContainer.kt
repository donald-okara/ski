package ke.don.ski.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.donald_okara.components.frames.SkiFrame
import io.github.donald_okara.components.values.Values
import ke.don.domain.NavDirection
import ke.don.domain.Slide
import ke.don.domain.ScreenTransition
import ke.don.ski.navigation.ContainerState

/**
 * Renders the presentation container with optional header and footer and animates slide changes.
 *
 * @param state Provides the current slide and navigation direction used to drive content and transitions.
 * @param frame Frame implementation that supplies the surrounding layout and rendering surface.
 * @param mode Determines header content and styling (used by the default header).
 * @param header Optional composable to display in the header area; by default uses `mainHeader(state, mode)` when the current slide requests a header.
 * @param footer Optional composable to display in the footer area; by default uses `mainFooter(state)` when the current slide requests a footer.
 * @param modifier Modifier applied to the container.
 * @param content Composable lambda that renders the active `Slide`.
 */
@Composable
fun MainContainer(
    state: ContainerState,
    frame: SkiFrame,
    mode: DeckMode,
    header: (@Composable () -> Unit)? = mainHeader(state, mode),
    footer: (@Composable () -> Unit)? = mainFooter(state),
    modifier: Modifier = Modifier,
    content: @Composable (Slide) -> Unit,
) {
    frame.Render(
        modifier = modifier
            .padding(16.dp),
        header = header,
        footer = footer,
    ) {
        LookaheadScope {
            AnimatedContent(
                targetState = state.slide,
                transitionSpec = {
                    transitionFor(
                        slide = targetState,
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

/**
     * Provides a footer composable for the current slide when the slide requests a footer.
     *
     * @param state The container state containing the current slide and navigation direction.
     * @return A composable lambda that renders the slide footer, or `null` if the current slide's footer is hidden.
     */
    private fun mainFooter(state: ContainerState): @Composable (() -> Unit)? =
    if (state.slide.showFooter) {
        {
            Row(
                modifier = Modifier
                    .animateContentSize()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(Values.cornerRadius)
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            CircleShape
                        )
                )

                Spacer(Modifier.width(10.dp))

                AnimatedContent(
                    targetState = state.slide.label,
                    transitionSpec = {
                        transitionFor(
                            slide = state.slide,
                            direction = state.direction
                        )
                    },
                    label = "text-change"
                ) { value ->
                    Text(
                        value,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    } else null


/**
     * Create a header composable for the current slide when the slide requests a header.
     *
     * The rendered header shows a short title ("Ski") and a mode-dependent subtitle:
     * - If `mode` is `DeckMode.Presenter`, subtitle is "Presentation Demo" with `onSurfaceVariant` color.
     * - Otherwise, subtitle is "Presenter's panel (Do not present)" with the `error` color.
     *
     * @param state The container state containing the current slide and related display flags.
     * @param mode The deck mode used to decide subtitle text and color.
     * @return A composable lambda that renders the header when `state.slide.showHeader` is `true`, or `null` when no header should be shown.
     */
    private fun mainHeader(state: ContainerState, mode: DeckMode): @Composable (() -> Unit)? =
    if (state.slide.showHeader) {
        {
            Row(
                modifier = Modifier
                    .animateContentSize()
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(Values.cornerRadius)
                    )
                    .border(
                        Values.lineThickness,
                        MaterialTheme.colorScheme.onSurface,
                        RoundedCornerShape(Values.cornerRadius)
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Accent rail
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(40.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(2.dp)
                        )
                )

                Spacer(Modifier.width(16.dp))

                Column {
                    Text(
                        "Ski",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        if (mode == DeckMode.Presenter) "Presentation Demo" else "Presenter's panel (Do not present)",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (mode == DeckMode.Presenter) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    } else null


fun transitionFor(
    slide: Slide,
    direction: NavDirection,
): ContentTransform {
    val duration = 500
    return when (slide.transitionFromPrevious) {

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