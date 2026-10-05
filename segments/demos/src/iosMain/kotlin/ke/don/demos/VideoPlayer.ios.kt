package ke.don.demos

// Remote http(s) URLs need no resolution on this platform.
// Bundle a local file here if a slide needs one.
actual suspend fun resolveVideoUriForPlayer(videoUri: String): String = videoUri
