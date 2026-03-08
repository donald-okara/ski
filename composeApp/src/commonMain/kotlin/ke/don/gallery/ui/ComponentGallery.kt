package ke.don.gallery.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import ke.don.gallery.domain.ComponentExample

@Composable
fun ComponentGallery(components: List<ComponentExample>) {
    var selectedComponent by remember { mutableStateOf<ComponentExample?>(null) }

    AnimatedContent(targetState = selectedComponent){ component ->
        when{
            component == null -> ComponentList(components){
                selectedComponent = it
            }

            else -> ComponentDetail(component) {
                selectedComponent = null
            }
        }


    }
    if (selectedComponent == null) {
        // Grid view

    } else {
        // Detail view

    }
}

@Composable
private fun ComponentList(
    components: List<ComponentExample>,
    onComponentClick: (ComponentExample) -> Unit = {}
){
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){
        LazyVerticalGrid(
            columns = GridCells.Adaptive(200.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(components) { component ->
                ComponentItem(
                    component = component,
                    onComponentClick = onComponentClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentDetail(component: ComponentExample, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(component.label) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Description", style = MaterialTheme.typography.titleMedium)
            Text(component.description, modifier = Modifier.padding(vertical = 8.dp))

            Text("Rendered", style = MaterialTheme.typography.titleMedium)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color.LightGray.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                component.rendered()
            }

            component.focusable?.let {
                Spacer(Modifier.height(12.dp))
                Text("Focusable Example", style = MaterialTheme.typography.titleMedium)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(Color.LightGray.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    it()
                }
            }

            Spacer(Modifier.height(12.dp))
            Text("Dos", style = MaterialTheme.typography.titleMedium)
            component.dos.forEach { doItem ->
                Text("• $doItem", modifier = Modifier.padding(start = 8.dp, top = 4.dp))
            }

            Spacer(Modifier.height(8.dp))
            Text("Don'ts", style = MaterialTheme.typography.titleMedium)
            component.donts.forEach { dontItem ->
                Text("• $dontItem", modifier = Modifier.padding(start = 8.dp, top = 4.dp))
            }
        }
    }
}

@Composable
fun ComponentItem(
    component: ComponentExample,
    onComponentClick: (ComponentExample) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        onClick = { onComponentClick(component) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            // Label with subtle styling
            Text(
                text = component.label,
                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Rendered component preview
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = 2.dp
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(8.dp)
                ) {
                    component.rendered()
                }
            }

            // Optional: a subtle description preview
            if (component.description.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = component.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}