#version 330

#if !defined(NO_CARDINAL_LIGHTING)
#moj_import <minecraft:light.glsl>
#endif
#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>
#moj_import <minecraft:sample_lightmap.glsl>

in vec3 Position;
in vec2 UV0;
in ivec2 UV2;
in vec3 Normal;

#ifndef EMISSIVE
uniform sampler2D Sampler2;
#endif

out vec4 vertexColor;

#ifndef EMISSIVE
out vec4 lightMapColor;
#endif

out vec2 texCoord0;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    vec4 defaultColor = vec4(1.0F, 1.0F, 1.0F, 1.0F);

    #if defined(NO_CARDINAL_LIGHTING)
    vertexColor = defaultColor;
    #else
    vertexColor = minecraft_mix_light(Light0_Direction, Light1_Direction, Normal, defaultColor);
    #endif

    #ifndef EMISSIVE
    lightMapColor = sample_lightmap(Sampler2, UV2);
    #endif

    texCoord0 = UV0;

    #ifdef APPLY_TEXTURE_MATRIX
    texCoord0 = (TextureMat * vec4(UV0, 0.0, 1.0)).xy;
    #endif
}
