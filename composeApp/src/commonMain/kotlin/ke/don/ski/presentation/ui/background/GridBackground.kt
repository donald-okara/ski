package ke.don.ski.presentation.ui.background

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

@Composable
fun LeftThirdCircleGrid(
    modifier: Modifier = Modifier,
    circleRadius: Float = 20f,
    circleSpacing: Float = 100f,
    circleColor: Color = Color.Gray
) {
    require(circleRadius.isFinite() && circleRadius > 0f) {
        "circleRadius must be finite and > 0"
    }
    val step = (circleRadius * 2f) + circleSpacing
    require(step.isFinite() && step > 0f) {
        "circleSpacing is too small; computed step must be > 0"
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val maxWidth = size.width / 3   // only left 1/3
        val maxHeight = size.height

        var y = circleRadius
        while (y + circleRadius <= maxHeight) {
            var x = circleRadius
            while (x + circleRadius <= maxWidth) {
                drawCircle(color = circleColor, radius = circleRadius, center = Offset(x, y))
                x += step
            }
            y += step
        }
    }
}