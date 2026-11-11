package io.github.donald_okara.components.timeline

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ke.don.domain.LocalRevealStep
import ke.don.domain.values.Values
import kotlin.math.min

/**
 * One stage of a timeline: its year, a short title, a caption, and detail lines.
 *
 * @param year The year shown above the node, for example "2018".
 * @param title A short name for the stage, shown under the node.
 * @param caption A label shown at the top of the details card next to the year, for example a repo or team name.
 * @param details Two or three short lines shown when the stage is hovered, selected or last revealed.
 */
data class TimelineStage(
    val year: String,
    val title: String,
    val caption: String,
    val details: List<String>
)

/**
 * Colours for [EvolutionTimeline].
 *
 * @param lit Colour of lit nodes, the drawn line and lit years.
 * @param track Colour of unlit nodes and the undrawn line.
 * @param year Colour of unlit years and of the gap marker's year and "?".
 * @param title Colour of stage titles.
 * @param cardContainer Background of the details card.
 * @param cardCaption Colour of the year and caption on the details card.
 * @param cardDot Colour of the dots before each detail line.
 * @param cardText Colour of the detail lines.
 */
@Immutable
data class TimelineColors(
    val lit: Color,
    val track: Color,
    val year: Color,
    val title: Color,
    val cardContainer: Color,
    val cardCaption: Color,
    val cardDot: Color,
    val cardText: Color,
)

/** Default [EvolutionTimeline] colours, taken from the current Material theme. */
object TimelineDefaults {
    @Composable
    fun colors(
        lit: Color = MaterialTheme.colorScheme.primary,
        track: Color = MaterialTheme.colorScheme.outlineVariant,
        year: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        title: Color = MaterialTheme.colorScheme.onSurface,
        cardContainer: Color = MaterialTheme.colorScheme.surfaceContainer,
        cardCaption: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        cardDot: Color = MaterialTheme.colorScheme.secondaryContainer,
        cardText: Color = MaterialTheme.colorScheme.onSurface,
    ) = TimelineColors(lit, track, year, title, cardContainer, cardCaption, cardDot, cardText)
}

private val NodeRowHeight = 56.dp
private val NodeSize = 28.dp
private val LineStroke = 3.dp
private val CardMaxWidth = 320.dp

/**
 * A horizontal timeline with one node per [TimelineStage], a year above each node and a title below.
 *
 * Stage `i` is lit once the reveal step reaches `i + 1` (see [LocalRevealStep]). Lit nodes spring up and pulse
 * once; unlit nodes are muted. The line draws on to the highest lit stage over 500 ms.
 *
 * Hovering a stage, or clicking it to pin it, shows a details card under that stage.
 *
 * @param stages The stages in chronological order. Must not be empty.
 * @param modifier The modifier for the whole timeline.
 * @param gapAfterIndex The index of the stage after which a gap marker is drawn, or null for no gap. The marker
 *   is a dashed link with a "?" node, labelled with the years missing between the two stages (2021 for 2020 to 2022).
 * @param gapTitle The title under the gap marker.
 */
