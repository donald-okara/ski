package io.github.donald_okara.components.charts

import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import ke.don.domain.values.Values
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

/**
 * One labelled value in a [ColumnChart] or [BarChart].
 *
 * @param label The text under the column, or to the left of the bar.
 * @param value The figure. Bars are scaled to the largest value in the list.
 * @param highlight Draws the bar in the theme's secondary colour, to point at one figure.
 */
data class ChartPoint(
    val label: String,
    val value: Float,
    val highlight: Boolean = false
)

/**
 * Colours for [ColumnChart] and [BarChart].
 *
 * @param bar Fill of a normal bar.
 * @param highlightBar Fill of a bar whose [ChartPoint.highlight] is true. Also used for its label and value.
 * @param value Colour of a normal column's value. [BarChart] draws values in the bar colour.
 * @param label Colour of a normal label.
 * @param caption Colour of the caption under a [ColumnChart].
 */
@Immutable
data class ChartColors(
    val bar: Color,
    val highlightBar: Color,
    val value: Color,
    val label: Color,
    val caption: Color,
)

/** Default chart colours, taken from the current Material theme. */
object ChartDefaults {
    @Composable
    fun colors(
        bar: Color = MaterialTheme.colorScheme.primary,
        highlightBar: Color = MaterialTheme.colorScheme.secondary,
        value: Color = MaterialTheme.colorScheme.onSurface,
        label: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        caption: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    ) = ChartColors(bar, highlightBar, value, label, caption)

    /** The four colours [Confetti] picks from by default. */
    @Composable
    fun confettiPalette(): List<Color> = MaterialTheme.colorScheme.run {
        listOf(primary, secondary, tertiary, primaryContainer)
    }
}

private const val CHART_ANIMATION_MILLIS = 800
private val ValueLabelReserve: Dp = 32.dp
private val BarThickness: Dp = 28.dp
private val LabelWidth: Dp = 140.dp
private val ValueWidth: Dp = 96.dp

/**
 * Vertical columns scaled to the largest value. Each column has its value above it and its label below.
 * Bars grow from zero when the chart first composes.
 *
 * @param points The values, drawn left to right.
 * @param caption Optional small text under the chart, for example the source or the unit.
 */
@Composable
fun ColumnChart(
    points: List<ChartPoint>,
    modifier: Modifier = Modifier,
    caption: String? = null,
    colors: ChartColors = ChartDefaults.colors(),
) {
    val progress = rememberChartProgress()
    val p = progress.value
    val max = points.maxOfOrNull { it.value } ?: 0f

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth().weight(1f)) {
            val track = maxHeight - ValueLabelReserve
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(Values.Dimens.smallPadding)
            ) {
                points.forEach { point ->
                    val fraction = fractionOf(point.value, max) * p
                    Column(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = formatChartValue(point.value),
                            style = MaterialTheme.typography.labelLarge,
                            color = if (point.highlight) colors.highlightBar else colors.value,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(track * fraction)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .background(if (point.highlight) colors.highlightBar else colors.bar)
                        ) {}
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = Values.Dimens.tinyPadding),
            horizontalArrangement = Arrangement.spacedBy(Values.Dimens.smallPadding)
        ) {
            points.forEach { point ->
                Text(
                    text = point.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (point.highlight) colors.highlightBar else colors.label,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        if (caption != null) {
            Text(
                text = caption,
                style = MaterialTheme.typography.labelMedium,
                color = colors.caption,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = Values.Dimens.tinyPadding)
            )
        }
    }
}

/**
 * Horizontal bars with the label on the left and the value on the right. Bars grow from zero when the
 * chart first composes.
 *
 * @param points The values, drawn top to bottom.
 * @param valueSuffix Text after each value, for example "ms" or "%".
 */
