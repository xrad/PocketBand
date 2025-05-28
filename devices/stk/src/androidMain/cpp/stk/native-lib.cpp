#include <jni.h>

#include <cassert>
#include <memory>
#include <map>

#include "include/FreeVerb.h"
#include "include/Chorus.h"
#include "include/PRCRev.h"

#include "include/FMVoices.h"
#include "include/BeeThree.h"
#include "include/SKINImsg.h"
#include "LoPass.h"

// generated from interface.txt
#include "stkgen.h"

class mapEntry {
public:
    mapEntry(int id, stk::Stk *stk) : id(id), u({.stk = stk}) {}
    int id;
    union {
        stk::Stk* stk;
        stk::Effect* effect;
        stk::Instrmnt* instrmnt;
        stk::Filter* filter;
    } u;
};

static std::map<int, mapEntry*> processors;

static int nextHandle = 1;
static u_long numCopies = 0;

extern "C"
JNIEXPORT void JNICALL
Java_de_nullgrad_pocketband_stk_service_StkImpl_setSampleRate(
        JNIEnv *env,
        jobject thiz,
        jfloat sampleRate
) {
    stk::Stk::setSampleRate(sampleRate);
}

extern "C"
JNIEXPORT void JNICALL
Java_de_nullgrad_pocketband_stk_platform_StkAssetsKt_setRawWavePath(
        JNIEnv *env,
        jobject thiz,
        jstring path
) {
    const char* strRawWavePath = env->GetStringUTFChars(path, nullptr);
    stk::Stk::setRawwavePath(strRawWavePath);
}

extern "C"
JNIEXPORT jint JNICALL
Java_de_nullgrad_pocketband_stk_service_StkImpl_create(
    JNIEnv *env,
    jobject thiz,
    jint id
) {
    stk::Stk *stk{};

    if (id == FX_FREEVERB) stk = new stk::FreeVerb();
    if (id == FX_CHORUS) stk = new stk::Chorus();
    if (id == FX_PRC_REVERB) stk = new stk::PRCRev();
    if (id == FX_LOWPASS) stk = new stk::OnePole();
    if (id == INST_FM_VOICE) stk = new stk::FMVoices();
    if (id == FLT_LOPASS) stk = new LoPass();

    assert(stk != nullptr);

    auto handle = nextHandle++;
    processors[handle] = new mapEntry(id, stk);

    return handle;
}

extern "C"
JNIEXPORT void JNICALL
Java_de_nullgrad_pocketband_stk_service_StkImpl_destroy(
        JNIEnv *env,
        jobject thiz,
        jint handle)
{
    auto it = processors.find(handle);
    assert(it != processors.end());
    auto e = it->second;
    processors.erase(it);
    if (e->id & STK_INSTRUMENTS) delete e->u.instrmnt;
    if (e->id & STK_EFFECTS) delete e->u.effect;
    if (e->id & STK_FILTERS) delete e->u.filter;
    delete e;
}

