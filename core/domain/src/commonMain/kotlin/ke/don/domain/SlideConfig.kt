package ke.don.domain

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import ke.don.domain.frames.SkiFrame
import ke.don.domain.timer.TimerController
import ke.don.domain.values.Values
import kotlinx.serialization.Serializable

data class SlideConfig(
    val label: String,
    val notes: List<AnnotatedString>? = null,
    val transition: ScreenTransition = ScreenTransition.Horizontal,
    val timer: TimerController,
    val frame: (@Composable () -> SkiFrame?)?,
    val header: (@Composable () -> Unit)? = null,
    val footer: (@Composable () -> Unit)? = null,
    val content: @Composable () -> Unit,
) {
    @Composable
    fun Render(modifier: Modifier = Modifier) {
        frame?.invoke()?.Render(
            modifier = modifier,
            header = header,
            footer = footer
        ) {
            Box(
                modifier = Modifier
                    .padding(
                        horizontal = Values.Dimens.mediumPadding
                    )
            ) { content() }
        } ?: content()
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
