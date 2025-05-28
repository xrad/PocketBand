//
// Created by xrad on 10.12.24.
//

#include <cassert>
#include <algorithm>

#include "PocketAudio.h"

#include "log.h"

static void outputTrampoline(void* userData, AudioQueueRef inAQ, AudioQueueBufferRef inBuffer) {
    auto instance = reinterpret_cast<PocketAudio*>(userData);
    instance->audioToolboxCb(inAQ, inBuffer);
}

static void inputTrampoline(
    void* userData, AudioQueueRef inAQ, AudioQueueBufferRef inBuffer,
    const AudioTimeStamp *timeStamp, UInt32 numPacketDescs, const AudioStreamPacketDescription *packetDescs) {
    auto instance = reinterpret_cast<PocketAudio*>(userData);
    instance->audioToolboxCb(inAQ, inBuffer);
}

bool PocketAudio::startStream(int bufferSize) {
    sampleRate = kSampleRate;

    AudioStreamBasicDescription format = {};
    format.mSampleRate = kSampleRate;
    format.mFormatID = kAudioFormatLinearPCM;
    format.mFormatFlags = kAudioFormatFlagIsFloat;
    format.mBitsPerChannel = sizeof(float) * 8;
    format.mChannelsPerFrame = kChannelCount;
    format.mBytesPerFrame = sizeof(float) * kChannelCount;
    format.mFramesPerPacket = 1;
    format.mBytesPerPacket = format.mBytesPerFrame * format.mFramesPerPacket;

    if (recording) {
        if (AudioQueueNewInput(&format, inputTrampoline, this, nullptr, nullptr, 0, &audioQueue) != noErr) {
            return false;
        }
    }
    else {
        if (AudioQueueNewOutput(&format, outputTrampoline, this, nullptr, nullptr, 0, &audioQueue) != noErr) {
            return false;
        }
    }

    auto bufferSizeBytes = bufferSize * format.mBytesPerFrame;
    for (int i=0; i<2; ++i) {
        AudioQueueBufferRef bufferRef;
        if (AudioQueueAllocateBuffer(audioQueue, bufferSizeBytes, &bufferRef) != noErr) {
            return false;
        }
        memset(bufferRef->mAudioData, 0, bufferSizeBytes);
        if (recording) {
            bufferRef->mAudioDataByteSize = 0;
        }
        else {
            bufferRef->mAudioDataByteSize = bufferSizeBytes;
        }
        AudioQueueEnqueueBuffer(audioQueue, bufferRef, 0, nullptr);
    }

    if (AudioQueueStart(audioQueue, nullptr) != noErr) {
        AudioQueueDispose(audioQueue, true);
        return false;
    }

    return true;
}

void PocketAudio::stopStream() {
    if (audioQueue != nullptr) {
        AudioQueueStop(audioQueue, true);
        AudioQueueDispose(audioQueue, true);
        audioQueue = nullptr;
    }
}

void PocketAudio::audioToolboxCb(AudioQueueRef inAQ, AudioQueueBufferRef inBuffer) {
    auto numFrames = (recording)
        ? inBuffer->mAudioDataByteSize / (kChannelCount * sizeof(float))
        : inBuffer->mAudioDataBytesCapacity / (kChannelCount * sizeof(float));
    auto audioData = reinterpret_cast<float*>(inBuffer->mAudioData);

    callback(reinterpret_cast<float*>(audioData), numFrames);

    inBuffer->mAudioDataByteSize = recording ? 0 : inBuffer->mAudioDataBytesCapacity;
    AudioQueueEnqueueBuffer(inAQ, inBuffer, 0, nullptr);
}
