#include <iostream>
#include <jni.h>

void nativeTest() {
    std::cout << "[C++] Native library build script test successful!" << std::endl;
}

extern "C" {
JNIEXPORT void JNICALL Java_top_begonia_wizardry_core_util_NativeMethodRouter_sayHello(JNIEnv* env, jobject obj) {
    std::cout << "[JNI] Hello from C++! Triggered by Minecraft Thread." << std::endl;
    nativeTest();
}
}