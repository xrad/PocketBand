cmake_minimum_required(VERSION 3.22.1)

project("pocketband")

add_library(${PROJECT_NAME} SHARED
    PocketAudio.cpp
    native-lib.cpp
)

if (ANDROID)
    target_sources(${PROJECT_NAME} PRIVATE PocketAudioAndroid.cpp)

    find_package (oboe REQUIRED CONFIG)

    set(LIBS android log mediandk oboe::oboe)
endif ()

if (APPLE)
    target_sources(${PROJECT_NAME} PRIVATE PocketAudioApple.cpp)

    find_library(AUDIO_TOOLBOX AudioToolbox)
    if (NOT AUDIO_TOOLBOX)
        message(FATAL_ERROR "AudioToolbox framework not found")
    endif()

    set(LIBS ${AUDIO_TOOLBOX})
endif ()

target_include_directories(${PROJECT_NAME} PRIVATE .)

target_compile_options(${PROJECT_NAME} PRIVATE -Wall -O3 -ffast-math)

target_compile_features(${PROJECT_NAME} PUBLIC cxx_std_17)

target_link_libraries(${PROJECT_NAME} ${LIBS})
