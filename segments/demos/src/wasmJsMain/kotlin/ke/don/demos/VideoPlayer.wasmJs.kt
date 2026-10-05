package ke.don.demos

import ski.shared.resources.generated.resources.Res

/** Bundled resources are served by URL on the web. Remote URLs pass through unchanged. */
actual suspend fun resolveVideoUriForPlayer(videoUri: String): String {
    if (videoUri.startsWith("http://") || videoUri.startsWith("https://")) return videoUri
    return Res.getUri(videoUri)
}
