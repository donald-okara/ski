import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import ke.don.domain.Slide
import ke.don.ski.Deck
import ke.don.ski.navigation.DeckNavigator
import ke.don.ski.navigation.rememberContainerState
import ke.don.ski.presentation.DeckMode

@Composable
expect fun DeckWebImpl()