package ke.don.gallery.domain

import androidx.compose.runtime.Composable


@ComponentGalleryDsl
class ComponentGalleryBuilder {
    private val _components = mutableListOf<ComponentExample>()
    val components: List<ComponentExample> get() = _components

    fun component(
        label: String,
        rendered: @Composable () -> Unit,
        focusable: (@Composable (() -> Unit))? = null,
        description: String = "",
        dos: List<String> = emptyList(),
        donts: List<String> = emptyList()
    ) {
        _components += ComponentExample(
            label = label,
            rendered = rendered,
            focusable = focusable,
            description = description,
            dos = dos,
            donts = donts
        )
    }
}

fun componentGallery(block: ComponentGalleryBuilder.() -> Unit): List<ComponentExample> {
    val builder = ComponentGalleryBuilder()
    builder.block()
    return builder.components
}