package io.github.donald_okara.components.guides.code_viewer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import io.github.donald_okara.components.icon.IconButtonToken

@Composable
fun KotlinCodeViewer(
    modifier: Modifier = Modifier,
    darkTheme: Boolean = true,
    toggleFocus: () -> Unit = {},
    toggleTheme: () -> Unit = {},
    code: () -> String
) {
    val targetTheme = if (darkTheme) {
        CodeThemes.Dark
    } else {
        CodeThemes.Light
    }

    val colorScheme = animateCodeTheme(targetTheme)

    val source = remember { code() }

    val highlighted = remember(source, colorScheme) {
        highlightKotlin(source, colorScheme)
    }

    Surface(
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 1.dp,
        color = colorScheme.background,
        modifier = modifier
            .padding(vertical = 4.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ){
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButtonToken(
                    icon = Icons.Default.Fullscreen,
                    accentColor = if (darkTheme){
                        Color.White
                    } else {
                        Color.Black
                    },
                    sizeInt = 48,
                    onClick = toggleFocus
                )

                IconButtonToken(
                    icon = if (darkTheme) {
                        Icons.Outlined.LightMode
                    } else {
                        Icons.Outlined.DarkMode
                    },
                    accentColor = if (darkTheme){
                        Color.White
                    } else {
                        Color.Black
                    },
                    sizeInt = 48,
                    onClick = toggleTheme
                )

            }
            Text(
                text = highlighted,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(12.dp),
            )
        }
    }
}


fun highlightKotlin(
    code: String,
    theme: CodeTheme
): AnnotatedString {
    val builder = AnnotatedString.Builder()

    val keywords = listOf(
        "val","var","fun","if","else","for","while","when","return",
        "class","object","interface","package","import","override",
        "private","public","protected","internal","sealed","data",
        "companion","enum","do","break","continue"
    )

    val keywordPattern = "\\b(${keywords.joinToString("|")})\\b".toRegex()
    val typePattern = "\\b([A-Z][A-Za-z0-9_]*)\\b".toRegex()
    val stringPattern = "\".*?\"".toRegex()
    val commentPattern = "//.*|/\\*(.|\\R)*?\\*/".toRegex()
    val annotationPattern = "@[A-Za-z0-9_]+".toRegex()

    val matches = buildList {
        addAll(keywordPattern.findAll(code))
        addAll(stringPattern.findAll(code))
        addAll(commentPattern.findAll(code))
        addAll(annotationPattern.findAll(code))
        addAll(typePattern.findAll(code))
    }.sortedBy { it.range.first }
        .fold(mutableListOf<MatchResult>()) { acc, match ->
            if (acc.none { it.range.overlaps(match.range) }) acc.add(match)
            acc
        }

    var index = 0

    for (match in matches) {
        if (match.range.first > index) {
            builder.withStyle(SpanStyle(color = theme.normal)) {
                append(code.substring(index, match.range.first))
            }
        }

        val color = when {
            commentPattern.matches(match.value) -> theme.comment
            stringPattern.matches(match.value) -> theme.string
            annotationPattern.matches(match.value) -> theme.annotation
            keywordPattern.matches(match.value) -> theme.keyword
            typePattern.matches(match.value) -> theme.type
            else -> theme.normal
        }

        builder.withStyle(SpanStyle(color = color)) {
            append(match.value)
        }

        index = match.range.last + 1
    }

    if (index < code.length) {
        builder.withStyle(SpanStyle(color = theme.normal)) {
            append(code.substring(index))
        }
    }

    return builder.toAnnotatedString()
}


private fun IntRange.overlaps(other: IntRange): Boolean =
    this.first <= other.last && other.first <= this.last