@Composable
fun BarChart(
    points: List<ChartPoint>,
    modifier: Modifier = Modifier,
    valueSuffix: String = "",
    colors: ChartColors = ChartDefaults.colors(),
) {
    val progress = rememberChartProgress()
    val p = progress.value
    val max = points.maxOfOrNull { it.value } ?: 0f

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Values.Dimens.smallPadding)
    ) {
        points.forEach { point ->
            val fraction = fractionOf(point.value, max) * p
            val tint = if (point.highlight) colors.highlightBar else colors.bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Values.Dimens.smallPadding)
            ) {
                Text(
                    text = point.label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (point.highlight) colors.highlightBar else colors.label,
                    modifier = Modifier.width(LabelWidth)
                )
                Row(
                    modifier = Modifier.weight(1f).height(BarThickness),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(fraction.coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(tint)
                    ) {}
                }
                Text(
                    text = formatChartValue(point.value) + valueSuffix,
                    style = MaterialTheme.typography.bodyLarge,
                    color = tint,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(ValueWidth)
                )
            }
        }
    }
}

@Composable
private fun rememberChartProgress(): Animatable<Float, AnimationVector1D> {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(1f, tween(durationMillis = CHART_ANIMATION_MILLIS, easing = FastOutSlowInEasing))
    }
    return progress
}

private fun fractionOf(value: Float, max: Float): Float =
    if (max <= 0f) 0f else (value / max).coerceIn(0f, 1f)

/**
 * Formats a chart value with at most one decimal place, without `String.format` so it works in common code.
 */
private fun formatChartValue(value: Float): String {
    val tenths = (value * 10).roundToInt()
    val whole = tenths / 10
    val fraction = abs(tenths % 10)
    return if (fraction == 0) whole.toString() else "$whole.$fraction"
}

private const val POP_IN_DAMPING = 0.5f
private const val POP_IN_STIFFNESS = 400f
private const val CONFETTI_MILLIS = 1200
private const val CONFETTI_DOTS = 24

/**
 * Scales the content from 0.8 to 1 and fades it in, with a spring that overshoots slightly.
 * Runs once when the modifier first composes, after [delayMillis].
 */
fun Modifier.popIn(delayMillis: Int = 0): Modifier = composed {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        progress.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = POP_IN_DAMPING, stiffness = POP_IN_STIFFNESS)
        )
    }
    graphicsLayer {
        val p = progress.value
        val scale = 0.8f + 0.2f * p
        scaleX = scale
        scaleY = scale
        alpha = p.coerceIn(0f, 1f)
    }
}

/**
 * A one-shot burst of 24 coloured dots from the centre, lasting 1.2 seconds. It plays each time [trigger]
 * changes to true and draws nothing otherwise. Put it over the content it celebrates, sized to fill it.
 *
 * @param palette The colours the dots are drawn in. Must not be empty.
 */
@Composable
fun Confetti(
    trigger: Boolean,
    modifier: Modifier = Modifier,
    palette: List<Color> = ChartDefaults.confettiPalette(),
) {
    require(palette.isNotEmpty()) { "palette must not be empty" }
    val progress = remember { Animatable(1f) }
    val dots = remember { confettiDots() }

    LaunchedEffect(trigger) {
        if (!trigger) {
            progress.snapTo(1f)
            return@LaunchedEffect
        }
        progress.snapTo(0f)
        progress.animateTo(1f, tween(durationMillis = CONFETTI_MILLIS, easing = FastOutSlowInEasing))
    }

    Canvas(modifier = modifier) {
        val p = progress.value
        if (p >= 1f) return@Canvas
        val radius = size.minDimension * 0.5f
        dots.forEach { dot ->
            val distance = radius * dot.spread * p
            val center = Offset(
                x = size.width / 2f + cos(dot.angle) * distance,
                y = size.height / 2f + sin(dot.angle) * distance
            )
            drawConfettiDot(
                center = center,
                color = palette[dot.colorIndex % palette.size].copy(alpha = 1f - p),
                radiusPx = dot.sizeDp * density
            )
        }
    }
}

private class ConfettiDot(val angle: Float, val spread: Float, val sizeDp: Float, val colorIndex: Int)

private fun confettiDots(): List<ConfettiDot> {
    val random = Random(seed = 24)
    return List(CONFETTI_DOTS) {
        ConfettiDot(
            angle = (random.nextFloat() * 2f * PI).toFloat(),
            spread = 0.6f + random.nextFloat() * 0.4f,
            sizeDp = 4f + random.nextFloat() * 3f,
            colorIndex = random.nextInt(0, CONFETTI_DOTS)
        )
    }
}

private fun DrawScope.drawConfettiDot(center: Offset, color: Color, radiusPx: Float) {
    drawCircle(color = color, radius = radiusPx, center = center)
}
