#version 330

uniform sampler2D InSampler;
uniform sampler2D PrevSampler;

in vec2 texCoord;

layout(std140) uniform SamplerInfo {
    vec2 OutSize;
    vec2 InSize;
};

layout(std140) uniform PhosphorConfig{
    vec3 Phosphor;
};

out vec4 fragColor;

void main() {
    vec4 CurrTexel = texture(InSampler, texCoord);
    vec4 PrevTexel = texture(PrevSampler, texCoord);

    fragColor = vec4(max(PrevTexel.rgb * Phosphor, CurrTexel.rgb), 1.0);
}
