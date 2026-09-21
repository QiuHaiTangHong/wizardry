package top.begonia.wizardry.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphicsExtractor.class)
public class GuiGraphicsExtractorMixin {
    @Shadow
    @Final
    private GuiGraphicsExtractor.ScissorStack scissorStack;

    @Inject(
            method = "item(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V",
            at = @At("TAIL")
    )
    private void recordVisibleGuiItems(
            LivingEntity owner,
            Level level,
            ItemStack itemStack,
            int x, int y,
            int seed,
            CallbackInfo ci
    ) {
        ScreenRectangle screenRectangle = this.scissorStack.peek();
    }
}
