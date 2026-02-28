package ke.don.ski.logo

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.donald_okara.components.SkiLogo
import ke.don.design.theme.AppTheme

@Preview
@Composable
fun TwoBulletsPreview() {
    AppTheme(darkTheme = true){
        Surface{
            SkiLogo(
                modifier = Modifier.padding(8.dp),
                height = 60,
                spacing = 16.dp
            )
        }
    }
}