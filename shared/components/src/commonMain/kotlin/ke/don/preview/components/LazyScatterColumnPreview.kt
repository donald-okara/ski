package ke.don.preview.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ke.don.components.layout.LazyScatterFlow
import ke.don.components.picture.BrushType
import ke.don.components.picture.ExpressivePictureFrame
import ke.don.resources.Resources


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
val frames = listOf(
    PictureFrameModel(
        drawable = Resources.Images.IAN,
        shape = MaterialShapes.Cookie4Sided,
        size = 300,
        backgroundColor = Color(0xFF546524)
    ),
    PictureFrameModel(
        drawable = Resources.Images.RAFAELLA,
        shape = MaterialShapes.Sunny,
        size = 200,
        backgroundColor = Color(0xFF5B6146)
    ),
    PictureFrameModel(
        drawable = Resources.Images.IVANA,
        shape = MaterialShapes.Cookie7Sided,
        size = 100,
        backgroundColor = Color(0xFF3A665E)
    ),
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ScatterBoxShowcase(
) {
    LazyScatterFlow(
        items = frames,
        itemsPerRow = 2,
        modifier = Modifier
            .fillMaxSize()
    ) { index, item ->
        ExpressivePictureFrame(
            image = item.drawable,
            index = index,
            sizeDp = item.size,
            polygon = item.shape,
            backgroundColor = item.backgroundColor,
            brushType = BrushType.SWEEP,
        )
    }
}