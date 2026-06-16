cmake_minimum_required(VERSION 3.22)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

project(wizardry_native CXX)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(JNI REQUIRED)

add_library(native-libs SHARED src/main/cpp/main.cpp)

target_include_directories(native-libs PRIVATE ${JNI_INCLUDE_DIRS})

target_compile_definitions(native-libs PRIVATE JNI_IMPLEMENTATION)