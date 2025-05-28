//
// Created by xrad on 10.12.24.
//

#include <cassert>
#include <algorithm>

#include "PocketAudio.h"

#include "log.h"

bool PocketAudio::startStream(int bufferSize) {
    assert(audioStream == nullptr);

    oboe::AudioStreamBuilder builder;
    // The builder set methods can be chained for convenience.
    oboe::Result result = builder.setSharingMode(oboe::SharingMode::Exclusive)
            ->setDirection(recording ? oboe::Direction::Input : oboe::Direction::Output)
            ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
            ->setInputPreset(oboe::InputPreset::Generic)
            ->setUsage(oboe::Usage::Media)
            ->setContentType(oboe::ContentType::Music)
                    //->setDeviceId(0)
            ->setChannelCount(kChannelCount)
            ->setSampleRate(kSampleRate)
            ->setSampleRateConversionQuality(oboe::SampleRateConversionQuality::None)
            ->setFormat(oboe::AudioFormat::Float)
            ->setDataCallback(this)
            ->openStream(audioStream);
    audioStream->requestStart();

    sampleRate = audioStream->getSampleRate();

    return result == oboe::Result::OK;
}

void PocketAudio::stopStream() {
    //__android_log_print(ANDROID_LOG_DEBUG, TAG, "%s %d", __func__, __LINE__);
    if (audioStream != nullptr) {
        audioStream->stop();
        audioStream->close();
        audioStream = nullptr;
    }
}

oboe::DataCallbackResult
PocketAudio::onAudioReady(oboe::AudioStream *oboeStream, void *audioData, int32_t numFrames) {
    callback(reinterpret_cast<float*>(audioData), numFrames);
    return oboe::DataCallbackResult::Continue;
}