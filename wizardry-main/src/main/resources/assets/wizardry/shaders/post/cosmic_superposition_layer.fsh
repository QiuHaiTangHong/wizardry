#version 330

#moj_import <minecraft:globals.glsl>

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform MouseInfo {
    vec4 Mouse;
};

out vec4 fragColor;

// 参数宏定义
#define iterations 17
#define formuparam 0.53

#define volsteps 20
#define stepsize 0.1

#define zoom       0.800
#define tile       0.850
#define speed      0.010

#define brightness 0.0015
#define darkmatter 0.300
#define distfading 0.730
#define saturation 0.850

void main() {
    // 1. 坐标归一化与宽高比矫正
    vec2 uv = texCoord - 0.5;
    uv.y *= OutSize.y / OutSize.x;
    vec4 inputColor = texture(InSampler, texCoord);
    vec2 mouseUv = Mouse.xy / Mouse.zw;

    // 2. 射线方向构建
    vec3 dir = vec3(uv * zoom, 1.0);

    // 3. 时间与摄像机初始位置 (若没有传入 Time 变量，可用固定常数测试)
    float time = GameTime * 2400 * speed + 0.25;

    // 旋转矩阵设置
    float a1 = 0.5 + mouseUv.x * 2.0;
    float a2 = 0.8 + mouseUv.y * 2.0;
    mat2 rot1 = mat2(cos(a1), sin(a1), -sin(a1), cos(a1));
    mat2 rot2 = mat2(cos(a2), sin(a2), -sin(a2), cos(a2));

    dir.xz *= rot1;
    dir.xy *= rot2;

    vec3 from = vec3(1.0, 0.5, 0.5);
//    from += vec3(time * 2.0, time, -2.0);
    from.xz *= rot1;
    from.xy *= rot2;

    // 4. 体积渲染 (Volumetric Raymarching)
    float s = 0.1;
    float fade = 1.0;
    vec3 v = vec3(0.0);

    for (int r = 0; r < volsteps; r++) {
        vec3 p = from + s * dir * 0.5;
        p = abs(vec3(tile) - mod(p, vec3(tile * 2.0))); // 空间重复网格

        float pa = 0.0;
        float a = 0.0;

        // 5. Kaliset 分形核心迭代
        for (int i = 0; i < iterations; i++) {
            p = abs(p) / dot(p, p) - formuparam;
            a += abs(length(p) - pa);
            pa = length(p);
        }

        // 6. 暗物质与距离衰减处理
        float dm = max(0.0, darkmatter - a * a * 0.001);
        a *= a * a; // 提高亮暗对比

        if (r > 6) fade *= 1.0 - dm;

        v += fade;
        v += vec3(s, s * s, s * s * s * s) * a * brightness * fade; // 按距离分配光谱
        fade *= distfading;
        s += stepsize;
    }

    // 7. 色彩饱和度调整与输出
    v = mix(vec3(length(v)), v, saturation);
    fragColor = vec4(v * 0.01, 1.0);
}