@Composable
fun EvolutionTimeline(
    stages: List<TimelineStage>,
    modifier: Modifier = Modifier,
    gapAfterIndex: Int? = null,
    gapTitle: String = "The gap",
    colors: TimelineColors = TimelineDefaults.colors(),
) {
    require(stages.isNotEmpty()) { "EvolutionTimeline needs at least one stage" }
    require(gapAfterIndex == null || gapAfterIndex in stages.indices) { "gapAfterIndex must index a stage" }

    val gapSlot = gapAfterIndex?.plus(1)
    val slotCount = stages.size + if (gapSlot != null) 1 else 0
    val gapYear = gapAfterIndex?.let { missingYears(stages[it].year, stages.getOrNull(it + 1)?.year.orEmpty()) }.orEmpty()

    fun slotOf(stageIndex: Int): Int =
        if (gapAfterIndex != null && stageIndex > gapAfterIndex) stageIndex + 1 else stageIndex

    val step = LocalRevealStep.current
    val litCount = min(step, stages.size).coerceAtLeast(0)
    val targetSlot = if (litCount == 0) 0f else slotOf(litCount - 1).toFloat()
    val drawn = animateFloatAsState(
        targetValue = targetSlot,
        animationSpec = tween(durationMillis = 500),
        label = "timelineDrawn"
    )

    var hoveredStage by remember { mutableStateOf<Int?>(null) }
    var selectedStage by remember { mutableStateOf<Int?>(null) }
    var shownStage by remember { mutableStateOf(0) }
    var rowHeightPx by remember { mutableStateOf(0) }
    var columnWidthPx by remember { mutableStateOf(0) }

    // The lit stage is the last one revealed. A click pins a stage until the next reveal step, so the lit stage
    // shows its details on every step. Hover previews any stage, and leaving it falls back to the pin or lit stage.
    val litStage = if (litCount > 0) litCount - 1 else null
    val activeStage = hoveredStage ?: selectedStage ?: litStage
    LaunchedEffect(step) {
        selectedStage = null
    }
    LaunchedEffect(activeStage) {
        activeStage?.let { shownStage = it }
    }

    // Maps a slot to its stage index, or null for the gap slot.
    fun stageAtSlot(slot: Int): Int? = when {
        gapSlot == null || slot < gapSlot -> slot
        slot == gapSlot -> null
        else -> slot - 1
    }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val slotWidth = maxWidth / slotCount
        val cardWidth = minOf(maxWidth, CardMaxWidth)
        val shownSlot = slotOf(shownStage)
        val cardLeft = (slotWidth * (shownSlot + 0.5f) - cardWidth / 2).coerceIn(0.dp, maxWidth - cardWidth)

        // One pointer region covers the node row and the details card, so hover does not drop when the pointer
        // moves from a stage down onto its card. Only pointers in the node row change the previewed stage.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { columnWidthPx = it.width }
                .pointerInput(slotCount, gapSlot) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Main)
                            val change = event.changes.firstOrNull() ?: continue
                            when (event.type) {
                                PointerEventType.Exit -> hoveredStage = null
                                PointerEventType.Enter, PointerEventType.Move -> {
                                    val inRow = change.position.y < rowHeightPx
                                    if (inRow && columnWidthPx > 0) {
                                        val slotPx = columnWidthPx.toFloat() / slotCount
                                        val slot = (change.position.x / slotPx).toInt().coerceIn(0, slotCount - 1)
                                        stageAtSlot(slot)?.let { hoveredStage = it }
                                    }
                                }
                                else -> Unit
                            }
                        }
                    }
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged { rowHeightPx = it.height }
            ) {
                for (slot in 0 until slotCount) {
                    val isGap = slot == gapSlot
                    val stageIndex = when {
                        gapSlot == null || slot < gapSlot -> slot
                        isGap -> -1
                        else -> slot - 1
                    }
                    val leftDashed = gapSlot != null && slot == gapSlot + 1
                    val rightDashed = gapSlot != null && slot == gapSlot - 1
                    if (isGap) {
                        TimelineSlot(
                            modifier = Modifier.weight(1f),
                            year = gapYear,
                            title = gapTitle,
                            isGap = true,
                            lit = false,
                            slot = slot,
                            slotCount = slotCount,
                            leftDashed = true,
                            rightDashed = true,
                            drawn = drawn,
                            colors = colors,
                            onClick = null
                        )
                    } else {
                        val stage = stages[stageIndex]
                        TimelineSlot(
                            modifier = Modifier.weight(1f),
                            year = stage.year,
                            title = stage.title,
                            isGap = false,
                            lit = stageIndex < litCount,
                            slot = slot,
                            slotCount = slotCount,
                            leftDashed = leftDashed,
                            rightDashed = rightDashed,
                            drawn = drawn,
                            colors = colors,
                            onClick = {
                                selectedStage = if (selectedStage == stageIndex) null else stageIndex
                            }
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = activeStage != null,
                modifier = Modifier
                    .offset(x = cardLeft)
                    .width(cardWidth),
                enter = fadeIn(tween(200)) + expandVertically(),
                exit = fadeOut(tween(150)) + shrinkVertically()
            ) {
                TimelineDetailsCard(stage = stages[shownStage.coerceIn(stages.indices)], colors = colors)
            }
        }
    }
}

/**
 * One column of the timeline: a year, a node sitting on the line, and a title.
 *
 * Each slot draws the half-links on either side of its node. Progress along the line comes from [drawn], so the
 * line fills in by reading the value inside the draw phase.
 */
