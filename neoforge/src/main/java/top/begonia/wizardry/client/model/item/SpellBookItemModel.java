package top.begonia.wizardry.client.model.item;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.renderer.item.SpellBookItemRenderer;
import top.begonia.wizardry.client.util.ClientHelper;
import top.begonia.wizardry.core.registry.WizardryComponents;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record SpellBookItemModel(
        List<BakedQuad> quads,
        Supplier<Vector3fc[]> extents,
        ModelRenderProperties properties,
        Matrix4fc transform
) implements ItemModel {
    @Override
    public void update(@NonNull ItemStackRenderState state, @NonNull ItemStack stack, @NonNull ItemModelResolver resolver, @NonNull ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        AbstractSpell spell = stack.getOrDefault(WizardryComponents.SPELL.get(), WizardrySpells.NONE).value();
        state.appendModelIdentityElement(spell);
        if (ClientHelper.shouldDisplayDiscovered(spell, stack)) {
            state.appendModelIdentityElement(true);
        }
        ItemStackRenderState.LayerRenderState layerState = state.newLayer();
        this.properties.applyToLayer(layerState, displayContext);
        List<BakedQuad> baseQuads = new ArrayList<>();
        List<BakedQuad> overlayQuads = new ArrayList<>();
        this.quads.forEach(bakedQuad -> {
            BakedQuad.MaterialInfo oldMaterialInfo = bakedQuad.materialInfo();
            Identifier textureIdentifier = oldMaterialInfo.sprite().contents().name();
            if (textureIdentifier.getPath().contains("overlay")) {
                overlayQuads.add(bakedQuad);
            } else {
                baseQuads.add(bakedQuad);
            }
        });
        layerState.setExtents(this.extents);
        layerState.setLocalTransform(this.transform);
        layerState.setUsesBlockLight(false);
        int elementColor = spell.getElement().getStyle().getColor().getValue();
        layerState.setupSpecialModel(
                new SpellBookItemRenderer(),
                new SpellBookItemRenderer.State(
                        baseQuads,
                        overlayQuads,
                        displayContext,
                        ARGB.color(255, elementColor)
                )
        );
    }
}
