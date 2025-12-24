package com.example.components.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import com.example.components.frames.ExpressiveFrame
import com.example.components.frames.ExpressivePictureFrame
import com.example.components.previews.PreviewContainer
import com.example.components.previews.ThemePreviews
import com.example.resources.Resources
import org.jetbrains.compose.resources.DrawableResource

data class PictureFrameModel(
    val drawable: DrawableResource,
    val shape: RoundedPolygon,
    val backgroundColor: Color
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private class PictureFramePreviewProvider : PreviewParameterProvider<PictureFrameModel> {
    val images = listOf(
        Resources.Images.RAFAELLA,
        Resources.Images.IVANA,
        Resources.Images.IAN
    )

    val shapes = listOf(
        MaterialShapes.Sunny,
        MaterialShapes.Cookie4Sided,
        MaterialShapes.Cookie6Sided,
        MaterialShapes.Cookie7Sided,
        MaterialShapes.PixelCircle
    )

    val colors = listOf(
        Color(0xFF546524),
        Color(0xFF5B6146),
        Color(0xFF3A665E),
    )

    override val values: Sequence<PictureFrameModel>
        get() {
            return sequence {
                for (image in images) {
                    for (shape in shapes) {
                        for (color in colors) {
                            yield(
                                PictureFrameModel(
                                    drawable = image,
                                    shape = shape,
                                    backgroundColor = color
                                )
                            )
                        }
                    }
                }
            }
        }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@ThemePreviews
@Composable
fun PictureFramePreview(
    @PreviewParameter(PictureFramePreviewProvider::class) model: PictureFrameModel
) {
    PreviewContainer {
        Box(
            modifier = Modifier.padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            ExpressivePictureFrame(
                image = model.drawable,
                polygon = model.shape,
                backgroundColor = model.backgroundColor,
            )
        }
    }
}