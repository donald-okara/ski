package ke.don.gallery.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import ke.don.design.theme.AppTheme
import ke.don.gallery.domain.ComponentExample
import ke.don.ski.presentation.ui.ToolBar

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ComponentGallery(components: List<ComponentExample>) {
    var selectedComponent by remember { mutableStateOf<ComponentExample?>(null) }
    var darkTheme by remember { mutableStateOf(true) }

    AppTheme(
        darkTheme = darkTheme,
        isGallery = true
    ){
        Surface(
            modifier = Modifier
                .onKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown && event.key == Key.D) {
                        darkTheme = !darkTheme
                        true
                    } else {
                        false
                    }
                }
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ){
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ToolBar(
                    darkTheme = darkTheme,
                    title = { Text("Ski Gallery") },
                    onThemeClick = {
                        darkTheme = !darkTheme
                    }
                )
                SharedTransitionLayout{
                    AnimatedContent(targetState = selectedComponent) { component ->
                        when {
                            component == null -> ComponentList(
                                components = components,
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedContentScope = this@AnimatedContent
                            ) {
                                selectedComponent = it
                            }

                            else -> ComponentDetail(
                                component = component,
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedContentScope = this@AnimatedContent
                            ) {
                                selectedComponent = null
                            }
                        }
                    }
                }
            }
        }
    }
}
