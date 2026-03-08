package ke.don.demos

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.donald_okara.components.guides.code_viewer.FocusKotlinViewer
import io.github.donald_okara.components.guides.code_viewer.KotlinCodeViewerCard
import io.github.donald_okara.components.guides.whiteboard.FocusWhiteboard
import io.github.donald_okara.components.guides.whiteboard.WhiteboardCard

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WhiteboardSlide(
    modifier: Modifier = Modifier
) {
    var isCardDark by remember {
        mutableStateOf(true)
    }
    var isFocusDark by remember {
        mutableStateOf(true)
    }
    var isFocused by remember {
        mutableStateOf(false)
    }

    var value by remember {
       mutableStateOf("")
    }

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.CenterStart
    ) {
        WhiteboardCard(
            modifier = Modifier.fillMaxWidth(0.7f),
            darkTheme = isCardDark,
            toggleFocus = { isFocused = !isFocused },
            toggleTheme = { isCardDark = !isCardDark },
            value = value,
            onValueChange = { value = it }
        )
    }

    if (isFocused){
        FocusWhiteboard(
            onDismiss = { isFocused = false },
            darkTheme = isFocusDark,
            toggleTheme = { isFocusDark = !isFocusDark },
            value = value,
            onValueChange = { value = it }
        )
    }
}