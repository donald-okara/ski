package io.github.donald_okara.components.guides.notes

import androidx.compose.ui.text.AnnotatedString

data class Notes(
    val title: String = "Presenter Notes",
    val points: List<AnnotatedString>
)
