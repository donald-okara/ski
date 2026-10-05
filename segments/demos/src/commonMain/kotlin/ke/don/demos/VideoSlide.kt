package ke.don.demos

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.donald_okara.components.layout.DemoLayout

/**
 * A slide that plays a video next to the snippet that explains it.
 *
 * @param videoUri The video to play. Use an http(s) URL, since bundled files need a platform-specific resolver.
 * @param explanation Points shown under the title.
 * @param code The snippet shown in the code card.
 */
@Composable
fun VideoSlide(
    videoUri: String,
    title: String,
    explanation: List<String>,
    code: String,
    modifier: Modifier = Modifier
) {
    DemoLayout(
        title = title,
        explanation = explanation,
        code = code,
        modifier = modifier
    ) {
        VideoPlayer(
            videoUri = videoUri,
            modifier = Modifier.fillMaxSize()
        )
    }
}
