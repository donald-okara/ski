package ke.don.ski.domain

import androidx.compose.runtime.staticCompositionLocalOf

val LocalDeckMode = staticCompositionLocalOf<DeckMode> {
    error("DeckMode not provided")
}