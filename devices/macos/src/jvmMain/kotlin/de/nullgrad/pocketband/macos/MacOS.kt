package de.nullgrad.pocketband.macos

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

object MacOS {
    init {
        runBlocking(Dispatchers.IO) {
            val libraryFile = kotlin.io.path.createTempFile().toFile()
            // Copy the library from resources to the temporary directory
            MacOS::class.java.getResourceAsStream("/libmacos.dylib")?.use { inputStream ->
                libraryFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            System.load(libraryFile.absolutePath)
            libraryFile.deleteOnExit()
        }
    }

    external fun requestMicrophonePermission()
    external fun getMicrophonePermissionStatus() : Int
}
