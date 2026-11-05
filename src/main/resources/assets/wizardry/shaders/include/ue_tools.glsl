#ifndef UE_TOOLS_GLSL
#define UE_TOOLS_GLSL
#define TWO_PI 6.283185

/**
 * 将二维 UV 坐标转换为径向坐标
 *
 * @param texCoords                    输入的二维 UV 坐标，通常范围为 [0, 1]
 * @param inputSwizzleCoordinateOutput 是否交换径向坐标输出中的 R、G 通道
 *
 * @return vec4                        返回 RGBA 四个通道组成的径向坐标
 *                                     true：  (angle, linearDistance, angle, linearDistance)
 *                                     false： (linearDistance, angle, angle, linearDistance)
 */
vec4 VectorToRadialValue(vec2 texCoords, bool inputSwizzleCoordinateOutput){
    vec2 remapCoords = texCoords.xy * 2.0F - 1.0F;

    // 计算角度
    float radian = atan(remapCoords.y, remapCoords.x) / TWO_PI;
    float angle = fract(radian);

    // 计算线性距离
    float linearDistance = sqrt(remapCoords.x * remapCoords.x + remapCoords.y * remapCoords.y);

    // 计算 Radial Coordinates
    if (inputSwizzleCoordinateOutput){
        return vec4(angle, linearDistance, angle, linearDistance);
    } else {
        return vec4(linearDistance, angle, angle, linearDistance);
    }
}
#endif