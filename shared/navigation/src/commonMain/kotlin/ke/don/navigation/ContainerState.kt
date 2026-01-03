package ke.don.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer
import ke.don.domain.NavDirection
import ke.don.domain.Slide

@Composable
fun rememberContainerState(
    slide: Slide = Slide.Introduction,
    direction: NavDirection = NavDirection.Forward
): ContainerState {
    val slideValue =
        rememberSerializable(
            slide,
            serializer = MutableStateSerializer(Slide.serializer()),
        ) {
            mutableStateOf(slide)
        }

    val directionValue =
        rememberSerializable(
            direction,
            serializer = MutableStateSerializer(NavDirection.serializer()),
        ) {
            mutableStateOf(direction)
        }
    val containerState = remember { ContainerState(slideValue, directionValue) }
    return containerState
}

class ContainerState(
    slide: MutableState<Slide>,
    direction: MutableState<NavDirection>
){
    var slide: Slide by slide
    var direction: NavDirection by direction
}

data class DeckState(
    val screenIndex: Int,
    val direction: NavDirection = NavDirection.Forward
)
