package io.github.donald_okara.components.swatches

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import ke.don.domain.values.Values
import kotlin.math.roundToInt

/**
 * Colours for the chrome around [SwatchRow]'s swatches. The swatches themselves are drawn in their own colours.
 *
 * @param label Colour of the row label.
 * @param border Colour of each swatch's outline.
 * @param name Colour of each swatch's name.
 * @param hex Colour of each swatch's hex value.
 */
@Immutable
data class SwatchRowColors(val label: Color, val border: Color, val name: Color, val hex: Color)

/** Default [SwatchRow] colours, taken from the current Material theme. */
object SwatchRowDefaults {
    @Composable
    fun colors(
        label: Color = MaterialTheme.colorScheme.primary,
        border: Color = MaterialTheme.colorScheme.outline,
        name: Color = MaterialTheme.colorScheme.onSurface,
        hex: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    ) = SwatchRowColors(label, border, name, hex)
}

/**
 * A labelled row of colour swatches. Each swatch has its name and hex value under it.
 *
 * @param label The text above the row.
 * @param swatches Each swatch's name and colour, left to right.
 */
@Composable
fun SwatchRow(
    label: String,
    swatches: List<Pair<String, Color>>,
    modifier: Modifier = Modifier,
    colors: SwatchRowColors = SwatchRowDefaults.colors(),
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = colors.label
        )
        Spacer(modifier = Modifier.height(Values.Dimens.smallPadding))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Values.Dimens.smallPadding)
        ) {
            swatches.forEach { (name, color) ->
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .background(color, RoundedCornerShape(Values.Dimens.smallPadding))
                            .border(1.dp, colors.border, RoundedCornerShape(Values.Dimens.smallPadding))
                    )
                    Text(
                        text = name,
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.name
                    )
                    Text(
                        text = hexOf(color),
                        style = MaterialTheme.typography.labelMedium,
                        fontFamily = FontFamily.Monospace,
                        color = colors.hex
                    )
                }
            }
        }
    }
}

/** Formats an opaque colour as `#RRGGBB`, for the swatch labels. Uses no `String.format`, since this is common code. */
private fun hexOf(color: Color): String {
    fun channel(value: Float): String = (value * 255f).roundToInt().toString(16).padStart(2, '0')
    return "#" + (channel(color.red) + channel(color.green) + channel(color.blue)).uppercase()
}
