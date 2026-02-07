package ke.don.demos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import io.github.donald_okara.components.guides.code_viewer.FocusKotlinViewer
import io.github.donald_okara.components.guides.code_viewer.KotlinCodeViewer
import io.github.donald_okara.components.guides.code_viewer.KotlinCodeViewerCard


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun KodeViewerSlide(
    modifier: Modifier = Modifier
) {
    var isDark by remember {
        mutableStateOf(true)
    }
    var isFocused by remember {
        mutableStateOf(false)
    }

    val code =  "@Composable\n" +
            "fun KodeViewerSlide(\n" +
            "    modifier: Modifier = Modifier\n" +
            ") {\n" +
            "    Box(\n" +
            "        modifier = modifier\n" +
            "            .fillMaxSize(),\n" +
            "        contentAlignment = Alignment.CenterStart\n" +
            "    ) {\n" +
            "        KotlinCodeViewer()\n" +
            "    }\n" +
            "}"

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.CenterStart
    ) {
        KotlinCodeViewerCard(
            modifier = Modifier.fillMaxWidth(0.7f),
            darkTheme = isDark,
            toggleFocus = { isFocused = !isFocused },
            toggleTheme = { isDark = !isDark }
        ){
           code
        }
    }

    if (isFocused){
        FocusKotlinViewer(
            onDismiss = { isFocused = false },
            darkTheme = isDark,
            toggleTheme = { isDark = !isDark }
        ){
            code
        }
    }
}