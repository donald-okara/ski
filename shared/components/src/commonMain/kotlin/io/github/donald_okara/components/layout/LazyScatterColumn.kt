package io.github.donald_okara.components.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.donald_okara.components.values.Values

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> LazyScatterFlow(
    items: List<T>,
    itemsPerRow: Int = 3,
    modifier: Modifier = Modifier,
    content: @Composable (index: Int, T) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Values.Dimens.mediumPadding, Alignment.CenterVertically)
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxItemsInEachRow = itemsPerRow,
            horizontalArrangement = Arrangement.spacedBy(Values.Dimens.mediumPadding, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.SpaceBetween,
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEachIndexed { index, item ->
                Box(modifier = Modifier.padding(Values.Dimens.tinyPadding)) {
                    content(index, item)
                }
            }
        }
    }
}