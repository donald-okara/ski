package io.github.donald_okara.components.layout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import io.github.donald_okara.components.guides.code_viewer.KotlinCodeViewerCard
import ke.don.domain.values.Values
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * A two-column slide for a live demo: the explanation and code on the left, the demo on the right.
 *
 * Content animates in once after the first composition, so every demo slide enters the same way.
 *
 * @param title Heading shown above the explanation.
 * @param explanation Short points shown under the title, one per line.
 * @param code Source shown in a [KotlinCodeViewerCard]. Pass the snippet the demo illustrates.
 * @param header Optional content above both columns, for example a metrics banner.
 * @param content The live demo, shown on the right.
 */
@Composable
fun DemoLayout(
    title: String,
    explanation: List<String>,
    code: String,
    modifier: Modifier = Modifier,
    header: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    var entered by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(50.milliseconds)
        entered = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Values.Dimens.largePadding),
        verticalArrangement = Arrangement.spacedBy(Values.Dimens.mediumPadding)
    ) {
        header?.invoke()

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(Values.Dimens.largePadding)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Values.Dimens.mediumPadding)
            ) {
                AnimatedVisibility(
                    visible = entered,
                    enter = fadeIn(animationSpec = tween(500)) +
                            slideInHorizontally(animationSpec = tween(500)) { -30 }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Values.Dimens.smallPadding)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        explanation.forEach { point ->
                            Text(
                                text = "• $point",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = entered,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 100))
                ) {
                    KotlinCodeViewerCard(
                        modifier = Modifier.fillMaxWidth(),
                        code = { code }
                    )
                }
            }

            val demoAlpha by animateFloatAsState(
                targetValue = if (entered) 1f else 0f,
                animationSpec = tween(durationMillis = 600, delayMillis = 150)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .graphicsLayer { alpha = demoAlpha },
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    }
}
