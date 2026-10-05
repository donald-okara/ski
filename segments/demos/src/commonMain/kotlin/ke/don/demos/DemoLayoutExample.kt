package ke.don.demos

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.donald_okara.components.devices.DeviceCatalog
import io.github.donald_okara.components.devices.DeviceFrame
import io.github.donald_okara.components.layout.DemoLayout

@Composable
fun DemoLayoutExample(
    modifier: Modifier = Modifier
) {
    DemoLayout(
        title = "Demo Layout",
        explanation = listOf(
            "The title and points explain what the demo shows.",
            "The code card shows the snippet the demo illustrates.",
            "The live demo sits on the right, inside a device frame."
        ),
        code = """
            DemoLayout(
                title = "Demo Layout",
                explanation = listOf("What the demo shows"),
                code = snippet,
            ) {
                DeviceFrame(spec = DeviceCatalog.Pixel8) {
                    LiveContent()
                }
            }
        """.trimIndent(),
        modifier = modifier
    ) {
        DeviceFrame(
            spec = DeviceCatalog.Pixel8,
            modifier = Modifier.height(560.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Live content",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}
