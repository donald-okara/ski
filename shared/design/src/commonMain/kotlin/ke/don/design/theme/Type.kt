package ke.don.design.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
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

private fun TextStyle.withFont(fontFamily: FontFamily) =
    copy(fontFamily = fontFamily)

private val baseline = Typography()

@Composable
private fun baseTypography() = Typography(
    displayLarge = baseline.displayLarge.withFont(displayFontFamily),
    displayMedium = baseline.displayMedium.withFont(displayFontFamily),
    displaySmall = baseline.displaySmall.withFont(subheadingsFontFamily),

    headlineLarge = baseline.headlineLarge.withFont(displayFontFamily),
    headlineMedium = baseline.headlineMedium.withFont(displayFontFamily),
    headlineSmall = baseline.headlineSmall.withFont(subheadingsFontFamily),

    titleLarge = baseline.titleLarge.withFont(displayFontFamily),
    titleMedium = baseline.titleMedium.withFont(displayFontFamily),
    titleSmall = baseline.titleSmall.withFont(subheadingsFontFamily),

    bodyLarge = baseline.bodyLarge.withFont(bodyFontFamily),
    bodyMedium = baseline.bodyMedium.withFont(bodyFontFamily),
    bodySmall = baseline.bodySmall.withFont(subheadingsFontFamily),

    labelLarge = baseline.labelLarge.withFont(bodyFontFamily),
    labelMedium = baseline.labelMedium.withFont(bodyFontFamily),
    labelSmall = baseline.labelSmall.withFont(subheadingsFontFamily),
)

val AppTypography: Typography
    @Composable
    get() {
        val base = baseTypography()

        return base.copy(
            displayLarge = base.displayLarge.copy(
                fontSize = 112.sp,
                letterSpacing = (-0.02).em,
                lineHeight = 140.sp
            ),
            displayMedium = base.displayMedium.copy(
                fontSize = 72.sp,
                lineHeight = 90.sp
            ),
            displaySmall = base.displaySmall.copy(
                fontSize = 56.sp,
                lineHeight = 70.sp
            ),
            headlineLarge = base.headlineLarge.copy(
                fontSize = 48.sp,
                lineHeight = 60.sp
            ),
            headlineMedium = base.headlineMedium.copy(
                fontSize = 40.sp,
                lineHeight = 50.sp
            ),
            headlineSmall = base.headlineSmall.copy(
                fontSize = 36.sp,
                lineHeight = 45.sp
            ),
            titleLarge = base.titleLarge.copy(
                fontSize = 32.sp,
                lineHeight = 40.sp
            ),
            titleMedium = base.titleMedium.copy(
                fontSize = 28.sp,
                lineHeight = 35.sp
            ),
            titleSmall = base.titleSmall.copy(
                fontSize = 24.sp,
                lineHeight = 30.sp
            ),
            bodyLarge = base.bodyLarge.copy(
                fontSize = 28.sp,
                lineHeight = 38.sp
            ),
            bodyMedium = base.bodyMedium.copy(
                fontSize = 24.sp,
                lineHeight = 32.sp
            ),
            bodySmall = base.bodySmall.copy(
                fontSize = 20.sp,
                lineHeight = 28.sp
            )
        )
    }

val GalleryTypography: Typography
    @Composable
    get() = baseTypography()