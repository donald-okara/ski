package com.example.components.components

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
import com.example.components.frames.AnimatedSnakeFramedCard
import com.example.components.frames.BrushType
import com.example.components.frames.ExpressivePictureFrame
import com.example.components.previews.DesktopPreview
import com.example.components.previews.PreviewContainer
import com.example.resources.Resources


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun FramedShowCase() {
    AnimatedSnakeFramedCard(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        header = {
            Row(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "🎉",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    "DevFest",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "🎉",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        },
        footer = {
            Row(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.secondaryContainer,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🎯", style = MaterialTheme.typography.bodyLarge)
                Text(
                    "GDG",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
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
                    image = Resources.Images.DON,
                    sizeDp = 400,
                    polygon = MaterialShapes.Sunny,
                    backgroundColor = MaterialTheme.colorScheme.tertiary,
                    brushType = BrushType.SWEEP
                )
            }
        }
    }
}

@DesktopPreview
@Composable
fun AnimatedSnakeCardPreview() {

    PreviewContainer {
        FramedShowCase()
    }
}
