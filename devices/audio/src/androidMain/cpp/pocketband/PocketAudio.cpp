//
// Created by xrad on 16.02.23.
//

#include <cassert>
#include <algorithm>

#include "PocketAudio.h"

#include "log.h"

bool PocketAudioBase::startAudio(int bufferSize, bool record) {
    assert(bufferSize > 32);
    recording = record;

    buffers[0] = {
            .data = new float [bufferSize],
            .size = bufferSize,
            .length = 0,
            .readIndex = 0,
    };
    assert(buffers[0].data != nullptr);
    buffers[1] = {
            .data = new float [bufferSize],
            .size = bufferSize,
            .length = 0,
            .readIndex = 0,
    };
    assert(buffers[1].data != nullptr);

    if (!record) {
        // for playback, make both buffers available for sender
        signalBuffer(0);
        signalBuffer(1);
    }
    else {
        // for record, make both buffers available for capture
        buffers[0].length = bufferSize / kChannelCount;
        buffers[1].length = bufferSize / kChannelCount;
    }
    rdIndex = 0;

    inputRms = new InputRMS(kSampleRate);

    return startStream(bufferSize);
}

void PocketAudioBase::stopAudio() {
    stopStream();
    if (buffers[0].data != nullptr) {
        delete [] buffers[0].data;
        buffers[0].data = nullptr;
    }
    if (buffers[1].data != nullptr) {
        delete [] buffers[1].data;
        buffers[1].data = nullptr;
    }
    int dummy;
    while(wqueue.try_dequeue(dummy))
        ;
    if (inputRms != nullptr) {
        delete inputRms;
        inputRms = nullptr;
    }
}

int PocketAudioBase::getSampleRate() const {
    return sampleRate;
}

Buffer* PocketAudioBase::waitForBuffer() {
    int index;
    wqueue.wait_dequeue(index);
    return &buffers[index];
}

float PocketAudioBase::waitRMS() {
    float rms;
    event_queue.wait_dequeue(rms);
    return rms;
}

void PocketAudioBase::signalRMS(float rms) {
    event_queue.try_enqueue(rms);
}

void PocketAudioBase::signalBuffer(int bufferIndex) {
    wqueue.try_enqueue(bufferIndex);
}

void PocketAudioBase::callback(float *audioData, int32_t numFrames) {
    auto current = &buffers[rdIndex];

    if (recording) {
        for (int i = 0; i < numFrames; i++) {
            float inL = ((float*)audioData)[i * 2];
            float inR = ((float*)audioData)[i * 2 + 1];

            auto rms = inputRms->processFrame(inL, inR);
            if (rms != nullptr) {
                signalRMS(*rms);
            }

            // no more buffer space from app - overrun
            if (current->length == 0) {
                continue;
            }

            // buffer full - overrun
            if (current->readIndex >= current->length) {
                continue;
            }

            auto j = current->readIndex++;

            current->data[j * 2] = inL;
            current->data[j * 2 + 1] = inR;

            if (current->readIndex >= current->length) {
                signalBuffer(rdIndex);
                rdIndex ^= 1;
                current = &buffers[rdIndex];
            }
        }
    }
    else {
        for (int i = 0; i < numFrames; i++) {
            // no more data from app - underrun
            if (current->length == 0) {
                ((float*)audioData)[i * 2] = 0;
                ((float*)audioData)[i * 2 + 1] = 0;
                continue;
            }

            auto j = current->readIndex++;

            ((float*)audioData)[i * 2] = std::clamp(current->data[j * 2], -1.0f, 1.0f);
            ((float*)audioData)[i * 2 + 1] = std::clamp(current->data[j * 2 + 1], -1.0f, 1.0f);

            if (current->readIndex >= current->length) {
                current->length = 0;
                current->readIndex = 0;
                signalBuffer(rdIndex);
                rdIndex ^= 1;
                current = &buffers[rdIndex];
            }
        }
    }
}

float* InputRMS::processFrame(float inL, float inR) {
    float *result = nullptr;

    rmsSum += inL * inL + inR * inR;
    rmsCount++;
    if (rmsCount >= rmsBlockSize) {
        float blockRMS = sqrt(rmsSum / (float)rmsCount / 2);
        if (blockRMS < .0001) blockRMS = 0;

        if (baselineSamples < baselineCalibrationFrames) {
            baselineRMS += blockRMS;
            baselineSamples++;
            if (baselineSamples == baselineCalibrationFrames) {
                baselineRMS /= (float)baselineSamples;   // Average baseline level
                threshold = baselineRMS * 0.5f;    // Set initial threshold as 50% of baseline
            }
        }

        adaptiveRMS = smoothingFactor * adaptiveRMS + (1 - smoothingFactor) * blockRMS;

        // Update the maximum RMS value observed
        if (adaptiveRMS > maxRMS) {
            maxRMS = adaptiveRMS;
        }

        // Normalize adaptive RMS to a range of 0 to 1
        normalizedRMS = adaptiveRMS / maxRMS;

        // Adjust threshold dynamically based on average RMS over time
        longTermRMS = 0.99f * longTermRMS + 0.01f * blockRMS;
        threshold = std::max(threshold * decayFactor, longTermRMS * 0.1f);

        // Gradually reduce maxRMS when input signal is low
        if (blockRMS < threshold && maxRMS > threshold) {
            maxRMS *= decayFactor;  // Gradually reduce maxRMS
        }

        if (abs(normalizedRMS - rmsLast) > .01) {
            result = &normalizedRMS;
            rmsLast = normalizedRMS;
        }
        rmsCount = 0;
        rmsSum = 0;
    }
    return result;
}