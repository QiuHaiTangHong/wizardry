package top.begonia.wizardry.api.renderer;

import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.vertex.VertexFormat;

public final class WizardryVertexFormat {
    private static final GpuFormat POSITION_FORMAT = GpuFormat.RGB32_FLOAT;
    private static final GpuFormat UV0_FORMAT = GpuFormat.RG32_FLOAT;
    private static final GpuFormat UV2_FORMAT = GpuFormat.RG16_SINT;
    private static final GpuFormat NORMAL_FORMAT = GpuFormat.RGBA8_SNORM;
    private static final GpuFormat COLOR_FORMAT = GpuFormat.RGBA8_UNORM;
    public static final VertexFormat POSITION_TEX_LIGHTMAP = VertexFormat.builder(0)
            .addAttribute("Position", POSITION_FORMAT)
            .addAttribute("UV0", UV0_FORMAT)
            .addAttribute("UV2", UV2_FORMAT)
            .addAttribute("Normal", NORMAL_FORMAT)
            .build();
    public static final VertexFormat NO_TEX_PARTICLE = VertexFormat.builder(0)
            .addAttribute("Position", POSITION_FORMAT)
            .addAttribute("Color", COLOR_FORMAT)
            .addAttribute("UV2", UV2_FORMAT)
            .build();
}
