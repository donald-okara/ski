package io.github.donald_okara.components.guides.code_viewer

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle

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
    val commentPattern = "//.*|/\\*(.|\\n|\\r)*?\\*/".toRegex()
    val annotationPattern = "@[A-Za-z0-9_]+".toRegex()

    val matches = buildList {
        addAll(stringPattern.findAll(code))
        addAll(commentPattern.findAll(code))
        addAll(annotationPattern.findAll(code))
        addAll(keywordPattern.findAll(code))
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


fun IntRange.overlaps(other: IntRange): Boolean =
    this.first <= other.last && other.first <= this.last

fun foldLambdas(
    code: String,
    folded: Boolean
): String {
    if (!folded) return code

    val result = StringBuilder()
    var depth = 0
    var i = 0

    while (i < code.length) {
        val c = code[i]

        when (c) {
            '{' -> {
                depth++
                result.append("{ … }")

                // skip until matching }
                i++
                var innerDepth = 1
                while (i < code.length && innerDepth > 0) {
                    when (code[i]) {
                        '{' -> innerDepth++
                        '}' -> innerDepth--
                    }
                    i++
                }

                depth--
                continue
            }
            else -> result.append(c)
        }

        i++
    }

    return result.toString()
}