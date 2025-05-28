#include <jni.h>

#include <cassert>
#include <memory>

#include "PocketAudio.h"

static std::unique_ptr<PocketAudio> audio = nullptr;

extern "C"
JNIEXPORT void JNICALL
Java_de_nullgrad_pocketband_audio_PocketPlayer_create(
    JNIEnv *env,
    jobject thiz)
{
    assert(audio.get() == nullptr);
    audio = std::make_unique<PocketAudio>();
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_de_nullgrad_pocketband_audio_PocketPlayer_startAudio(
        JNIEnv *env,
        jobject thiz,
        jint bufferSize,
        jboolean record)
{
    assert(audio.get() != nullptr);
    return audio->startAudio(bufferSize, record);
}

extern "C"
JNIEXPORT void JNICALL
Java_de_nullgrad_pocketband_audio_PocketPlayer_stopAudio(
        JNIEnv *env,
        jobject thiz)
{
    if (audio) {
        audio->stopAudio();
    }
}

extern "C"
JNIEXPORT jint JNICALL
Java_de_nullgrad_pocketband_audio_PocketPlayer_getSampleRate(
        JNIEnv *env,
        jobject thiz)
{
    assert(audio.get() != nullptr);
    return audio->getSampleRate();
}

extern "C"
JNIEXPORT void JNICALL
Java_de_nullgrad_pocketband_audio_PocketPlayer_writeBuffer(
        JNIEnv *env,
        jobject thiz,
        jfloatArray data
        )
{
    assert(audio.get() != nullptr);
    jsize len = env->GetArrayLength(data);
    auto dest = audio->waitForBuffer();
    assert(dest->data != nullptr && dest->size == len);
    env->GetFloatArrayRegion(data, 0, len, dest->data);
    // writing length informs render callback that there is new data to play
    dest->length = len / 2;
}

extern "C"
JNIEXPORT void JNICALL
Java_de_nullgrad_pocketband_audio_PocketPlayer_readBuffer(
        JNIEnv *env,
        jobject thiz,
        jfloatArray data
)
{
    assert(audio.get() != nullptr);
    jsize len = env->GetArrayLength(data);
    auto dest = audio->waitForBuffer();
    assert(dest->data != nullptr && dest->size == len);
    assert(dest->length != 0 && dest->length == dest->readIndex);
    if (dest->length != 0 && dest->length == dest->readIndex) {
        env->SetFloatArrayRegion(data, 0, len, dest->data);
        // resetting read index informs render callback that there is new data to record
        dest->readIndex = 0;
        return;
    }
}

extern "C"
JNIEXPORT jfloat JNICALL
Java_de_nullgrad_pocketband_audio_PocketPlayer_waitRMS(
        JNIEnv *env,
        jobject thiz
)
{
    assert(audio.get() != nullptr);
    return audio->waitRMS();
}
