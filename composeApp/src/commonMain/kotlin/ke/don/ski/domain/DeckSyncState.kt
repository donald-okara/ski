package ke.don.ski.domain

import ke.don.domain.NavDirection
import kotlinx.serialization.Serializable

@Serializable
data class DeckSyncState(
    val slideIndex: Int,
    val direction: NavDirection,
)