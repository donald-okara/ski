package ke.don.components.guides.keys_shortcuts

import androidx.compose.ui.input.key.Key

sealed class KeyEventHandler(
    val hint: String,
    val keys: Set<Key>,
) {
    fun matches(key: Key): Boolean = key in keys

    val shortcuts = listOf(
        Next,
        Previous,
        SwitchTheme,
        ShowToolBar,
        ShowTableOfContent,
    )

    data object Next : KeyEventHandler(
        hint = "Next Slide",
        keys = setOf(Key.DirectionRight, Key.Spacebar, Key.Enter)
    )

    data object Previous : KeyEventHandler(
        hint = "Previous Slide",
        keys = setOf(Key.DirectionLeft, Key.Backspace)
    )

    data object SwitchTheme : KeyEventHandler(
        hint = "Switch Theme",
        keys = setOf(Key.D)
    )

    data object ShowToolBar : KeyEventHandler(
        hint = "Show/Hide Toolbar",
        keys = setOf(Key.DirectionUp)
    )

    data object ShowTableOfContent : KeyEventHandler(
        hint = "Show/Hide Table of Content",
        keys = setOf(Key.T)
    )

    data object ShowShortcutGuide : KeyEventHandler(
        hint = "Show/Hide Shortcuts Guide",
        keys = setOf(Key.C)
    )

    data object DismissAll : KeyEventHandler(
        hint = "Dismiss All",
        keys = setOf(Key.Escape)
    )
}

val DeckShortcuts = listOf(
    KeyEventHandler.Next,
    KeyEventHandler.Previous,
    KeyEventHandler.SwitchTheme,
    KeyEventHandler.ShowToolBar,
    KeyEventHandler.ShowTableOfContent,
    KeyEventHandler.ShowShortcutGuide,
    KeyEventHandler.DismissAll
)


fun Key.displayName(): String = when (this) {
    Key.DirectionRight -> "→"
    Key.DirectionLeft -> "←"
    Key.Spacebar -> "Space"
    Key.Enter -> "Enter"
    Key.Backspace -> "BackSpace"
    Key.T -> "T"
    Key.D -> "D"
    Key.C -> "C"
    Key.DirectionUp -> "↑"
    Key.Escape -> "Esc"
    else -> Key.toString()
}
