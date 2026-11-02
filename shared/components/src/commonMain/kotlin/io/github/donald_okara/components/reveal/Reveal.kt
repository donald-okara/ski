package io.github.donald_okara.components.reveal

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import io.github.donald_okara.components.text.BulletColors
import io.github.donald_okara.components.text.BulletRow
import io.github.donald_okara.components.text.SlideBlockDefaults
import ke.don.domain.LocalRevealStep
import ke.don.domain.values.Values

/**
 * Shows [content] once the current reveal step reaches [index] (1-based), and keeps its space while hidden.
 *
 * The step comes from [LocalRevealStep], which the deck sets from the slide's `steps`. Outside a deck every item
 * is shown. Hidden items are fully transparent rather than removed, so the layout does not jump as steps advance.
 *
 * @param index The step at which this item appears. The first revealable item is 1.
 * @param content The item to reveal.
 */
@Composable
fun RevealItem(
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val step = LocalRevealStep.current
    val alpha by animateFloatAsState(
        targetValue = if (step >= index) 1f else 0f,
        animationSpec = tween(300),
        label = "revealItemAlpha"
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { this.alpha = alpha },
        verticalArrangement = Arrangement.Top
    ) {
        content()
    }
}

/**
 * A bulleted list where each bullet is revealed on its own step: item `i` appears at step `i + 1`.
 * Give the slide `steps = items.size`.
 *
 * @param items The bullet text, in reveal order.
 */
@Composable
fun RevealBulletList(
    items: List<String>,
    modifier: Modifier = Modifier,
    colors: BulletColors = SlideBlockDefaults.bulletColors(),
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Values.Dimens.smallPadding)
    ) {
        items.forEachIndexed { i, item ->
            RevealItem(index = i + 1) {
                BulletRow(item, colors = colors)
            }
        }
    }
}
