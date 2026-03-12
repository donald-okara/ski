package ke.don.gallery.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import ke.don.gallery.domain.ComponentExample

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalSharedTransitionApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun ComponentDetail(
    component: ComponentExample,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onBack: () -> Unit
) {
    var showFocusable by remember {
        mutableStateOf(false)
    }
    with(sharedTransitionScope) {
        Scaffold(
            modifier = Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(key = "container-${component.label}"),
                animatedVisibilityScope = animatedContentScope
            ),
            topBar = {
                TopAppBar(title = {
                    Text(
                        text = component.label, modifier = Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "text-${component.label}"),
                            animatedVisibilityScope = animatedContentScope
                        )
                    )
                }, navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                })
            },
        ) { padding ->
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize().
                verticalScroll(rememberScrollState())
                    .padding(padding)
                    .padding(16.dp)
            ) {
                ShowcaseContainer {
                    component.rendered()
                }

                DescriptionSegment(component)

                if (component.dos.isNotEmpty() || component.donts.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier
                            .align(Alignment.Start)
                            .fillMaxWidth(widthFraction),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (component.dos.isNotEmpty()) {
                            DosComponent(
                                component = component,
                                isDos = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .widthIn(min = 300.dp)
                            )
                        }

                        if (component.donts.isNotEmpty()) {
                            DosComponent(
                                component = component,
                                isDos = false,
                                modifier = Modifier
                                    .weight(1f)
                                    .widthIn(min = 300.dp)
                            )
                        }
                    }
                }

                if (component.focusable != null) {
                    Section(
                        title = "Focus Mode"
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {

                                ShowcaseContainer(blur = true) {
                                    component.rendered()
                                }

                                Surface(
                                    shape = RoundedCornerShape(50),
                                    tonalElevation = 6.dp
                                ) {
                                    Button(
                                        onClick = { showFocusable = true }
                                    ) {
                                        Text("Open Focus Mode")
                                    }
                                }
                            }

                            PathComponent(
                                path = component.focusable.path,
                            )
                        }
                    }
                }
            }
        }
    }

    if (showFocusable && component.focusable != null) {
        component.focusable.rendered { showFocusable = false }
    }

}

@Composable
private fun DescriptionSegment(component: ComponentExample) {
    Section("Description") {
        Text(component.description)
    }
}

@Composable
private fun DosComponent(
    component: ComponentExample,
    isDos: Boolean,
    hueColor: Color = if (isDos) Color.Green else MaterialTheme.colorScheme.error,
    modifier: Modifier = Modifier
) {
    val items = if (isDos) component.dos else component.donts

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = hueColor.copy(alpha = 0.1f).compositeOver(
            MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(
            width = 1.dp, color = hueColor.copy(alpha = 0.4f).compositeOver(
                MaterialTheme.colorScheme.outline
            )
        )
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isDos) Icons.Default.Check else Icons.Rounded.Close,
                    contentDescription = if (isDos) "Dos" else "Don'ts",
                    tint = hueColor
                )
                Text(
                    text = if (isDos) "Dos" else "Don'ts",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            items.forEach { item ->
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("•")
                    Text(item)
                }
            }
        }
    }
}

@Composable
private fun ShowcaseContainer(
    modifier: Modifier = Modifier,
    blur: Boolean = false,
    content: @Composable () -> Unit,
) {
    val blurModifier = if (blur) Modifier.blur(8.dp) else Modifier

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
    ){
        Surface(
            modifier = Modifier
                .fillMaxWidth(widthFraction)
                .height(240.dp),
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 2.dp,
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant
            ),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                Color.Transparent
                            )
                        )
                    )
                    .then(blurModifier),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    }
}

@Composable
private fun Section(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp), // indentation
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            content()
        }
    }
}

@Composable
fun PathComponent(
    modifier: Modifier = Modifier, path: String
) {
    val text = buildAnnotatedString {
        append("Path: ")

        // Push styles for the path portion
        pushStyle(
            SpanStyle(
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary, // Highlight color
                background = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) // Background highlight
            )
        )
        append(path)
        pop() // Remove the styles
    }

    // Example of displaying it
    Text(
        text = text, modifier = modifier, style = MaterialTheme.typography.bodyMedium
    )
}

const val widthFraction = 0.7f
