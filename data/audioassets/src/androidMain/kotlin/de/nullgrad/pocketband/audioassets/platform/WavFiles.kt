@file:OptIn(ExperimentalUnsignedTypes::class)

package de.nullgrad.pocketband.audioassets.platform

import de.nullgrad.pocketband.audioassets.model.AudioFile
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer

private fun InputStream.copyAll(out: OutputStream) {
    val buf = ByteArray(1024)
    var len: Int
    while ((read(buf).also { len = it }) > 0) {
        out.write(buf, 0, len)
    }
}

fun loadWavFile(path: String): AudioFile? {
    val file = File(path)
    if (file.exists()) {
        FileInputStream(file).use {
            return loadInputStream(it, path)
        }
    }
    return null
}

private fun loadInputStream(inputStream: InputStream, path: String): AudioFile? {
    ByteArrayOutputStream().use { outputStream ->
        inputStream.copyAll(outputStream)
        val bytes = outputStream.toByteArray()
        val byteBuffer = ByteBuffer.wrap(bytes)
        return loadWavBuffer(byteBuffer, path)
    }
}

private fun findChunkOffset(byteData: ByteBuffer, chunkId: Long): Int {
    var offset = 12
    while (offset < byteData.size()) {
        val id = readId(byteData, offset)
        if (id == chunkId) {
            return offset
        }
        val size = readU32(byteData, offset + 4)
        // skip chunk id, chunk size and chunk body to get to next one
        offset += 8 + size.toInt()
    }
    return -1
}

fun loadWavBuffer(byteData: ByteBuffer, sourcePath: String) : AudioFile? {
    val chunkRiff = readId(byteData, 0)
    if (chunkRiff != _wavChunkRIFF) {
        return null
    }

    val chunkFmtOffset = findChunkOffset(byteData, _wavChunkFmt)
    val chunkDataOffset = findChunkOffset(byteData, _wavChunkData)
    if (chunkFmtOffset < 0 || chunkDataOffset < 0) {
        return null
    }

    val fmtOffset = chunkFmtOffset + 8
    val audioFormat = readU16(byteData, fmtOffset)
    val numChannels = readS16(byteData, fmtOffset + 2).toInt()
    val sampleRate = readU32(byteData, fmtOffset + 4).toInt()
    val byteRate = readU32(byteData, fmtOffset + 8)
    val blockAlign = readS16(byteData, fmtOffset + 12).toInt()
    val bitsPerSample = readS16(byteData, fmtOffset + 14).toInt()

    if (audioFormat != _wavMediaFormatPCM) {
        return null
    }

    if (numChannels == 1 && bitsPerSample == 16) {
        val chunkDataSize = readU32(byteData, chunkDataOffset + 4).toInt()
        val numSamples = chunkDataSize / 2
        val dataOffset = chunkDataOffset + 8

        val floatDataL = FloatArray(numSamples)
        val floatDataR = FloatArray(numSamples)
        for (i in 0 until numSamples) {
            val pcm = readS16(byteData, dataOffset + i * blockAlign)
            val s = (pcm / 32768.0).coerceIn(-1.0, 1.0).toFloat()
            floatDataL[i] = s
            floatDataR[i] = s
        }
        return AudioFile(
            path = sourcePath,
            soundData = listOf(floatDataL, floatDataR),
            sampleRate = sampleRate
        )
    } else if (numChannels == 2 && bitsPerSample in setOf(8, 16, 24, 32)) {
        val chunkDataSize = readU32(byteData, chunkDataOffset + 4)
        val bytesPerSample = bitsPerSample / 8
        val numSamples = (chunkDataSize / numChannels / bytesPerSample).toInt()
        val dataOffset = chunkDataOffset + 8
        val maxRange = 1 shl (bitsPerSample - 1)

        val readValue = when (bitsPerSample) {
            32 -> ::readS32
            24 -> ::readS24
            16 -> ::readS16
            else -> ::readS8
        }

        val floatData = Array(2) { FloatArray(numSamples) }
        for (i in 0 until numSamples) {
            for (j in 0 until numChannels) {
                val pcm = readValue(byteData, dataOffset + i * blockAlign + j * bytesPerSample).toLong()
                val s = (pcm / maxRange.toDouble()).coerceIn(-1.0, 1.0)
                floatData[j][i] = s.toFloat()
            }
        }
        return AudioFile(
            path = sourcePath,
            soundData = floatData.toList(),
            sampleRate = sampleRate
        )
    } else {
        println("unknown WAV format: num channels $numChannels, block align $blockAlign")
        return null
    }
}

private const val _wavMediaFormatPCM : Int = 1
private const val _wavChunkRIFF = 0x52494646L // "RIFF"
private const val _wavChunkFmt = 0x666d7420L // "fmt "
private const val _wavChunkData = 0x64617461L // "data"
private const val _wavIdWAVE = 0x57415645L // "W

// numeric values are stored in little endian order

private fun ByteBuffer.size() : Int {
    position(0)
    return remaining()
}

private fun readS8(byteBuffer: ByteBuffer, offset: Int): Byte {
    return byteBuffer[offset]
}

private fun readS16(byteBuffer: ByteBuffer, offset: Int): Short {
    val v0 = byteBuffer[offset].toUByte().toInt()
    val x1 = byteBuffer[offset+1].toUByte().toInt()
    //println(x1)
    val v1 = byteBuffer[offset+1].toInt()
    return ((v1 shl 8) or v0).toShort()
}

