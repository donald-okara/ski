package ke.don.ski.presentation.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ke.don.design.theme.dimens
import kotlinx.coroutines.launch
import kotlin.math.abs

/** Index-card colours, cycled card to card so the stack reads like a set of real flashcards. */
private val IndexCardColors = listOf(
    Color(0xFFFFFFFF), // white
    Color(0xFFFFF59D), // canary yellow
    Color(0xFFF8BBD0), // pink
    Color(0xFFBBDEFB), // blue
    Color(0xFFC8E6C9), // green
    Color(0xFFFFE0B2), // buff
)
private val IndexInk = Color(0xFF1B1B1B)
private val RuleBlue = Color(0xFF90CAF9)
private val MarginRed = Color(0xFFEF9A9A)

/** Drag distance, in px, past which a released card counts as swiped. */
private const val SWIPE_THRESHOLD = 300f
private const val FLY_DISTANCE = 2000f
private const val CARDS_BEHIND = 2

/**
 * Shows the slide's notes as a stack of index cards, one note per card, in the bottom-right quarter
 * of the slide. Swipe the top card away to reveal the next. Toggled with F, dismissed with Esc.
 *
 * @param notes The current slide's notes. Each non-blank note becomes one card.
 */
@Composable
fun FlashcardOverlay(
    notes: List<AnnotatedString>?,
    modifier: Modifier = Modifier
) {
    val cards = notes.orEmpty().map { it.text }.filter { it.isNotBlank() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(MaterialTheme.dimens.mediumPadding),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.5f).fillMaxHeight(0.5f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            if (cards.isEmpty()) {
                Text(
                    text = "No flashcards on this slide",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            } else {
                FlashcardStack(cards = cards)
            }
        }
    }
}

@Composable
private fun FlashcardStack(cards: List<String>) {
    var swiped by remember(cards) { mutableIntStateOf(0) }
    val finished = swiped >= cards.size

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = if (finished) "" else "Swipe the card away to reveal the next note",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f)
        ) {
            if (finished) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "All cards revealed",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    TextButton(onClick = { swiped = 0 }) {
                        Text(text = "Start over", color = Color.White)
                    }
                }
            } else {
                // Cards behind the top one peek out below it and shrink slightly.
                for (depth in CARDS_BEHIND downTo 1) {
                    val index = swiped + depth
                    if (index < cards.size) {
                        IndexCard(
                            text = cards[index],
                            index = index,
                            total = cards.size,
                            modifier = Modifier
                                .fillMaxSize()
                                .offset(y = (6 * depth).dp)
                                .graphicsLayer {
                                    scaleX = 1f - 0.05f * depth
                                    scaleY = 1f - 0.05f * depth
                                }
                        )
                    }
                }

                // Keyed by index so each card gets its own drag state.
                key(swiped) {
                    SwipeableCard(
                        text = cards[swiped],
                        index = swiped,
                        total = cards.size,
                        onSwiped = { swiped++ },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun SwipeableCard(
    text: String,
    index: Int,
    total: Int,
    onSwiped: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val dragX = remember { Animatable(0f) }

    IndexCard(
        text = text,
        index = index,
        total = total,
        modifier = modifier
            .graphicsLayer {
                translationX = dragX.value
                rotationZ = dragX.value / 30f
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, drag ->
                        change.consume()
                        scope.launch { dragX.snapTo(dragX.value + drag.x) }
                    },
                    onDragEnd = {
                        scope.launch {
                            if (abs(dragX.value) > SWIPE_THRESHOLD) {
                                val direction = if (dragX.value < 0) -FLY_DISTANCE else FLY_DISTANCE
                                dragX.animateTo(direction, tween(durationMillis = 250))
                                onSwiped()
                            } else {
                                dragX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                            }
                        }
                    },
                    onDragCancel = {
                        scope.launch { dragX.animateTo(0f) }
                    }
                )
            }
    )
}

/** One index card: ruled lines, a red margin, the note text, and a position label. */
@Composable
private fun IndexCard(
    text: String,
    index: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .shadow(6.dp, shape)
            .clip(shape)
            .background(IndexCardColors[index % IndexCardColors.size])
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val margin = 36.dp.toPx()
            drawLine(
                color = MarginRed,
                start = Offset(margin, 0f),
                end = Offset(margin, size.height),
                strokeWidth = 2f
            )
            val rule = 28.dp.toPx()
            var y = rule
            while (y < size.height) {
                drawLine(
                    color = RuleBlue,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f
                )
                y += rule
            }
        }

        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = IndexInk,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(start = 40.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
        )

        Text(
            text = "${index + 1} / $total",
            style = MaterialTheme.typography.labelSmall,
            color = IndexInk.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
        )
    }
}
