package de.nullgrad.pocketband.stk.platform

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

object StkDesktopPlatform : Platform {

    override fun initialize() {
        runBlocking(Dispatchers.IO) {
            val libraryFile = kotlin.io.path.createTempFile().toFile()
            // Copy the library from resources to the temporary directory
            StkDesktopPlatform::class.java.getResourceAsStream("/libstk.dylib")?.use { inputStream ->
                libraryFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            System.load(libraryFile.absolutePath)
            libraryFile.deleteOnExit()
            //setupStkAssets(app.applicationContext)
        }
    }
}

internal actual fun getPlatform(): Platform = StkDesktopPlatform

