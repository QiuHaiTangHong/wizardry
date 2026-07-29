cmake_minimum_required(VERSION 3.22)

project(wizardry-native-libs
        VERSION 1.0.0
        LANGUAGES C CXX
)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_C_STANDARD 11)
set(CMAKE_C_STANDARD_REQUIRED ON)

add_library(wizardry-native-libs SHARED
        src/main/cpp/MathUtils.cpp
)

target_compile_definitions(wizardry-native-libs
    PRIVATE WIZARDRY_NATIVE_LIBS_EXPORTS
)

target_include_directories(wizardry-native-libs
        PUBLIC  src/main/include
)

set_target_properties(wizardry-native-libs PROPERTIES
        OUTPUT_NAME wizardry-native-libs
        VERSION     ${PROJECT_VERSION}
        SOVERSION   1
)

install(TARGETS wizardry-native-libs
        RUNTIME DESTINATION lib
        LIBRARY DESTINATION lib
        ARCHIVE DESTINATION lib
)

install(FILES src/main/include/MathUtils.h
        DESTINATION include
)