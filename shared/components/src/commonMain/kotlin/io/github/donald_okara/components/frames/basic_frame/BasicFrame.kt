package io.github.donald_okara.components.frames.basic_frame

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import io.github.donald_okara.components.frames.SkiFrame
import io.github.donald_okara.components.values.Values

class BasicFrame(
    private val curve: Dp = Values.cornerRadius,
    val opacity: Float = 0.5f
): SkiFrame {
    @Composable
    override fun Render(
        modifier: Modifier,
        header: @Composable (() -> Unit)?,
        footer: @Composable (() -> Unit)?,
        content: @Composable (BoxScope.() -> Unit)
    ) {
        BasicFrameComponent(
            modifier = modifier,
            curve = curve,
            header = header,
            opacity = opacity,
            footer = footer,
            content = content
        )
    }
}

@Composable
fun BasicFrameComponent(
    modifier: Modifier = Modifier,
    curve: Dp = Values.cornerRadius,
    opacity: Float,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Values.Dimens.mediumPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        header?.invoke()
        CurvedBox(
            modifier = Modifier.weight(1f),
            curve = curve,
            content = content,
            opacity = opacity
        )
        footer?.invoke()
    }
}

@Composable
private fun CurvedBox(
    modifier: Modifier = Modifier,
    thickness: Dp = Values.lineThickness,
    opacity: Float,
    curve: Dp = Values.cornerRadius,
    content: @Composable BoxScope.() -> Unit
){
    Surface(
        border = BorderStroke(
            width = thickness,
            color = MaterialTheme.colorScheme.onSurface
        ),
        color = MaterialTheme.colorScheme.surface.copy(opacity),
        shape = RoundedCornerShape(curve),
        modifier = modifier
            .fillMaxSize()
    ){
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ){
            content()
        }
    }
}