package ke.don.gallery.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ke.don.gallery.domain.ComponentExample

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ComponentList(
    components: List<ComponentExample>,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onComponentClick: (ComponentExample) -> Unit = {}
) {
    val sortedComponents = components.sortedBy { it.label }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(300.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(sortedComponents) { component ->
            ComponentItem(
                component = component,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                onComponentClick = onComponentClick
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ComponentItem(
    component: ComponentExample,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onComponentClick: (ComponentExample) -> Unit,
    modifier: Modifier = Modifier
) {
    with(sharedTransitionScope) {
        OutlinedCard(
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth()
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "container-${component.label}"),
                    animatedVisibilityScope = animatedContentScope
                ),
            onClick = { onComponentClick(component) },
            shape = RoundedCornerShape(16.dp),
        ) {
            Column {
                // Preview area
                MintBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    component.rendered()
                }

                // Shaded metadata section
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = component.label,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.sharedBounds(
                                    sharedContentState = rememberSharedContentState(key = "text-${component.label}"),
                                    animatedVisibilityScope = animatedContentScope
                                )
                            )

                            Box(
                                modifier = Modifier.height(50.dp)
                            ){
                                if (component.type != null) {
                                    AssistChip(
                                        modifier = modifier,
                                        onClick = {},
                                        border = BorderStroke(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        label = {
                                            Text(
                                                text = component.type.name,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        if (component.description.isNotEmpty()) {
                            Text(
                                text = component.description,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MintBox(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable () -> Unit
) {
    Box(
        contentAlignment = contentAlignment,
        modifier = modifier.pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    awaitPointerEvent() // observe but do not consume
                }
            }
        }
    ) {
        content()
    }
}
