package ke.don.preview.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ke.don.components.frames.AnimatedSnakeFramedCard
import ke.don.components.picture.BrushType
import ke.don.components.picture.ExpressivePictureFrame
import ke.don.resources.Resources


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FramedShowCase() {
    AnimatedSnakeFramedCard(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        header = {
            Text("Header")
        },
        footer = {
            Text("Footer")
        }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    "Building Slides with Jetpack Compose on Ski",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Donald Isoe",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                ExpressivePictureFrame(
                    image = Resources.Images.RAFAELLA,
                    sizeDp = 400,
                    polygon = MaterialShapes.Sunny,
                    backgroundColor = MaterialTheme.colorScheme.tertiary,
                    brushType = BrushType.SWEEP
                )
            }
        }
    }
}