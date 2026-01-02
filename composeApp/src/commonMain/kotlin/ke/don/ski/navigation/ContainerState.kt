package ke.don.ski.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer
import ke.don.domain.NavDirection
import ke.don.domain.Screen

@Composable
fun rememberContainerState(
    screen: Screen = Screen.Introduction,
    direction: NavDirection = NavDirection.Forward
): ContainerState {
    val screenValue =
        rememberSerializable(
            screen,
            serializer = MutableStateSerializer(Screen.serializer()),
        ) {
            mutableStateOf(screen)
        }

    val directionValue =
        rememberSerializable(
            direction,
            serializer = MutableStateSerializer(NavDirection.serializer()),
        ) {
            mutableStateOf(direction)
        }
    val containerState = remember { ContainerState(screenValue, directionValue) }
    return containerState
}

class ContainerState(
    screen: MutableState<Screen>,
    direction: MutableState<NavDirection>
){
    var screen: Screen by screen
    var direction: NavDirection by direction
}

data class DeckState(
    val screenIndex: Int,
    val direction: NavDirection = NavDirection.Forward
)
