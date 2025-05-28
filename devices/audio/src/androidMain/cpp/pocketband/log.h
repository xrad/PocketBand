//
// Created by xrad on 21.02.23.
//

#ifndef POCKETBANDX_LOG_H
#define POCKETBANDX_LOG_H

#ifdef __ANDROID__

#include <android/log.h>

#define TAG "pocketband"

#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__))

#endif

#ifdef __APPLE__

#define LOGD(...)

#endif

#endif //POCKETBANDX_LOG_H
