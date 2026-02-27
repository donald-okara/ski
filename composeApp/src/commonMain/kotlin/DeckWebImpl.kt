import androidx.compose.runtime.Composable

/**
 * Composable entry point that renders the deck UI for the Web target.
 *
 * Implementations on each platform must provide the actual UI composition for displaying and interacting
 * with the deck presentation when running on the Web.
 */
@Composable
expect fun DeckWebImpl()