package ke.don.gallery.data

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.donald_okara.components.charts.BarChart
import io.github.donald_okara.components.charts.ChartDefaults
import io.github.donald_okara.components.charts.ChartPoint
import io.github.donald_okara.components.charts.ColumnChart
import io.github.donald_okara.components.charts.Confetti
import io.github.donald_okara.components.reveal.RevealBulletList
import io.github.donald_okara.components.stats.StatDirection
import io.github.donald_okara.components.stats.StatTile
import io.github.donald_okara.components.swatches.SwatchRow
import io.github.donald_okara.components.text.BulletList
import io.github.donald_okara.components.text.CalloutCard
import io.github.donald_okara.components.text.SlideTitle
import io.github.donald_okara.components.timeline.EvolutionTimeline
import io.github.donald_okara.components.timeline.TimelineStage
import ke.don.domain.LocalRevealStep
import ke.don.gallery.domain.ComponentGalleryBuilder
import ke.don.gallery.domain.ComponentType

fun ComponentGalleryBuilder.slideContent() {
    component(
        label = "Slide Title",
        description = "A slide heading with an optional kicker line above it. It fades and rises in when the slide opens." +
            "\n\nColours: pass `SlideBlockDefaults.titleColors(kicker = ..., title = ...)`.",
        type = ComponentType.Text,
        rendered = {
            GalleryPadding {
                SlideTitle(title = "Why we rewrote the app", kicker = "Part two")
            }
        },
        dos = listOf("Keep the kicker to two or three words", "Use one title per slide"),
        donts = listOf("Don't put full sentences in the kicker")
    )

    component(
        label = "Bullet List",
        description = "A bulleted list whose items fade in one after another when the slide opens." +
            "\n\nColours: pass `SlideBlockDefaults.bulletColors(dot = ..., text = ...)`.",
        type = ComponentType.Text,
        rendered = {
            GalleryPadding {
                BulletList(listOf("One codebase", "Four platforms", "Zero copy-paste"))
            }
        },
        dos = listOf("Keep each bullet to one line"),
        donts = listOf("Don't use it when the presenter should control the pace; use Reveal Bullet List")
    )

    component(
        label = "Reveal Bullet List",
        description = "Bullets that appear one per arrow press. Give the slide `steps = items.size` in `generateDeck`, " +
            "and the deck reveals one item per step before moving to the next slide. Use `RevealItem(index)` to reveal " +
            "anything else the same way. Hidden items keep their space, so the layout doesn't jump.",
        type = ComponentType.Text,
        rendered = {
            RevealStepper(steps = 3) {
                RevealBulletList(listOf("Measure first", "Change one thing", "Measure again"))
            }
        },
        dos = listOf("Match the slide's `steps` to the number of revealed items", "Use reveals to pace a story"),
        donts = listOf("Don't duplicate a slide to fake a reveal")
    )

    component(
        label = "Callout Card",
        description = "An outlined card with a title chip, for a side note next to the main content." +
            "\n\nColours: pass `SlideBlockDefaults.calloutCardColors(border = ..., chipContainer = ..., chipContent = ...)`.",
        type = ComponentType.Text,
        rendered = {
            GalleryPadding {
                CalloutCard(title = "Community corner") {
                    Text("Join the monthly meetup to see this live.", style = MaterialTheme.typography.bodyLarge)
                }
            }
        },
        dos = listOf("Use for asides the audience can skip"),
        donts = listOf("Don't stack several callouts on one slide")
    )

    component(
        label = "Stat Tile",
        description = "A big number that counts up when the slide opens, with an arrow for the direction of the change. " +
            "Decreases also shrink the number as it counts." +
            "\n\nColours: pass `StatTileDefaults.colors(improvement = ..., regression = ..., container = ...)`.",
        type = ComponentType.Chart,
        rendered = {
            GalleryPadding {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatTile(
                        value = 22f,
                        unit = "%",
                        label = "Cold start",
                        direction = StatDirection.Decrease,
                        sha = "a1b2c3d",
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        value = 12.5f,
                        unit = "%",
                        label = "Crash rate",
                        direction = StatDirection.Increase,
                        improvement = false,
                        source = "Play Console, March",
                        decimals = 1,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        dos = listOf("Cite a source or commit under the number", "Set `improvement = false` for bad news"),
        donts = listOf("Don't show more than three tiles on one slide")
    )

    component(
        label = "Column Chart",
        description = "Vertical columns scaled to the largest value. The columns grow from zero when the slide opens, " +
            "and a point with `highlight = true` is drawn in the highlight colour." +
            "\n\nColours: pass `ChartDefaults.colors(bar = ..., highlightBar = ..., ...)`.",
        type = ComponentType.Chart,
        rendered = {
            GalleryPadding {
                ColumnChart(
                    points = listOf(
                        ChartPoint("2019", 4f),
                        ChartPoint("2020", 9f),
                        ChartPoint("2022", 17f),
                        ChartPoint("2024", 31f, highlight = true)
                    ),
                    caption = "Modules per year",
                    modifier = Modifier.fillMaxWidth().height(260.dp)
                )
            }
        },
        dos = listOf("Give the chart a fixed height", "Highlight one point at most"),
        donts = listOf("Don't use for more than about eight values")
    )

    component(
        label = "Bar Chart",
        description = "Horizontal bars with the label on the left and the value on the right. The bars grow from zero " +
            "when the slide opens." +
            "\n\nThis example passes custom colours through `ChartDefaults.colors`.",
        type = ComponentType.Chart,
        rendered = {
            GalleryPadding {
                BarChart(
                    points = listOf(
                        ChartPoint("Before", 1335f),
                        ChartPoint("After", 1047f, highlight = true)
                    ),
                    valueSuffix = " ms",
                    colors = ChartDefaults.colors(bar = Color(0xFF90A4AE), highlightBar = Color(0xFF43A047))
                )
            }
        },
        dos = listOf("Use for before-and-after comparisons"),
        donts = listOf("Don't mix units in one chart")
    )

    component(
        label = "Confetti",
        description = "A one-shot burst of dots from the centre. It plays each time `trigger` becomes true. " +
            "Pass `palette` to choose the dot colours.",
        type = ComponentType.Chart,
        rendered = {
            var fired by remember { mutableStateOf(false) }
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                OutlinedButton(onClick = { fired = !fired }) { Text("Celebrate") }
                Confetti(trigger = fired, modifier = Modifier.fillMaxSize())
            }
        },
        dos = listOf("Save it for one big moment in the talk"),
        donts = listOf("Don't loop it")
    )

    component(
        label = "Evolution Timeline",
        description = "A horizontal timeline that lights one stage per reveal step. Hover a stage to preview its " +
            "details, or click to pin it. `gapAfterIndex` draws a dashed \"?\" marker for missing years." +
            "\n\nColours: pass `TimelineDefaults.colors(lit = ..., track = ..., ...)`.",
        type = ComponentType.Chart,
        rendered = {
            RevealStepper(steps = timelineStages.size) {
                EvolutionTimeline(stages = timelineStages, gapAfterIndex = 1)
            }
        },
        dos = listOf("Give the slide `steps = stages.size`", "Keep stage titles to two or three words"),
        donts = listOf("Don't use more than about eight stages")
    )

    component(
        label = "Swatch Row",
        description = "A labelled row of colour swatches, each with its name and hex value." +
            "\n\nColours: pass `SwatchRowDefaults.colors(...)` for the label, outline and text.",
        type = ComponentType.Chart,
        rendered = {
            val scheme = MaterialTheme.colorScheme
            GalleryPadding {
                SwatchRow(
                    label = "Current theme",
                    swatches = listOf(
                        "primary" to scheme.primary,
                        "secondary" to scheme.secondary,
                        "tertiary" to scheme.tertiary,
                        "background" to scheme.background
                    )
                )
            }
        },
        dos = listOf("Use for design-system and theming talks"),
        donts = listOf("Don't show more than about six swatches in a row")
    )
}

private val timelineStages = listOf(
    TimelineStage("2018", "First app", "app-v1", listOf("A single module", "Java and XML")),
    TimelineStage("2020", "Modules", "app-v2", listOf("Split into feature modules")),
    TimelineStage("2022", "Compose", "app-v3", listOf("UI rewritten in Compose")),
    TimelineStage("2024", "Multiplatform", "app-kmp", listOf("Shared logic on Android and iOS"))
)

@Composable
private fun GalleryPadding(content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, content = content)
}

/** Simulates the deck's reveal steps in the gallery, where no deck sets [LocalRevealStep]. */
@Composable
private fun RevealStepper(steps: Int, content: @Composable () -> Unit) {
    var step by remember { mutableStateOf(0) }
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { if (step > 0) step-- }) { Text("Back") }
            OutlinedButton(onClick = { if (step < steps) step++ }) { Text("Next") }
            Text("Step $step of $steps", style = MaterialTheme.typography.labelLarge)
        }
        CompositionLocalProvider(LocalRevealStep provides step) {
            content()
        }
    }
}
