package io.github.donald_okara.components.guides.whiteboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class WhiteboardTheme(
    val background: Color,
    val normal: Color,
    val link: Color,
)

object WhiteboardThemes {

    val Dark = WhiteboardTheme(
        background = Color(0xFF1E1E1E),
        normal = Color(0xFFD4D4D4),
        link = Color(0xFF569CD6),
    )

    val Light = WhiteboardTheme(
        background = Color(0xFFF5F5F5),
        normal = Color(0xFF1E1E1E),
        link = Color(0xFF0000FF),
    )
}

@Composable
fun animateWhiteboardTheme(target: WhiteboardTheme): WhiteboardTheme {
    return WhiteboardTheme(
        background = animateColorAsState(target.background, label = "bg").value,
        normal = animateColorAsState(target.normal, label = "normal").value,
        link = animateColorAsState(target.link, label = "keyword").value,
    )
}
