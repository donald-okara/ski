package ke.don.domain

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class DeckNavigator(
    private val slides: List<SlideConfig>
) {
    init {
        require(slides.isNotEmpty()) { "DeckNavigator requires at least one slide." }

    }
    var direction by mutableStateOf(NavDirection.Forward)

    var currentIndex by mutableStateOf(0)
        private set

    val currentSlide: SlideConfig
        get() = slides[currentIndex]

    fun next() {
        direction = NavDirection.Forward
        if (currentIndex < slides.lastIndex) currentIndex++
    }

    fun previous() {
        direction = NavDirection.Backward
        if (currentIndex > 0) currentIndex--
    }

    fun goTo(index: Int) {
        if (index !in slides.indices || index == currentIndex) return
        direction = if (index > currentIndex) NavDirection.Forward else NavDirection.Backward
        currentIndex = index
    }
}
