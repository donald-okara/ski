package ke.don.demos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.donald_okara.components.devices.DeviceCatalog
import io.github.donald_okara.components.devices.DeviceFrame
import io.github.donald_okara.components.devices.DeviceOrientation
import io.github.donald_okara.components.devices.DeviceSpec

@Composable
fun DeviceGallery() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(48.dp)
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            item {
                DeviceItem("Google Pixel 8", DeviceCatalog.Pixel8)
            }
            item {
                DeviceItem("Samsung Galaxy S26", DeviceCatalog.GalaxyS26)
            }
            item {
                DeviceItem("iPhone 17 Pro", DeviceCatalog.IPhone17)
            }
        }
    }
}

@Composable
private fun DeviceItem(name: String, spec: DeviceSpec) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        var orientation by remember {
            mutableStateOf(DeviceOrientation.PORTRAIT)
        }
        DeviceControls(
            orientation = orientation,
            onOrientationChange = {
                orientation = it
            }
        )

        DeviceFrame(
            spec = spec.copy(
                orientation = orientation
            ),
            modifier = Modifier.height(500.dp)
        ) {
            DemoScreen()
        }
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
    }
}

@Composable
fun DemoScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF6200EE), Color(0xFF3700B3))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "App Preview",
            color = Color.White,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun DeviceControls(
    orientation: DeviceOrientation,
    onOrientationChange: (DeviceOrientation) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        DeviceOrientation.PORTRAIT to "Portrait",
        DeviceOrientation.LANDSCAPE to "Landscape"
    )

    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { (value, label) ->
            val isSelected = value == orientation

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isSelected) Color.Black else Color.LightGray.copy(alpha = 0.3f)
                    )
                    .clickable { onOrientationChange(value) }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = label,
                    color = if (isSelected) Color.White else Color.Black
                )
            }
        }
    }
}
