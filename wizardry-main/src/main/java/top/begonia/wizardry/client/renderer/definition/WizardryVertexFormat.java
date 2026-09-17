package top.begonia.wizardry.client.renderer.definition;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

public final class WizardryVertexFormat {
    private static final GpuFormat POSITION_FORMAT = GpuFormat.RGB32_FLOAT;
    private static final GpuFormat UV0_FORMAT = GpuFormat.RG32_FLOAT;
    private static final GpuFormat UV2_FORMAT = GpuFormat.RG16_SINT;
    private static final GpuFormat NORMAL_FORMAT = GpuFormat.RGBA8_SNORM;
    public static final VertexFormat POSITION_TEX_LIGHTMAP = VertexFormat.builder(0)
            .addAttribute("Position", POSITION_FORMAT)
            .addAttribute("UV0", UV0_FORMAT)
            .addAttribute("UV2", UV2_FORMAT)
            .addAttribute("Normal", NORMAL_FORMAT)
            .build();
}
