package ke.don.design.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import ke.don.resources.Resources
import org.jetbrains.compose.resources.Font

val bodyFontFamily: FontFamily
    @Composable
    get() = FontFamily(
        Font(Resources.Font.GOOGLE_SANS_EXTRA_LIGHT, FontWeight.Normal),
        )

val displayFontFamily: FontFamily
    @Composable
    get() = FontFamily(
        Font(Resources.Font.GOOGLE_SANS_BOLD, FontWeight.Bold),
        )
val subheadingsFontFamily: FontFamily
    @Composable
    get() = FontFamily(
        Font(Resources.Font.GOOGLE_SANS_EXTRA_LIGHT, FontWeight.Bold),
    )

// Default Material 3 typography values
val baseline = Typography()

// Typography customized for a slideshow presentation
val AppTypography: Typography
    @Composable
    get() = Typography(
        displayLarge = baseline.displayLarge.copy(
            fontFamily = displayFontFamily,
            fontSize = 112.sp,
            letterSpacing = (-0.02).em,
            lineHeight = 140.sp
        ),
        displayMedium = baseline.displayMedium.copy(
            fontFamily = displayFontFamily,
            fontSize = 72.sp,
            lineHeight = 90.sp
        ),
        displaySmall = baseline.displaySmall.copy(
            fontFamily = subheadingsFontFamily,
            fontSize = 56.sp,
            lineHeight = 70.sp
        ),
        headlineLarge = baseline.headlineLarge.copy(
            fontFamily = displayFontFamily,
            fontSize = 48.sp,
            lineHeight = 60.sp
        ),
        headlineMedium = baseline.headlineMedium.copy(
            fontFamily = displayFontFamily,
            fontSize = 40.sp,
            lineHeight = 50.sp
        ),
        headlineSmall = baseline.headlineSmall.copy(
            fontFamily = subheadingsFontFamily,
            fontSize = 36.sp,
            lineHeight = 45.sp
        ),
        titleLarge = baseline.titleLarge.copy(
            fontFamily = displayFontFamily,
            fontSize = 32.sp,
            lineHeight = 40.sp
        ),
        titleMedium = baseline.titleMedium.copy(
            fontFamily = displayFontFamily,
            fontSize = 28.sp,
            lineHeight = 35.sp
        ),
        titleSmall = baseline.titleSmall.copy(
            fontFamily = subheadingsFontFamily,
            fontSize = 24.sp,
            lineHeight = 30.sp
        ),
        bodyLarge = baseline.bodyLarge.copy(
            fontFamily = bodyFontFamily,
            fontSize = 28.sp,
            lineHeight = 38.sp
        ),
        bodyMedium = baseline.bodyMedium.copy(
            fontFamily = bodyFontFamily,
            fontSize = 24.sp,
            lineHeight = 32.sp
        ),
        bodySmall = baseline.bodySmall.copy(
            fontFamily = subheadingsFontFamily,
            fontSize = 20.sp,
            lineHeight = 28.sp
        ),
        labelLarge = baseline.labelLarge.copy(
            fontFamily = bodyFontFamily
        ),
        labelMedium = baseline.labelMedium.copy(
            fontFamily = bodyFontFamily
        ),
        labelSmall = baseline.labelSmall.copy(
            fontFamily = subheadingsFontFamily
        ),
    )
