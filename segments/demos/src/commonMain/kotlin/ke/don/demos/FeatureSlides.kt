package ke.don.demos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import io.github.donald_okara.components.guides.keys_shortcuts.DeckShortcuts
import io.github.donald_okara.components.guides.keys_shortcuts.ShortcutsDictionary
import io.github.donald_okara.components.layout.DemoLayout
import ke.don.domain.frames.FrameBuilder
import ke.don.domain.values.Values

/** A text slide: a title and short points. Used for the features that are easier to read than to demo. */
@Composable
fun FeatureSlide(
    title: String,
    points: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Values.Dimens.largePadding),
        verticalArrangement = Arrangement.spacedBy(Values.Dimens.mediumPadding),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        points.forEach { point ->
            Text(
                text = "• $point",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SlidesAreComposablesDemo(
    modifier: Modifier = Modifier
) {
    DemoLayout(
        title = "Slides Are Composables",
        explanation = listOf(
            "Each slide is a @Composable function.",
            "The deck is a list of those functions, built with a DSL.",
            "Anything you can build in Compose can be a slide."
        ),
        code = """
            generateDeck(timerController) {
                slide("Title") {
                    TitleScreen()
                }
                slide("Demo", notes = demoNotes) {
                    LiveDemo()
                }
            }
        """.trimIndent(),
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Values.Dimens.smallPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            listOf("TitleScreen()", "LiveDemo()", "Q&A()").forEach { name ->
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(Values.Dimens.mediumPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun RevealDemo(
    modifier: Modifier = Modifier
) {
    val points = listOf(
        "State drives the reveal.",
        "No slide is duplicated to show the next bullet.",
        "Press the button to show the next point."
    )
    var revealed by remember { mutableIntStateOf(0) }

    DemoLayout(
        title = "Stateful Reveals",
        explanation = listOf(
            "Reveals are Compose state, read by the slide.",
            "The same slide shows more of its content as state changes."
        ),
        code = """
            var revealed by remember { mutableIntStateOf(0) }

            points.forEachIndexed { index, point ->
                AnimatedVisibility(visible = index < revealed) {
                    Text(point)
                }
            }
        """.trimIndent(),
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Values.Dimens.mediumPadding)
        ) {
            points.forEachIndexed { index, point ->
                AnimatedVisibility(
                    visible = index < revealed,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = point,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Values.Dimens.smallPadding)) {
                Button(onClick = { if (revealed < points.size) revealed++ }) {
                    Text("Reveal")
                }
                Button(onClick = { revealed = 0 }) {
                    Text("Reset")
                }
            }
        }
    }
}

@Composable
fun FramesDemo(
    modifier: Modifier = Modifier
) {
    val snake = FrameBuilder().setFrame { snake }.build()
    val basic = FrameBuilder().setFrame { basic }.build()

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(Values.Dimens.largePadding),
        horizontalArrangement = Arrangement.spacedBy(Values.Dimens.mediumPadding)
    ) {
        snake.Render(modifier = Modifier.weight(1f), header = null, footer = null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Snake frame", style = MaterialTheme.typography.titleLarge)
            }
        }
        basic.Render(modifier = Modifier.weight(1f), header = null, footer = null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Basic frame", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

@Composable
fun ShortcutsSlide(
    modifier: Modifier = Modifier
) {
    val frame = FrameBuilder().setFrame { basic }.build()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(Values.Dimens.largePadding)
    ) {
        ShortcutsDictionary(
            shortcuts = DeckShortcuts,
            frame = frame
        )
    }
}
