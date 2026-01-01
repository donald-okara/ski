package ke.don.preview.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import ke.don.components.picture.BrushType
import ke.don.components.picture.ExpressivePictureFrame
import ke.don.components.picture.frameShapes
import ke.don.resources.Resources
import org.jetbrains.compose.resources.DrawableResource

data class PictureFrameModel(
    val drawable: DrawableResource,
    val shape: RoundedPolygon,
    val backgroundColor: Color,
    val size: Int = 200,
)

val images = listOf(
    Resources.Images.RAFAELLA,
//    Resources.Images.RAFAELLA,
//    Resources.Images.IVANA,
//    Resources.Images.IAN,
)

val colors = listOf(
    Color(0xFF546524),
//    Color(0xFF5B6146),
//    Color(0xFF3A665E)
)

val values: Sequence<PictureFrameModel>
    get() {
        return sequence {
            for (image in images) {
                for (shape in frameShapes) {
                    for (color in colors) {
                        yield(
                            PictureFrameModel(
                                drawable = image,
                                shape = shape,
                                backgroundColor = color,
                            )
                        )
                    }
                }
            }
        }
    }

@Composable
fun FramesShowcase(
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier
    ) {
        values.forEach { entry ->
            Box(
                modifier = Modifier.padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                ExpressivePictureFrame(
                    image = entry.drawable,
                    sizeDp = 200,
                    polygon = entry.shape,
                    backgroundColor = entry.backgroundColor,
                    brushType = BrushType.SWEEP,
                )
            }
        }
    }
}
//@OptIn(ExperimentalMaterial3ExpressiveApi::class)
//@Preview
//@Composable
//fun PictureFramePreview(
//    @PreviewParameter(PictureFramePreviewProvider::class) model: PictureFrameModel
//) {
//    val color = remember { mutableStateOf(model.backgroundColor) }
//    PreviewContainer {
//        Box(
//            modifier = Modifier.padding(8.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            ExpressivePictureFrame(
//                image = model.drawable,
//                sizeDp = 200,
//                polygon = model.shape,
//                backgroundColor = color.value,
//                brushType = BrushType.SWEEP,
//                modifier = Modifier
//                    .clickable(
//                        onClick = {
//                            color.value = colors.random()
//                        }
//                    )
//            )
//        }
//    }
//}