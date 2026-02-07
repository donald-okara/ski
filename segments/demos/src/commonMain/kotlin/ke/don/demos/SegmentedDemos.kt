package ke.don.demos

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.donald_okara.components.frames.defaultSkiFrames
import io.github.donald_okara.components.layout.HorizontallySegmentedScreen
import io.github.donald_okara.components.layout.VerticallySegmentedScreen

@Composable
fun HorizontalSegmentsDemo(
    modifier: Modifier = Modifier,
) {
    HorizontallySegmentedScreen(
        modifier = modifier,
        initialSegments = segments,
    )
}

@Composable
fun VerticalSegmentsDemo(
    modifier: Modifier = Modifier,
) {
    VerticallySegmentedScreen(
        modifier = modifier,
        initialSegments = segments,
    )
}
val segments = listOf(
    1f to @Composable { SegmentItem(index = 1) },
    1f to @Composable { SegmentItem(index = 2) },
    1f to @Composable { SegmentItem(index = 3) },
)

@Composable
fun SegmentItem(
    modifier: Modifier = Modifier,
    index: Int
) {
    val frame = defaultSkiFrames().snake.create()
    frame.Render(
        modifier = modifier
            .padding(16.dp),
        header = null,
        footer = null,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = "Segment $index",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }

}