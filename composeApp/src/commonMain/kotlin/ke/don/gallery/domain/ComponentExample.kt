package ke.don.gallery.domain

import androidx.compose.runtime.Composable

data class ComponentExample(
    val label: String,
    val rendered: @Composable () -> Unit,
    val type: ComponentType? = null,
    val focusable: (@Composable (() -> Unit))? = null,
    val description: String = "",
    val dos: List<String> = emptyList(),
    val donts: List<String> = emptyList()
)

enum class ComponentType{
    Background, Frame, Guide, Icon, Layout, Picture, Timer
}