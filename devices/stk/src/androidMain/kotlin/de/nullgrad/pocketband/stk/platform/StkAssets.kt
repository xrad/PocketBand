package de.nullgrad.pocketband.stk.platform

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import kotlin.io.path.Path

external fun setRawWavePath(path: String)

internal fun setupStkAssets(context: Context): Boolean {
    var result = true

    val rawwaves = "rawwaves"

    val filesDir = context.filesDir
    try {
        val outputDir = File(filesDir, rawwaves)
        outputDir.mkdir()
        context.assets.list(rawwaves)?.forEach { assetName ->
            val assetInputPath = Path(rawwaves, assetName).toString()
            context.assets.open(assetInputPath).use { input ->
                val fileOutputPath = File(outputDir, assetName)
                if (!fileOutputPath.exists()) {
                    FileOutputStream(fileOutputPath).use { output ->
                        input.copyTo(output)
                    }
                }
            }
        }
        setRawWavePath(outputDir.path)
    }
    catch (e: Exception) {
        result = false
    }

    return result
}
