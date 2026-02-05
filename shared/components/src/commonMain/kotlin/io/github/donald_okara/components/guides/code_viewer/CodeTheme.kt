package io.github.donald_okara.components.guides.code_viewer

import androidx.compose.animation.animateColorAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class CodeTheme(
    val background: Color,
    val normal: Color,
    val keyword: Color,
    val type: Color,
    val string: Color,
    val comment: Color,
    val annotation: Color,
)

object CodeThemes {

    val Dark = CodeTheme(
        background = Color(0xFF1E1E1E),
        normal = Color(0xFFD4D4D4),
        keyword = Color(0xFF569CD6),
        type = Color(0xFF4EC9B0),
        string = Color(0xFFD69D85),
        comment = Color(0xFF6A9955),
        annotation = Color(0xFFDCDCAA),
    )

    val Light = CodeTheme(
        background = Color(0xFFF5F5F5),
        normal = Color(0xFF1E1E1E),
        keyword = Color(0xFF0000FF),
        type = Color(0xFF267F99),
        string = Color(0xFFA31515),
        comment = Color(0xFF008000),
        annotation = Color(0xFF795E26),
    )
}

@Composable
fun animateCodeTheme(target: CodeTheme): CodeTheme {
    return CodeTheme(
        background = animateColorAsState(target.background, label = "bg").value,
        normal = animateColorAsState(target.normal, label = "normal").value,
        keyword = animateColorAsState(target.keyword, label = "keyword").value,
        type = animateColorAsState(target.type, label = "type").value,
        string = animateColorAsState(target.string, label = "string").value,
        comment = animateColorAsState(target.comment, label = "comment").value,
        annotation = animateColorAsState(target.annotation, label = "annotation").value,
    )
}
