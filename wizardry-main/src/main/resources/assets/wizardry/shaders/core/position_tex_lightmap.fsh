#version 330

#moj_import <minecraft:dynamictransforms.glsl>

uniform sampler2D Sampler0;

in vec4 vertexColor;

#ifndef EMISSIVE
in vec4 lightMapColor;
#endif

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    #ifndef EMISSIVE
    color = color * lightMapColor * vertexColor;
    #endif
    fragColor = color * ColorModulator;
}