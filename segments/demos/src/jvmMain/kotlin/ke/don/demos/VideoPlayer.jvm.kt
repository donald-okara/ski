package ke.don.demos

import java.io.File
import ski.shared.resources.generated.resources.Res

/**
 * JVM players need a file: URI. Bundled resources are copied to the temp directory
 * once, then the copy is reused while its size matches.
 */
actual suspend fun resolveVideoUriForPlayer(videoUri: String): String {
    if (videoUri.startsWith("http://") || videoUri.startsWith("https://")) return videoUri

    val bytes = Res.readBytes(videoUri)
    val file = File(System.getProperty("java.io.tmpdir"), "ski_${videoUri.substringAfterLast('/')}")
    if (!file.exists() || file.length() != bytes.size.toLong()) {
        file.writeBytes(bytes)
    }
    return file.toURI().toString()
}
