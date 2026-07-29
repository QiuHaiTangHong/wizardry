#ifndef WIZARDRY_NATIVE_LIBS_MATHUTILS_H
#define WIZARDRY_NATIVE_LIBS_MATHUTILS_H

#include <stdint.h>

#ifdef _WIN32
  #ifdef WIZARDRY_NATIVE_LIBS_EXPORTS
    #define WIZARDRY_API __declspec(dllexport)
  #else
    #define WIZARDRY_API __declspec(dllimport)
  #endif
#else
  #define WIZARDRY_API __attribute__((visibility("default")))
#endif

typedef struct {
    uint64_t m[4];
} Matrix2x2;

#ifdef __cplusplus
extern "C" {
#endif

WIZARDRY_API Matrix2x2 multiply(const Matrix2x2 *A, const Matrix2x2 *B);

WIZARDRY_API Matrix2x2 matrix_pow(const Matrix2x2 *base, uint64_t p);

WIZARDRY_API uint64_t  fibonacci(int n);

#ifdef __cplusplus
}
#endif

#endif