package top.begonia.wizardry.core.spell.impl.summon;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.data.spell.definition.spell.part.SpellContext;
import top.begonia.wizardry.core.entity.living.minion.WitherSkeletonMinionEntity;

import java.util.function.Function;

public class WitherSkeletonSummonSpell extends MinionSpell<WitherSkeletonMinionEntity> {
    public WitherSkeletonSummonSpell(Identifier identifier, Function<Level, WitherSkeletonMinionEntity> minionFactory) {
        super(identifier, minionFactory);
        this.soundValues(7, 0.6f, 0);
    }

    @Override
    protected void addMinionExtras(@NonNull WitherSkeletonMinionEntity minion, BlockPos pos, LivingEntity owner, SpellContext context, int alreadySpawned) {
        minion.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
        minion.setDropChance(EquipmentSlot.MAINHAND, 0.0f);
    }
}
