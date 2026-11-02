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

    /** Reveal step on the current slide, from 0 (nothing revealed) to `currentSlide.steps`. */
    var currentStep by mutableStateOf(0)
        private set

    val currentSlide: SlideConfig
        get() = slides[currentIndex]

    fun next() {
        direction = NavDirection.Forward
        if (currentStep < currentSlide.steps) {
            currentStep++
        } else if (currentIndex < slides.lastIndex) {
            currentIndex++
            currentStep = 0
        }
    }

    fun previous() {
        direction = NavDirection.Backward
        if (currentStep > 0) {
            currentStep--
        } else if (currentIndex > 0) {
            currentIndex--
            currentStep = slides[currentIndex].steps
        }
    }

    fun goTo(index: Int, step: Int = 0) {
        if (index !in slides.indices) return
        val clampedStep = step.coerceIn(0, slides[index].steps)
        if (index == currentIndex && clampedStep == currentStep) return
        if (index != currentIndex) {
            direction = if (index > currentIndex) NavDirection.Forward else NavDirection.Backward
        }
        currentIndex = index
        currentStep = clampedStep
    }
}
