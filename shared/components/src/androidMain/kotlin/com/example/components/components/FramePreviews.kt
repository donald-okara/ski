package com.example.components.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.components.frames.ExpressivePictureFrame
import com.example.components.previews.PreviewContainer
import com.example.components.previews.ThemePreviews

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@ThemePreviews
@Composable
fun PictureFramePreview() {
    PreviewContainer {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ExpressivePictureFrame {
                Text(
                    "Hello"
                )
            }
        }


    }
}