package io.github.donald_okara.components.text

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ke.don.domain.values.Values
import kotlinx.coroutines.delay

/**
 * Colours for [SlideTitle].
 *
 * @param kicker Colour of the short label above the title.
 * @param title Colour of the title.
 */
@Immutable
data class SlideTitleColors(val kicker: Color, val title: Color)

/**
 * Colours for [BulletList], [BulletRow] and the revealable bullet list.
 *
 * @param dot Colour of the bullet dot.
 * @param text Colour of the bullet text.
 */
@Immutable
data class BulletColors(val dot: Color, val text: Color)

/**
 * Colours for [CommitTag].
 *
 * @param container Background of the tag.
 * @param content Colour of the SHA text.
 */
@Immutable
data class CommitTagColors(val container: Color, val content: Color)

/**
 * Colours for [CalloutCard].
 *
 * @param border Colour of the card outline.
 * @param chipContainer Background of the title chip.
 * @param chipContent Colour of the title chip text.
 */
@Immutable
data class CalloutCardColors(val border: Color, val chipContainer: Color, val chipContent: Color)

/** Default colours for the slide text blocks, taken from the current Material colour scheme. */
object SlideBlockDefaults {
    @Composable
    fun titleColors(
        kicker: Color = MaterialTheme.colorScheme.primary,
        title: Color = MaterialTheme.colorScheme.onSurface,
    ) = SlideTitleColors(kicker = kicker, title = title)

    @Composable
    fun bulletColors(
        dot: Color = MaterialTheme.colorScheme.secondaryContainer,
        text: Color = MaterialTheme.colorScheme.onSurface,
    ) = BulletColors(dot = dot, text = text)

    @Composable
    fun commitTagColors(
        container: Color = MaterialTheme.colorScheme.surfaceVariant,
        content: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    ) = CommitTagColors(container = container, content = content)

    @Composable
    fun calloutCardColors(
        border: Color = MaterialTheme.colorScheme.outline,
        chipContainer: Color = MaterialTheme.colorScheme.secondaryContainer,
        chipContent: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    ) = CalloutCardColors(border = border, chipContainer = chipContainer, chipContent = chipContent)
}

/**
 * The title block for a content slide, with an optional kicker line above the title. Fades and rises in once
 * when the slide composes.
 *
 * @param title The slide title, shown large and bold.
 * @param kicker An optional short label shown above the title.
 */
@Composable
fun SlideTitle(
    title: String,
    modifier: Modifier = Modifier,
    kicker: String? = null,
    colors: SlideTitleColors = SlideBlockDefaults.titleColors(),
) {
    val entrance = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        entrance.animateTo(1f, tween(durationMillis = TITLE_ENTRANCE_MILLIS))
    }
    val rise = 24.dp
    Column(
        modifier = modifier
            .padding(bottom = Values.Dimens.mediumPadding)
            .graphicsLayer {
                alpha = entrance.value
                translationY = (1f - entrance.value) * rise.toPx()
            },
        verticalArrangement = Arrangement.spacedBy(Values.Dimens.tinyPadding)
    ) {
        if (kicker != null) {
            Text(
                text = kicker,
                style = MaterialTheme.typography.labelLarge,
                color = colors.kicker
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = colors.title
        )
    }
}

/**
 * A plain bulleted list. The bullets fade in one after another when the slide composes.
 *
 * For bullets that appear on reveal steps, use `RevealBulletList`.
 *
 * @param items The text of each bullet, in order.
 */
@Composable
fun BulletList(
    items: List<String>,
    modifier: Modifier = Modifier,
    colors: BulletColors = SlideBlockDefaults.bulletColors(),
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Values.Dimens.smallPadding)
    ) {
        items.forEachIndexed { i, item ->
            StaggeredEntrance(delayMillis = i * BULLET_STAGGER_MILLIS) {
                BulletRow(item, colors = colors)
            }
        }
    }
}

/** Fades [content] in and lifts it a few dp after [delayMillis], once, when the slide composes. */
@Composable
private fun StaggeredEntrance(delayMillis: Int, content: @Composable () -> Unit) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        progress.animateTo(1f, tween(durationMillis = BULLET_ENTRANCE_MILLIS))
    }
    val lift = 8.dp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = progress.value
                translationY = (1f - progress.value) * lift.toPx()
            }
    ) {
        content()
    }
}

private const val TITLE_ENTRANCE_MILLIS = 400
private const val BULLET_ENTRANCE_MILLIS = 400
private const val BULLET_STAGGER_MILLIS = 120

/**
 * One bullet: a dot followed by the item text in `headlineSmall`.
 *
 * @param text The bullet text.
 */
@Composable
fun BulletRow(
    text: String,
    modifier: Modifier = Modifier,
    colors: BulletColors = SlideBlockDefaults.bulletColors(),
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 12.dp)
                .size(Values.Dimens.smallPadding)
                .clip(CircleShape)
                .background(colors.dot)
        )
        Spacer(modifier = Modifier.size(Values.Dimens.smallPadding))
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall,
            color = colors.text
        )
    }
}

/**
 * A short commit SHA shown as a small monospace tag, for citing a commit on screen.
 *
 * @param sha The abbreviated commit SHA.
 */
@Composable
fun CommitTag(
    sha: String,
    modifier: Modifier = Modifier,
    colors: CommitTagColors = SlideBlockDefaults.commitTagColors(),
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Values.Dimens.tinyPadding))
            .background(colors.container)
            .padding(horizontal = Values.Dimens.smallPadding, vertical = Values.Dimens.tinyPadding)
    ) {
        Text(
            text = sha,
            style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
            color = colors.content
        )
    }
}

/**
 * A rounded, outlined card with a small title chip, for a side note.
 *
 * @param title The text on the chip at the top of the card.
 * @param content The card body, laid out in a column below the chip.
 */
@Composable
fun CalloutCard(
    title: String,
    modifier: Modifier = Modifier,
    colors: CalloutCardColors = SlideBlockDefaults.calloutCardColors(),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Values.Dimens.smallPadding))
            .border(1.dp, colors.border, RoundedCornerShape(Values.Dimens.smallPadding))
            .padding(Values.Dimens.mediumPadding),
        verticalArrangement = Arrangement.spacedBy(Values.Dimens.smallPadding)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = colors.chipContent,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(colors.chipContainer)
                .padding(horizontal = Values.Dimens.smallPadding, vertical = Values.Dimens.tinyPadding)
        )
        content()
    }
}

/**
 * A two-column slide layout. Both columns are equal width and vertically centred.
 *
 * @param lead The left column, for example the slide title and its focal element.
 * @param details The right column, for example the body text.
 */
@Composable
fun SplitSlide(
    lead: @Composable ColumnScope.() -> Unit,
    details: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(Values.Dimens.largePadding)
    ) {
        Column(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            lead()
        }
        Column(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            details()
        }
    }
}
