//
// Created by xrad on 16.02.23.
//

#ifndef POCKETBAND_POCKETAUDIO_H
#define POCKETBAND_POCKETAUDIO_H

#include <memory>

#include "readerwriterqueue.h"

using namespace moodycamel;

class Buffer {
public:
    float *data {};
    int size {};
    int length {};
    int readIndex {};
};

class InputRMS {
public:
    explicit InputRMS(int sampleRate) { rmsBlockSize = sampleRate / 20; }
    float *processFrame(float inL, float inR);

private:
    float rmsSum {};
    int rmsCount {};
    int rmsBlockSize {};
    float rmsLast {};
    float adaptiveRMS {};
    float maxRMS = {};
    static constexpr float smoothingFactor = 0.7f;
    static constexpr float decayFactor = 0.90;
    float threshold = 0.01;
    int baselineSamples = {};
    static constexpr int baselineCalibrationFrames = 4800;
    float baselineRMS = {};
    float longTermRMS = {};
    float normalizedRMS = {};
};

class PocketAudioBase {
public:
    PocketAudioBase() = default;
    virtual ~PocketAudioBase() = default;

    bool startAudio(int bufferSize, bool record);
    void stopAudio();
    Buffer* waitForBuffer();
    float waitRMS();
    int getSampleRate() const;

    static constexpr int NumBuffers = 2;

protected:
    Buffer buffers[NumBuffers] {};

    BlockingReaderWriterQueue<int> wqueue { BlockingReaderWriterQueue<int>(2) };
    BlockingReaderWriterQueue<float> event_queue { BlockingReaderWriterQueue<float>(2) };

    int sampleRate {};

    void signalBuffer(int bufferIndex);
    void signalRMS(float rms);

    int rdIndex {};
    static int constexpr kChannelCount = 2;
    static int constexpr kSampleRate = 48000;

    bool recording {};
    InputRMS *inputRms {};

    void callback(float *audioData, int32_t numFrames);

    virtual bool startStream(int bufferSize) = 0;
    virtual void stopStream() = 0;
};

#ifdef __ANDROID__

#include <oboe/Oboe.h>

class PocketAudio : public PocketAudioBase, public oboe::AudioStreamDataCallback {
public:
    ~PocketAudio() override = default;

    oboe::DataCallbackResult onAudioReady(
            oboe::AudioStream *oboeStream,
            void *audioData,
            int32_t numFrames) override;

protected:
    bool startStream(int bufferSize) override;
    void stopStream() override;

private:
    std::shared_ptr<oboe::AudioStream> audioStream {};
};
#endif

#ifdef __APPLE__

#include <cstdint>
#include <AudioToolbox/AudioToolbox.h>

class PocketAudio : public PocketAudioBase {
public:
    ~PocketAudio() override = default;

    void audioToolboxCb(AudioQueueRef inAQ, AudioQueueBufferRef inBuffer);

protected:
    bool startStream(int bufferSize) override;
    void stopStream() override;

private:
    AudioQueueRef audioQueue;
};
#endif

#endif //POCKETBAND_POCKETAUDIO_H
