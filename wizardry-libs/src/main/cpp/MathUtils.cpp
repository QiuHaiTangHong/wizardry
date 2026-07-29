#include "MathUtils.h"

Matrix2x2 multiply(const Matrix2x2 *A, const Matrix2x2 *B) {
    Matrix2x2 R;
    R.m[0] = A->m[0] * B->m[0] + A->m[1] * B->m[2];
    R.m[1] = A->m[0] * B->m[1] + A->m[1] * B->m[3];
    R.m[2] = A->m[2] * B->m[0] + A->m[3] * B->m[2];
    R.m[3] = A->m[2] * B->m[1] + A->m[3] * B->m[3];
    return R;
}

Matrix2x2 matrix_pow(const Matrix2x2 *base, uint64_t p) {
    Matrix2x2 result = {{1, 0, 0, 1}};
    Matrix2x2 b = *base;

    while (p > 0) {
        if (p & 1) {
            result = multiply(&result, &b);
        }
        b = multiply(&b, &b);
        p >>= 1;
    }
    return result;
}

uint64_t fibonacci(int n) {
    if (n <= 0) return 0;
    if (n == 1) return 1;

    Matrix2x2 F = {{1, 1, 1, 0}};
    Matrix2x2 R = matrix_pow(&F, (uint64_t)(n - 1));
    return R.m[0];
}