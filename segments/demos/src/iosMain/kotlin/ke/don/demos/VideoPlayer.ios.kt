package ke.don.demos

import ski.shared.resources.generated.resources.Res

/**
 * Bundled resources are resolved with Res.getUri. Not verified on this platform yet:
 * check that the player opens the bundled sample before relying on it.
 */
actual suspend fun resolveVideoUriForPlayer(videoUri: String): String {
    if (videoUri.startsWith("http://") || videoUri.startsWith("https://")) return videoUri
    return Res.getUri(videoUri)
}
