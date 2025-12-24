package com.example.components.previews

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.design.ui.theme.AppTheme

@Preview(
    name = "Mobile",
    group = "Devices",
    heightDp = 890,
    widthDp = 441,
)
@Preview(
    name = "Tablet",
    group = "Devices",
    heightDp = 720,
    widthDp = 800,
)
@Preview(
    name = "Desktop",
    group = "Devices",
    heightDp = 800,
    widthDp = 1280,
)
annotation class DevicePreviews

@Preview(
    name = "Light",
    group = "Themes",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Dark",
    group = "Themes",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class ThemePreviews

@Composable
fun PreviewContainer(
    isDark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    AppTheme(
        darkTheme = isDark
    ) {
        Surface(
            modifier = Modifier,
        ) {
            content.invoke()
        }
    }
}