package ke.don.gallery.domain

import androidx.compose.runtime.Composable

data class ComponentExample(
    val label: String,
    val description: String,
    val rendered: @Composable () -> Unit,
    val type: ComponentType? = null,
    val focusable: Focusable? = null,
    val dos: List<String> = emptyList(),
    val donts: List<String> = emptyList()
)

data class Focusable(
    val path: String,
    val rendered: @Composable (onDismiss: () -> Unit) -> Unit
)

enum class ComponentType{
    Background, Frame, Guide, Icon, Layout, Picture, Timer
}