package ke.don.domain

import androidx.compose.runtime.staticCompositionLocalOf

val LocalDeckMode = staticCompositionLocalOf<DeckMode> {
    error("DeckMode not provided")
}

val LocalSharesFrameFlag = staticCompositionLocalOf {
    false
}
