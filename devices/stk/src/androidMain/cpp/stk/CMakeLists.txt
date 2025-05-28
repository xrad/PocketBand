cmake_minimum_required(VERSION 3.22.1)

project("stk")

file(GLOB lib_srcs src/*.cpp)

list(FILTER lib_srcs EXCLUDE REGEX .*src/Inet.*)
list(FILTER lib_srcs EXCLUDE REGEX .*src/Midi.*)
list(FILTER lib_srcs EXCLUDE REGEX .*src/Thread.*)
list(FILTER lib_srcs EXCLUDE REGEX .*src/Mutex.*)
list(FILTER lib_srcs EXCLUDE REGEX .*src/Rt*)
list(FILTER lib_srcs EXCLUDE REGEX .*src/Socket.*)
list(FILTER lib_srcs EXCLUDE REGEX .*src/Tcp.*)
list(FILTER lib_srcs EXCLUDE REGEX .*src/Udp.*)

add_library(${PROJECT_NAME} SHARED
        # List C/C++ source files with relative paths to this CMakeLists.txt.
        native-lib.cpp
        ${lib_srcs}
)

target_include_directories(${PROJECT_NAME} PRIVATE . include ../../../../build/generated/stkgen/cpp)

target_compile_options(${PROJECT_NAME} PRIVATE -Wall -O3 -ffast-math)
target_compile_features(${PROJECT_NAME} PUBLIC cxx_std_17)


# Specifies libraries CMake should link to your target library. You
# can link libraries from various origins, such as libraries defined in this
# build script, prebuilt third-party libraries, or Android system libraries.
target_link_libraries(${PROJECT_NAME}
        # List libraries link to the target library
)

