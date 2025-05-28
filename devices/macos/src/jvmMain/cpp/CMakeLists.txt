cmake_minimum_required(VERSION 3.22.1)

project("macos")

add_library(${PROJECT_NAME} SHARED
    native-lib.cpp
)

target_sources(${PROJECT_NAME} PRIVATE permissions.m)

find_library(AVFOUNDATION AVFoundation)
if (NOT AVFOUNDATION)
    message(FATAL_ERROR "AVFoundation framework not found")
endif()
find_library(FOUNDATION Foundation)
if (NOT FOUNDATION)
    message(FATAL_ERROR "Foundation framework not found")
endif()

set(LIBS ${AVFOUNDATION} ${FOUNDATION})

target_include_directories(${PROJECT_NAME} PRIVATE .)

target_compile_options(${PROJECT_NAME} PRIVATE -Wall -Ofast)

target_compile_features(${PROJECT_NAME} PUBLIC cxx_std_17)

target_link_libraries(${PROJECT_NAME} ${LIBS})
