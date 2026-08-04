package ke.don.domain

import kotlinx.serialization.Serializable

@Serializable
data class DeckSyncState(
    val slideIndex: Int,
    val direction: NavDirection,
)
