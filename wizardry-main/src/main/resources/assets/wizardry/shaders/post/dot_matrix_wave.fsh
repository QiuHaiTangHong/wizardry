#version 330

#moj_import<minecraft:globals.glsl>
uniform sampler2D InSampler;
in vec2 texCoord;
layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};
out vec4 fragColor;

void main() {
    vec2 uv = (texCoord * 2.0 - 1.0) * vec2(InSize.x / InSize.y, 1.0);
    float iTime = GameTime * 2400.0F;
    float camTime = iTime * 0.3;

    //构造虚拟相机
    // 摄像机位置
    vec3 ro = vec3(5.0 * cos(camTime), 3.0, 5.0 * sin(camTime));
    // 目标
    vec3 ta = vec3(0.0, -0.5, 0.0);
    // 前轴
    vec3 cw = normalize(ta - ro);
    // 上轴
    vec3 cp = vec3(0.0, 1.0, 0.0);
    // 右轴
    vec3 cu = normalize(cross(cw, cp));
    // 上轴
    vec3 cv = normalize(cross(cu, cw));
    // 光路
    vec3 rd = normalize(uv.x * cu + uv.y * cv + 1.8 * cw);

    //定义边界和间隔
    const float GRID_COUNT = 36.0;
    const float SPACING = 5.0;
    vec3 finalPoints = vec3(0.0);
    float t = -ro.y / rd.y;

    if (t > 0.0) {
        vec2 groundPos = ro.xz + rd.xz * t;
        vec2 gridIdx = (groundPos / SPACING + 0.5) * GRID_COUNT;
        vec2 centerID = floor(gridIdx);
        for (float i = -10.0; i <= 10.0; i++) {
            for (float j = -10.0; j <= 10.0; j++) {
                vec2 currentID = centerID + vec2(i, j);

                // 边界过滤
                if (currentID.x < 0.0 || currentID.x >= GRID_COUNT ||
                    currentID.y < 0.0 || currentID.y >= GRID_COUNT) {
                    continue;
                }

                // 计算该格点的 3D 坐标
                float px = (currentID.x / GRID_COUNT - 0.5) * SPACING;
                float pz = (currentID.y / GRID_COUNT - 0.5) * SPACING;

                // 物理公式：波浪高度 y
                float dCenter = length(vec2(px, pz));
                float py = 0.5 * sin(dCenter * 2.5 - iTime * 3.0) + 0.2 * cos(px * 1.5 + iTime * 2.0);

                vec3 p3d = vec3(px, py, pz);
                vec3 relPos = p3d - ro;
                float distToCam = dot(relPos, cw);
                if (distToCam > 0.1) {
                    vec2 p2d = vec2(dot(relPos, cu), dot(relPos, cv)) / distToCam;
                    p2d *= 1.8;

                    float distToPixel = length(uv - p2d);
                    float size = 0.08 / distToCam;

                    if (distToPixel < size) {
                        float brightness = pow(clamp(1.0 - distToPixel / size, 0.0, 1.0), 2.0);
                        vec3 baseCol = mix(vec3(0.05, 0.1, 0.8), vec3(0.2, 1.0, 0.9), py + 0.5);
                        float distFade = exp(-0.15 * distToCam);
                        finalPoints += baseCol * brightness * distFade * 2.0;
                    }
                }
            }
        }
    }
    vec3 sceneColor = texture(InSampler, texCoord).rgb;
    vec3 result = sceneColor + finalPoints;
    fragColor = vec4(clamp(result, 0.0, 1.0), 1.0);
}