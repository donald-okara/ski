package ke.don.ski.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ke.don.domain.NavDirection
import ke.don.ski.domain.SlideConfig

class DeckNavigator(
    private val slides: List<SlideConfig>
) {
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
        direction = if (index > currentIndex) NavDirection.Forward else NavDirection.Backward
        if (index in slides.indices) {
            currentIndex = index
        }
    }
}