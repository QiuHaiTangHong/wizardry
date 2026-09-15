package top.begonia.wizardry.api.particle.extension.extract;

import net.minecraft.util.Mth;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

public interface IColorFlowOperation extends IBaseFlowOperation {
    Vector3f getColor();

    int getHEXColor();

    Vector3f getStartColor();

    Vector3f getEndColor();

    float getAlpha();

    int getLightCoords();

    IColorFlowOperation setColor(float red, float green, float blue);

    @SuppressWarnings("UnusedReturnValue")
    default IColorFlowOperation setVecColor(@NonNull Vector3f lerpColor){
        return this.setColor(lerpColor.x, lerpColor.y, lerpColor.z);
    }

    default IColorFlowOperation setHEXColor(int hexColor) {
        int red = (hexColor & 0x00_FF_00_00) >> 16;
        int green = (hexColor & 0x00_00_FF_00) >> 8;
        int blue = hexColor & 0x00_00_00_FF;
        return this.setRGBColor(red, green, blue);
    }

    default IColorFlowOperation setRGBColor(int red, int green, int blue) {
        return this.setColor(red / 255.0F, green / 255.0F, blue / 255.0F);
    }

    default IColorFlowOperation setHSVColor(float h, float s, float v) {
        h = h - Mth.floor(h);
        s = Mth.clamp(s, 0.0F, 1.0F);
        v = Mth.clamp(v, 0.0F, 1.0F);

        float h6 = h * 6.0F;
        int sector = Mth.floor(h6);
        float f = h6 - sector;

        float p = v * (1.0F - s);
        float q = v * (1.0F - s * f);
        float t = v * (1.0F - s * (1.0F - f));

        return switch (sector % 6) {
            case 0 -> setColor(v, t, p);
            case 1 -> setColor(q, v, p);
            case 2 -> setColor(p, v, t);
            case 3 -> setColor(p, q, v);
            case 4 -> setColor(t, p, v);
            case 5 -> setColor(v, p, q);
            default -> throw new AssertionError();
        };
    }

    IColorFlowOperation setAlpha(float alpha);

    IColorFlowOperation setLightCoords(int lightCoords);
}