private fun readU16(byteBuffer: ByteBuffer, offset: Int): Int {
    val v0 = byteBuffer[offset].toInt()
    val v1 = byteBuffer[offset+1].toInt()
    return (v1 shl 8) or v0
}

private fun readS24(byteBuffer: ByteBuffer, offset: Int): Int {
    val v0 = byteBuffer[offset].toUByte().toInt()
    val v1 = byteBuffer[offset+1].toUByte().toInt()
    val v2 = byteBuffer[offset+2].toInt()
    return (v2 shl 16) or (v1 shl 8) or v0
}

private fun readU24(byteBuffer: ByteBuffer, offset: Int): Int {
    val v0 = byteBuffer[offset].toUByte().toInt()
    val v1 = byteBuffer[offset+1].toUByte().toInt()
    val v2 = byteBuffer[offset+2].toUByte().toInt()
    return (v2 shl 16) or (v1 shl 8) or v0
}

private fun readS32(byteBuffer: ByteBuffer, offset: Int): Int {
    val v0 = byteBuffer[offset].toUByte().toLong()
    val v1 = byteBuffer[offset+1].toUByte().toLong()
    val v2 = byteBuffer[offset+2].toUByte().toLong()
    val v3 = byteBuffer[offset+3].toLong()
    return ((v3 shl 24) or (v2 shl 16) or (v1 shl 8) or v0).toInt()
}

private fun readU32(byteBuffer: ByteBuffer, offset: Int): Long {
    val v0 = byteBuffer[offset].toUByte().toLong()
    val v1 = byteBuffer[offset+1].toUByte().toLong()
    val v2 = byteBuffer[offset+2].toUByte().toLong()
    val v3 = byteBuffer[offset+3].toUByte().toLong()
    return (v3 shl 24) or (v2 shl 16) or (v1 shl 8) or v0
}

private fun readId(byteBuffer: ByteBuffer, offset: Int): Long {
    val v3 = byteBuffer[offset].toUByte().toLong()
    val v2 = byteBuffer[offset+1].toUByte().toLong()
    val v1 = byteBuffer[offset+2].toUByte().toLong()
    val v0 = byteBuffer[offset+3].toUByte().toLong()
    return (v3 shl 24) or (v2 shl 16) or (v1 shl 8) or v0
}

internal fun saveWavFile(path: String, soundData: FloatArray, sampleRate: Int) {
    val numSamples = soundData.size
    val numChannels = 2
    val bitsPerSample = 16
    val bytesPerSample = bitsPerSample / 8
    val blockAlign = bytesPerSample * numChannels

    val totalLength = 12 + (8 + 16) + (8 + numSamples * bytesPerSample * numChannels)
    val uint8List = UByteArray(totalLength)
    writeId(uint8List, 0, _wavChunkRIFF)
    writeU32(uint8List, 4, totalLength - 8L)
    writeId(uint8List, 8, _wavIdWAVE)

    // write fmt chunk
    writeId(uint8List, 12, _wavChunkFmt)
    writeU32(uint8List, 16, 16) // size of fmt chunk
    writeU16(uint8List, 20, _wavMediaFormatPCM) // format
    writeU16(uint8List, 22, numChannels) // num channels
    writeU32(uint8List, 24, sampleRate.toLong()) // sample rate
    writeU32(uint8List, 28, (sampleRate * numChannels).toLong()) // byte rate
    writeU16(uint8List, 32, blockAlign) // block align
    writeU16(uint8List, 34, bitsPerSample) // bits per sample

    // write data chunk
    writeId(uint8List, 36, _wavChunkData)
    writeU32(uint8List, 40, soundData.size * 2L)
    for (i in 0 until numSamples) {
        val s = soundData[i]
        val pcm = (s * 32768.0).toInt()
        writeS16(uint8List, 44 + i * bytesPerSample, pcm)
    }

    val file = File(path)
    file.writeBytes(uint8List.asByteArray())
}

// 32 bit IDs are stored in big endian order
private fun writeId(byteBuffer: UByteArray, offset: Int, id: Long) {
    byteBuffer[offset] = ((id shr 24) and 0xff).toUByte()
    byteBuffer[offset+1] = ((id shr 16) and 0xff).toUByte()
    byteBuffer[offset+2] = ((id shr 8) and 0xff).toUByte()
    byteBuffer[offset+3] = (id and 0xff).toUByte()
}

// numeric 32 bit values are stored in little endian order
fun writeU32(byteBuffer: UByteArray, offset: Int, value: Long) {
    byteBuffer[offset] = (value and 0xff).toUByte()
    byteBuffer[offset+1] = ((value shr 8) and 0xff).toUByte()
    byteBuffer[offset+2] = ((value shr 16) and 0xff).toUByte()
    byteBuffer[offset+3] = ((value shr 24) and 0xff).toUByte()
}

// numeric 16 bit values are stored in little endian order
fun writeU16(byteBuffer: UByteArray, offset: Int, value: Int) {
    byteBuffer[offset] = (value and 0xff).toUByte()
    byteBuffer[offset+1] = ((value shr 8) and 0xff).toUByte()
}

// numeric 16 bit values are stored in little endian order
fun writeS16(byteBuffer: UByteArray, offset: Int, value: Int) {
    byteBuffer[offset] = (value and 0xff).toUByte()
    byteBuffer[offset+1] = ((value shr 8) and 0xff).toUByte()
}