@Composable
private fun TimelineSlot(
    year: String,
    title: String,
    isGap: Boolean,
    lit: Boolean,
    slot: Int,
    slotCount: Int,
    leftDashed: Boolean,
    rightDashed: Boolean,
    drawn: State<Float>,
    colors: TimelineColors,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()

    val wobble = remember { Animatable(0f) }
    LaunchedEffect(hovered) {
        if (isGap && hovered) {
            repeat(3) {
                wobble.animateTo(14f, tween(durationMillis = 80))
                wobble.animateTo(-14f, tween(durationMillis = 80))
            }
            wobble.animateTo(0f, tween(durationMillis = 120))
        }
    }

    val initiallyLit = remember { lit }
    val pulse = remember { Animatable(1f) }
    LaunchedEffect(lit) {
        if (lit && !initiallyLit) {
            pulse.animateTo(1.45f, tween(durationMillis = 160, easing = FastOutSlowInEasing))
            pulse.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }

    val nodeScale by animateFloatAsState(
        targetValue = if (lit) 1f else 0.7f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "timelineNodeScale"
    )
    val textAlpha by animateFloatAsState(
        targetValue = if (lit || isGap) 1f else 0.4f,
        animationSpec = tween(durationMillis = 300),
        label = "timelineTextAlpha"
    )

    val nodeColor = if (lit) colors.lit else colors.track
    val trackColor = colors.track
    val litColor = colors.lit
    val dash = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))

    val slotModifier = modifier
        .hoverable(interactionSource = interaction)
        .let { base ->
            if (onClick != null) {
                base.clickable(interactionSource = interaction, indication = null, onClick = onClick)
            } else {
                base
            }
        }

    Column(
        modifier = slotModifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Values.Dimens.tinyPadding)
    ) {
        Text(
            text = year,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = if (lit && !isGap) colors.lit else colors.year,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Values.Dimens.tinyPadding)
                .graphicsLayer { alpha = textAlpha }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(NodeRowHeight)
                .drawBehind {
                    val y = size.height / 2f
                    val w = size.width
                    val cx = w / 2f
                    val stroke = LineStroke.toPx()
                    val progress = drawn.value

                    if (slot > 0) {
                        drawLine(
                            color = trackColor,
                            start = Offset(0f, y),
                            end = Offset(cx, y),
                            strokeWidth = stroke,
                            pathEffect = if (leftDashed) dash else null
                        )
                    }
                    if (slot < slotCount - 1) {
                        drawLine(
                            color = trackColor,
                            start = Offset(cx, y),
                            end = Offset(w, y),
                            strokeWidth = stroke,
                            pathEffect = if (rightDashed) dash else null
                        )
                    }

                    // Link to the left: its second half lies in this slot.
                    val leftLink = (progress - (slot - 1)).coerceIn(0f, 1f)
                    val leftFrac = (leftLink * 2f - 1f).coerceIn(0f, 1f)
                    if (slot > 0 && leftFrac > 0f) {
                        drawLine(
                            color = litColor,
                            start = Offset(0f, y),
                            end = Offset(cx * leftFrac, y),
                            strokeWidth = stroke
                        )
                    }
                    // Link to the right: its first half lies in this slot.
                    val rightLink = (progress - slot).coerceIn(0f, 1f)
                    val rightFrac = (rightLink * 2f).coerceIn(0f, 1f)
                    if (slot < slotCount - 1 && rightFrac > 0f) {
                        drawLine(
                            color = litColor,
                            start = Offset(cx, y),
                            end = Offset(cx + (w - cx) * rightFrac, y),
                            strokeWidth = stroke
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(NodeSize)
                    .graphicsLayer {
                        val s = nodeScale * pulse.value
                        scaleX = s
                        scaleY = s
                    }
                    .clip(CircleShape)
                    .background(nodeColor),
                contentAlignment = Alignment.Center
            ) {
                if (isGap) {
                    Text(
                        text = "?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.year,
                        modifier = Modifier.graphicsLayer { rotationZ = wobble.value }
                    )
                }
            }
        }

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isGap) colors.year else colors.title,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Values.Dimens.tinyPadding)
                .graphicsLayer { alpha = textAlpha }
        )
    }
}

/**
 * The card shown under a hovered or selected stage: its caption and detail lines.
 */
@Composable
private fun TimelineDetailsCard(stage: TimelineStage, colors: TimelineColors, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Values.Dimens.smallPadding))
            .background(colors.cardContainer)
            .padding(Values.Dimens.mediumPadding),
        verticalArrangement = Arrangement.spacedBy(Values.Dimens.smallPadding)
    ) {
        Text(
            text = "${stage.year} · ${stage.caption}",
            style = MaterialTheme.typography.labelLarge.copy(fontFamily = FontFamily.Monospace),
            color = colors.cardCaption
        )
        stage.details.forEach { detail ->
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .size(Values.Dimens.tinyPadding)
                        .clip(CircleShape)
                        .background(colors.cardDot)
                )
                Spacer(modifier = Modifier.width(Values.Dimens.smallPadding))
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.cardText,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * The years missing between two stage years, for the gap marker label: "2021" for 2020 and 2022.
 */
private fun missingYears(before: String, after: String): String {
    val a = before.toIntOrNull() ?: return "?"
    val b = after.toIntOrNull() ?: return "?"
    return if (b - a == 2) "${a + 1}" else "${a + 1}–${b - 1}"
}
