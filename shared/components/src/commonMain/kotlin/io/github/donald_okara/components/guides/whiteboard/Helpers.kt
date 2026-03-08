package io.github.donald_okara.components.guides.whiteboard

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle

fun highlightWhiteboard(
    text: String,
    theme: WhiteboardTheme
): AnnotatedString {
    val builder = AnnotatedString.Builder()

    val urlPattern = "(https?://[\\w./?=&-]+)".toRegex()
    val boldPattern = "\\*\\*(.*?)\\*\\*".toRegex()
    val italicPattern = "_(.*?)_".toRegex()
    val strikePattern = "~~(.*?)~~".toRegex()

    val matches = buildList {
        addAll(urlPattern.findAll(text))
        addAll(boldPattern.findAll(text))
        addAll(italicPattern.findAll(text))
        addAll(strikePattern.findAll(text))
    }.sortedBy { it.range.first }
        .fold(mutableListOf<MatchResult>()) { acc, match ->
            if (acc.none { it.range.overlaps(match.range) }) acc.add(match)
            acc
        }

    var index = 0

    for (match in matches) {
        if (match.range.first > index) {
            builder.withStyle(SpanStyle(color = theme.normal)) {
                append(text.substring(index, match.range.first))
            }
        }

        val style = when {
            urlPattern.matches(match.value) -> SpanStyle(color = theme.link)
            boldPattern.matches(match.value) -> SpanStyle(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            italicPattern.matches(match.value) -> SpanStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            strikePattern.matches(match.value) -> SpanStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
            else -> SpanStyle(color = theme.normal)
        }

        builder.withStyle(style) {
            // For Markdown-like patterns, we might want to strip the symbols
            val content = when {
                boldPattern.matches(match.value) -> match.groupValues[1]
                italicPattern.matches(match.value) -> match.groupValues[1]
                strikePattern.matches(match.value) -> match.groupValues[1]
                else -> match.value
            }
            append(content)
        }

        index = match.range.last + 1
    }

    if (index < text.length) {
        builder.withStyle(SpanStyle(color = theme.normal)) {
            append(text.substring(index))
        }
    }

    return builder.toAnnotatedString()
}

fun IntRange.overlaps(other: IntRange): Boolean =
    this.first <= other.last && other.first <= this.last
