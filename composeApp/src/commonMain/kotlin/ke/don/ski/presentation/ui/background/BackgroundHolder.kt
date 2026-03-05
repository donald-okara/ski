package ke.don.ski.presentation.ui.background

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ke.don.resources.Resources
import org.jetbrains.compose.resources.painterResource

@Composable
fun BackgroundHolder(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        content()

        Image(
            painter = painterResource(Resources.Images.ANDROID_ROBOT),
            contentDescription = null,
            modifier = Modifier
                .padding(8.dp)
                .align(
                    Alignment.BottomEnd
                )
        )
    }
}