package ke.don.gallery.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateBounds
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ke.don.gallery.domain.ComponentExample
import ke.don.gallery.domain.ComponentType

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ComponentList(
    components: List<ComponentExample>,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onComponentClick: (ComponentExample) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<ComponentType?>(null) }
    
    val gridState = rememberLazyGridState()

    val filteredComponents = components
        .filter { component ->
            val matchesSearch = component.label.contains(searchQuery, ignoreCase = true) ||
                    component.description.contains(searchQuery, ignoreCase = true)
            val matchesType = selectedType == null || component.type == selectedType
            matchesSearch && matchesType
        }
        .sortedBy { it.label }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val spacing = 16.dp
        val minWidth = 300.dp
        
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = minWidth),
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(spacing),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                SkiHeader(modifier = Modifier.padding(bottom = spacing))
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                FilterRow(
                    modifier = Modifier.padding(bottom = spacing),
                    searchQuery = searchQuery,
                    selectedType = selectedType,
                    onQueryChange = { searchQuery = it },
                    onTypeChange = { selectedType = it }
                )
            }

            if (filteredComponents.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EmptyState(
                        query = searchQuery,
                        selectedType = selectedType,
                        onClearFilters = {
                            searchQuery = ""
                            selectedType = null
                        }
                    )
                }
            } else {
                items(
                    items = filteredComponents,
                    key = { it.label }
                ) { component ->
                    LookaheadScope {
                        ComponentItem(
                            modifier = Modifier
                                .animateItem()
                                .animateBounds(this),
                            component = component,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedContentScope = animatedContentScope,
                            onComponentClick = onComponentClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SkiHeader(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Ski: A Compose Multiplatform Presentation Framework",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Ski is a programmable presentation framework that allows Kotlin engineers to build slide decks the same way they build UI: with composables, state, and reusable components. By treating slides as declarative UI, Ski enables seamless integration of live demos, state-driven animations, and a structured layout system that aligns with modern development practices.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "If you can build it in Compose, you can present it.",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun EmptyState(
    query: String,
    selectedType: ComponentType?,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth().height(400.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No components found",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            val message = when {
                query.isNotEmpty() && selectedType != null -> 
                    "No results for \"$query\" in ${selectedType.name}."
                query.isNotEmpty() -> 
                    "No results for \"$query\"."
                selectedType != null -> 
                    "No components of type ${selectedType.name} found."
                else -> "The component gallery is currently empty."
            }
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            if (query.isNotEmpty() || selectedType != null) {
                AssistChip(
                    onClick = onClearFilters,
                    label = { Text("Clear all filters") },
                    leadingIcon = { Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterRow(
    searchQuery: String,
    selectedType: ComponentType?,
    modifier: Modifier = Modifier,
    onQueryChange: (String) -> Unit = {},
    onTypeChange: (ComponentType?) -> Unit = {}
){
    FlowRow(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom)
    ) {
        // Search Bar - Top Left
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            modifier = Modifier
                .widthIn(max = 400.dp),
            placeholder = { Text("Search components...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        FilterChip(
            selected = selectedType == null,
            onClick = { onTypeChange(null) },
            label = { Text("All") }
        )

        ComponentType.entries.forEach { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeChange(type) },
                label = { Text(type.name) }
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalLayoutApi::class)
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

                // Metadata section
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
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

                            if (component.type != null) {
                                AssistChip(
                                    onClick = {},
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outline
                                    ),
                                    label = {
                                        Text(
                                            text = component.type.name,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                )
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
                    awaitPointerEvent()
                }
            }
        }
    ) {
        content()
    }
}
