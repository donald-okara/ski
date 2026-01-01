package ke.don.ski

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform