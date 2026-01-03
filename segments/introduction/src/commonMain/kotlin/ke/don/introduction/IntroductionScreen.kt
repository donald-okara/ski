package ke.don.introduction

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
import io.github.donald_okara.components.picture.BrushType
import io.github.donald_okara.components.picture.ExpressivePictureFrame
import ke.don.resources.Resources
import org.jetbrains.compose.resources.DrawableResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun IntroductionScreen(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
    ) {
        TextSegment(
            title = "Building Slides with Jetpack Compose on Ski",
            presenter = "Jane Doe"
        )

        ImageSegment(
            painter = Resources.Images.RAFAELLA
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ImageSegment(
    modifier: Modifier = Modifier,
    painter: DrawableResource
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxHeight()
    ) {
        ExpressivePictureFrame(
            image = painter,
            sizeDp = 400,
            polygon = MaterialShapes.Clover4Leaf,
            backgroundColor = MaterialTheme.colorScheme.tertiary,
            brushType = BrushType.SWEEP
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TextSegment(
    modifier: Modifier = Modifier,
    title: String,
    presenter: String,
    organisation: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth(0.5f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            title,
            style = MaterialTheme.typography.displayMediumEmphasized,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            presenter,
            style = MaterialTheme.typography.bodyLarge
        )

        organisation?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                organisation,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}