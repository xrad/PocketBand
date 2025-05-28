package de.nullgrad.pocketband.audioassets.platform

import android.content.Context
import de.nullgrad.pocketband.audioassets.model.AudioAsset
import java.io.File

internal fun getBuiltinWavDir(context: Context): String {
    val filesDir = context.filesDir
    return File(File(filesDir, "wav"), "builtin").absolutePath
}

internal fun getUserWavDir(context: Context): String {
    val filesDir = context.filesDir
    return File(File(filesDir, "wav"), "user").absolutePath
}

internal fun syncAudioAssets(context: Context) {
    val builtInDir = getBuiltinWavDir(context)
    val assets = context.assets
    assets.list("wav")?.forEach {
        val inputStream = assets.open("wav/$it")
        val file = File(builtInDir, it)
        if (!file.exists()) {
            File(builtInDir).mkdirs()
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        inputStream.close()
    }
}

internal fun getAudioAssets(context: Context) : List<AudioAsset> {
    val result = mutableListOf<AudioAsset>()

    File(getBuiltinWavDir(context)).listFiles()?.map {
        AudioAsset(
            path = it.path,
            isBuiltIn = true,
            lastModified = it.lastModified()
        )
    }?.let {
        result.addAll(it.toList())
    }

    File(getUserWavDir(context)).listFiles()?.map {
        AudioAsset(
            path = it.path,
            isBuiltIn = false,
            lastModified = it.lastModified()
        )
    }?.let {
        result.addAll(it.toList())
    }

    return result
}
