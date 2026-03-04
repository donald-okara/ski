package ke.don.ski.domain

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import io.github.donald_okara.components.frames.SkiFrame
import io.github.donald_okara.components.frames.defaultSkiFrames
import io.github.donald_okara.components.timer.TimerController
import io.github.donald_okara.components.values.Values
import ke.don.design.theme.dimens
import ke.don.domain.ScreenTransition
import ke.don.ski.SlidesConstants.FRAME_OPACITY
import ke.don.ski.presentation.ui.MainFooter
import ke.don.ski.presentation.ui.MainHeader

data class SlideConfig(
    val label: String,
    val notes: List<AnnotatedString>? = null,
    val transition: ScreenTransition = ScreenTransition.Horizontal,
    val timer: TimerController,
    val showHeader: Boolean = true,
    val frame: (@Composable () -> SkiFrame)?,
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
                        horizontal = MaterialTheme.dimens.mediumPadding
                    )
            ) { content() }
        } ?: content()
    }
}
