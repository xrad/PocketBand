#include <jni.h>

extern "C"
JNIEXPORT void JNICALL
Java_de_nullgrad_pocketband_macos_MacOS_requestMicrophonePermission(
        JNIEnv *env,
        jobject thiz)
{
    extern void requestMicrophonePermission();
    requestMicrophonePermission();
}

extern "C"
JNIEXPORT jint JNICALL
Java_de_nullgrad_pocketband_macos_MacOS_getMicrophonePermissionStatus(
        JNIEnv *env,
        jobject thiz)
{
    extern int getMicrophonePermissionStatus();
    return (jint)getMicrophonePermissionStatus();
}
