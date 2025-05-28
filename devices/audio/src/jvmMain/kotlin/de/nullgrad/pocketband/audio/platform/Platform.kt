package de.nullgrad.pocketband.audio.platform

import de.nullgrad.pocketband.audio.PocketPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

object JvmPlatform : Platform {
    override fun initialize() {
        runBlocking(Dispatchers.IO) {
            val libraryFile = kotlin.io.path.createTempFile().toFile()
            // Copy the library from resources to the temporary directory
            PocketPlayer::class.java.getResourceAsStream("/libpocketband.dylib")?.use { inputStream ->
                libraryFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            System.load(libraryFile.absolutePath)
            libraryFile.deleteOnExit()
        }
    }
}

internal actual fun getPlatform(): Platform = JvmPlatform