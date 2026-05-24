package top.begonia.wizardry.client.particle;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class MutableDoubleSpriteSet {
    private volatile List<List<TextureAtlasSprite>> doubleSprites = new ArrayList<>();

    public MutableDoubleSpriteSet() {
    }

    public void rebind(List<List<TextureAtlasSprite>> newSprites) {
        this.doubleSprites = List.copyOf(newSprites);
    }

    public TextureAtlasSprite getSprite(int rowIndex, int age, int lifetime) {
        List<TextureAtlasSprite> row = this.getRow(rowIndex);
        int frameIndex = Mth.floor((float) age / (float) lifetime * (float) (row.size() - 1));
        return row.get(Mth.clamp(frameIndex, 0, row.size() - 1));
    }

    public TextureAtlasSprite getSprite(int rowIndex, int frameIndex) {
        List<TextureAtlasSprite> row = this.getRow(rowIndex);
        return row.get(Mth.clamp(frameIndex, 0, row.size() - 1));
    }

    private List<TextureAtlasSprite> getRow(int rowIndex) {
        List<List<TextureAtlasSprite>> currentSprites = this.doubleSprites;
        if (currentSprites.isEmpty()) {
            throw new IllegalStateException("❌ 尝试获取未绑定的粒子精灵集合！请检查重载流程。");
        }

        int safeRowIndex = Mth.clamp(rowIndex, 0, currentSprites.size() - 1);
        List<TextureAtlasSprite> row = currentSprites.get(safeRowIndex);

        if (row.isEmpty()) {
            throw new IllegalStateException("❌ 粒子精灵集合中存在空行配置！Row: " + safeRowIndex);
        }
        return row;
    }

    public int getRowsCount() {
        return this.doubleSprites.size();
    }

    public int getFrameCount(int rowIndex) {
        List<List<TextureAtlasSprite>> currentSprites = this.doubleSprites;
        if (currentSprites.isEmpty()) return 0;
        return currentSprites.get(Mth.clamp(rowIndex, 0, currentSprites.size() - 1)).size();
    }

    public boolean isEmpty() {
        return this.doubleSprites.isEmpty();
    }
}
