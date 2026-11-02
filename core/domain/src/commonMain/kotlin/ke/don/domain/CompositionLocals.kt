package ke.don.domain

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf

val LocalDeckMode = staticCompositionLocalOf<DeckMode> {
    error("DeckMode not provided")
}

/** Reveal step for the slide being rendered. The default shows everything outside a deck. */
val LocalRevealStep = compositionLocalOf { Int.MAX_VALUE }

val LocalSharesFrameFlag = staticCompositionLocalOf {
    false
}
