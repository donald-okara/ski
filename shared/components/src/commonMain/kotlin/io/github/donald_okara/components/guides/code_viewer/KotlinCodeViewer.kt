package io.github.donald_okara.components.guides.code_viewer

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.UnfoldLess
import androidx.compose.material.icons.outlined.UnfoldMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import io.github.donald_okara.components.icon.IconButtonToken

@Composable
fun KotlinCodeViewerCard(
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

    var foldLambdas by rememberSaveable { mutableStateOf(false) }

    val source = remember { code() }

    Surface(
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 1.dp,
        color = colorScheme.background,
        modifier = modifier,
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
                    icon = if (foldLambdas)
                        Icons.Outlined.UnfoldMore
                    else
                        Icons.Outlined.UnfoldLess,
                    accentColor = colorScheme.normal,
                    sizeInt = 48,
                    onClick = { foldLambdas = !foldLambdas }
                )

                IconButtonToken(
                    icon = Icons.Default.Fullscreen,
                    accentColor = colorScheme.normal,
                    sizeInt = 48,
                    onClick = toggleFocus
                )

                IconButtonToken(
                    icon = if (darkTheme) {
                        Icons.Outlined.LightMode
                    } else {
                        Icons.Outlined.DarkMode
                    },
                    accentColor = colorScheme.normal,
                    sizeInt = 48,
                    onClick = toggleTheme
                )

            }
            KotlinCodeViewer(
                darkTheme = darkTheme,
                foldLambdas = foldLambdas,
                codeTheme = colorScheme,
                code = source,
            )
        }
    }
}

@Composable
fun KotlinCodeViewer(
    modifier: Modifier = Modifier,
    code: String,
    darkTheme: Boolean = true,
    codeTheme: CodeTheme = if (darkTheme) {
        CodeThemes.Dark
    } else {
        CodeThemes.Light
    },
    textScale: Float = 1f,
    foldLambdas: Boolean = true
) {
    val processedCode = remember(code, foldLambdas) {
        foldLambdas(code, foldLambdas)
    }

    val baseStyle = MaterialTheme.typography.bodyLarge

    AnimatedContent(
        targetState = processedCode,
        label = "code-fold",
        transitionSpec = {
            fadeIn() + expandVertically() togetherWith
                    fadeOut() + shrinkVertically()
        }
    ) { animatedCode ->

        val highlighted = remember(animatedCode, codeTheme) {
            highlightKotlin(animatedCode, codeTheme)
        }

        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 1.dp,
            color = codeTheme.background,
            modifier = modifier
                .padding(vertical = 4.dp, horizontal = 8.dp),
        ) {
            SelectionContainer{
                Text(
                    text = highlighted,
                    fontFamily = FontFamily.Monospace,
                    style = baseStyle.scaled(textScale),
                    modifier = modifier
                )
            }

        }
    }
}
