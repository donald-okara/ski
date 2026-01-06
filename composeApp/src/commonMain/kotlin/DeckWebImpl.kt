import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import ke.don.domain.Slide
import ke.don.ski.Deck
import ke.don.ski.navigation.DeckNavigator
import ke.don.ski.navigation.rememberContainerState
import ke.don.ski.presentation.DeckMode

/**
 * Composable entry point that renders the deck UI for the Web target.
 *
 * Implementations on each platform must provide the actual UI composition for displaying and interacting
 * with the deck presentation when running on the Web.
 */
@Composable
expect fun DeckWebImpl()