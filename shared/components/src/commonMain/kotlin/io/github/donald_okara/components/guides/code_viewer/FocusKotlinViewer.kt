package io.github.donald_okara.components.guides.code_viewer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.UnfoldLess
import androidx.compose.material.icons.outlined.UnfoldMore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.donald_okara.components.icon.IconButtonToken

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusKotlinViewer(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    darkTheme: Boolean,
    toggleTheme: () -> Unit,
    code: () -> String
) {
    /* ---------- scale ---------- */

    val minScale = 0.75f
    val maxScale = 1.75f

    var textScale by rememberSaveable { mutableStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = textScale,
        label = "text-scale"
    )

    /* ---------- folding ---------- */

    var foldLambdas by rememberSaveable { mutableStateOf(false) }

    /* ---------- theme ---------- */

    val targetTheme = if (darkTheme) CodeThemes.Dark else CodeThemes.Light
    val colorScheme = animateCodeTheme(targetTheme)

    /* ---------- dialog ---------- */

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(0.8f),
            color = colorScheme.background,
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.End
            ) {

                /* --- top bar --- */

                TopAppBar(
                    title = { Text("Preview") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorScheme.background,
                        titleContentColor = colorScheme.normal,
                        actionIconContentColor = colorScheme.normal
                    ),
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = colorScheme.normal
                            )
                        }
                    },
                    actions = {

                        IconButtonToken(
                            icon = if (foldLambdas)
                                Icons.Outlined.UnfoldMore
                            else
                                Icons.Outlined.UnfoldLess,
                            contentDescription = "Fold Lambdas",
                            accentColor = colorScheme.normal,
                            sizeInt = 36,
                            onClick = { foldLambdas = !foldLambdas }
                        )

                        IconButtonToken(
                            icon = if (darkTheme)
                                Icons.Outlined.LightMode
                            else
                                Icons.Outlined.DarkMode,
                            contentDescription = "Toggle Theme",
                            accentColor = colorScheme.normal,
                            sizeInt = 36,
                            onClick = toggleTheme
                        )
                    }
                )

                /* --- scale control --- */

                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "A",
                        style = MaterialTheme.typography.bodySmall.scaled(minScale),
                        color = colorScheme.normal
                    )

                    Slider(
                        value = textScale,
                        onValueChange = { textScale = it },
                        valueRange = minScale..maxScale,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "A",
                        style = MaterialTheme.typography.bodyLarge.scaled(maxScale),
                        color = colorScheme.normal
                    )
                }

                HorizontalDivider()

                /* --- content --- */

                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    KotlinCodeViewer(
                        code = code(),
                        codeTheme = colorScheme,
                        textScale = animatedScale,
                        shouldFoldLambdas = foldLambdas,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    )
                }
            }
        }
    }
}



fun TextStyle.scaled(scale: Float): TextStyle {
    return copy(
        fontSize = fontSize * scale,
        lineHeight = lineHeight * scale
    )
}