extern "C"
JNIEXPORT void JNICALL
Java_de_nullgrad_pocketband_stk_service_StkImpl_tickProcess(
        JNIEnv *env,
        jobject thiz,
        jint handle,
        jfloatArray audioData,
        jint offset,
        jint numFrames
)
{
    auto it = processors.find(handle);
    assert(it != processors.end());
    auto e = it->second;
    assert(e->id & (STK_EFFECTS | STK_FILTERS));

    auto numSamples = numFrames * 2;

    jboolean isCopy;
    auto temp = reinterpret_cast<float*>(env->GetPrimitiveArrayCritical(audioData, &isCopy));
    if (isCopy) ++numCopies;

    if (e->id == FX_FREEVERB) {
        auto p = reinterpret_cast<stk::FreeVerb*>(e->u.effect);
        for (auto i = 0; i < numSamples; i+=2) {
            temp[offset+i] = p->tick(temp[offset+i], temp[offset+i+1]);
            temp[offset+i+1] = p->lastOut(1);
        }
    }
    if (e->id == FX_PRC_REVERB) {
        auto p = reinterpret_cast<stk::PRCRev*>(e->u.effect);
        for (auto i = 0; i < numSamples; i+=2) {
            stk::StkFloat s = (temp[offset+i] + temp[offset+i+1]) * 0.5f;
            temp[offset+i] = p->tick(s);
            temp[offset+i+1] = p->lastOut(1);
        }
    }
    if (e->id == FX_CHORUS) {
        auto p = reinterpret_cast<stk::Chorus*>(e->u.effect);
        for (auto i = 0; i < numSamples; i+=2) {
            stk::StkFloat s = (temp[offset+i] + temp[offset+i+1]) * 0.5f;
            temp[offset+i] = p->tick(s);
            temp[offset+i+1] = p->lastOut(1);
        }
    }
    if (e->id == FLT_LOPASS) {
        auto p = reinterpret_cast<LoPass*>(e->u.filter);
        for (auto i = 0; i < numSamples; i+=2) {
            temp[offset+i] = p->f[0].tick(temp[offset+i]);
            temp[offset+i+1] = p->f[1].tick(temp[offset+i+1]);
        }
    }
    
    env->ReleasePrimitiveArrayCritical(audioData, temp, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_de_nullgrad_pocketband_stk_service_StkImpl_setParameter(
        JNIEnv *env,
        jobject thiz,
        jint handle,
        jint paramId,
        jdouble value
) {
    auto it = processors.find(handle);
    assert(it != processors.end());
    auto e = it->second;

    if (e->id & STK_EFFECTS) {
        if (paramId == FX_PARAM_MIX) {
            e->u.effect->setEffectMix((stk::StkFloat) value);
            return;
        }
    }

    if (e->id == FLT_LOPASS) {
        auto p = reinterpret_cast<LoPass*>(e->u.filter);
        if (paramId == FX_PARAM_CUTOFF) {
            auto pole = (stk::StkFloat)exp(-2.0 * M_PI * value / stk::Stk::sampleRate());
            p->f[0].setPole(pole);
            p->f[1].setPole(pole);
        }
    }

    if (e->id == FX_FREEVERB) {
        auto p = reinterpret_cast<stk::FreeVerb*>(e->u.effect);
        if (paramId == FX_PARAM_SIZE) {
            p->setRoomSize((stk::StkFloat)value);
        }
        if (paramId == FX_PARAM_WIDTH) {
            p->setWidth((stk::StkFloat)value);
        }
    }

    if (e->id == FX_PRC_REVERB) {
        auto p = reinterpret_cast<stk::PRCRev*>(e->u.effect);
        if (paramId == FX_PARAM_T60) {
            p->setT60((stk::StkFloat)value);
        }
    }

    if (e->id == FX_CHORUS) {
        auto p = reinterpret_cast<stk::Chorus*>(e->u.effect);
        if (paramId == FX_PARAM_DEPTH) {
            p->setModDepth((stk::StkFloat)value);
        }
        if (paramId == FX_PARAM_FREQUENCY) {
            p->setModFrequency((stk::StkFloat)value);
        }
    }

    if (e->id == INST_FM_VOICE) {
        auto p = reinterpret_cast<stk::FMVoices*>(e->u.instrmnt);
        if (paramId == INST_PARAM_CONT1) {
            p->controlChange(1, (stk::StkFloat)value);
        }
        if (paramId == INST_PARAM_CONT2) {
            p->controlChange(2, (stk::StkFloat)value);
        }
        if (paramId == INST_PARAM_CONT4) {
            p->controlChange(4, (stk::StkFloat)value);
        }
        if (paramId == INST_PARAM_CONT11) {
            p->controlChange(11, (stk::StkFloat)value);
        }
    }

}

extern "C"
JNIEXPORT void JNICALL
Java_de_nullgrad_pocketband_stk_service_StkImpl_tickInstrument(
        JNIEnv *env,
        jobject thiz,
        jint handle,
        jfloatArray audioData,
        jint offset,
        jint numFrames
)
{
    auto it = processors.find(handle);
    assert(it != processors.end());
    auto e = it->second;

    auto numSamples = numFrames * 2;

    jboolean isCopy;
    auto temp = reinterpret_cast<float*>(env->GetPrimitiveArrayCritical(audioData, &isCopy));
    if (isCopy) ++numCopies;

    auto p = reinterpret_cast<stk::Instrmnt*>(e->u.instrmnt);
    if (p->channelsOut() == 2) {
        for (auto i = 0; i < numSamples; i+=2) {
            temp[offset+i] += p->tick();
            temp[offset+i+1] += p->lastOut(1);
        }
    }
    else {
        for (auto i = 0; i < numSamples; i+=2) {
            auto s = p->tick();
            temp[offset+i] += s;
            temp[offset+i+1] += s;
        }
    }

    env->ReleasePrimitiveArrayCritical(audioData, temp, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_de_nullgrad_pocketband_stk_service_StkImpl_noteOn(
        JNIEnv *env,
        jobject thiz,
        jint handle,
        jfloat frequency,
        jfloat amplitude)
{
    auto it = processors.find(handle);
    assert(it != processors.end());
    auto i = it->second;
    assert(i->id & STK_INSTRUMENTS);

    i->u.instrmnt->noteOn(frequency, amplitude);
}

extern "C"
JNIEXPORT void JNICALL
Java_de_nullgrad_pocketband_stk_service_StkImpl_noteOff(
        JNIEnv *env,
        jobject thiz,
        jint handle,
        jfloat amplitude)
{
    auto it = processors.find(handle);
    assert(it != processors.end());
    auto i = it->second;
    assert(i->id & STK_INSTRUMENTS);

    i->u.instrmnt->noteOff(amplitude);
}
