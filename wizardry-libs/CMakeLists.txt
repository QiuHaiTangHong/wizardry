cmake_minimum_required(VERSION 3.22)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

project(wizardry-native-libs CXX)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(JNI REQUIRED)

add_library(wizardry-native-libs SHARED src/main/cpp/main.cpp)

target_include_directories(wizardry-native-libs PRIVATE ${JNI_INCLUDE_DIRS})

target_compile_definitions(wizardry-native-libs PRIVATE JNI_IMPLEMENTATION)
install(TARGETS wizardry-native-libs
    RUNTIME DESTINATION .
    LIBRARY DESTINATION .
    ARCHIVE DESTINATION .
)