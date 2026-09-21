package top.begonia.wizardry.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import top.begonia.wizardry.client.audio.MovingSoundEntity;
import top.begonia.wizardry.core.config.CommonConfig;
import top.begonia.wizardry.core.registry.WizardryAttachment;
import top.begonia.wizardry.core.spell.AbstractSpell;

import javax.annotation.Nullable;

public final class ClientHelper {
    public static boolean shouldDisplayDiscovered(AbstractSpell spell, @Nullable ItemStack stack) {
        if (!CommonConfig.discoveryMode) {
            return true;
        }
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return false;
        }
        if (player.isCreative()) {
            return true;
        }
        return player.getData(WizardryAttachment.WIZARD_PLAYER_DATA.get()).hasSpellBeenDiscovered(spell);
    }

    public static boolean isFirstPerson(Entity entity) {
        return entity == Minecraft.getInstance().getCameraEntity() && Minecraft.getInstance().options.getCameraType().isFirstPerson();
    }

    public static void playMovingSound(Entity entity, SoundEvent sound, SoundSource category, float volume, float pitch, boolean repeat) {
        Minecraft.getInstance().getSoundManager().play(
                new MovingSoundEntity<>(entity, sound, category, volume, pitch, repeat)
        );
    }

    public static void spawnTornadoParticle(
            Level level,
            double x, double y, double z,
            double velX, double velZ,
            double radius, int maxAge,
            BlockState block, BlockPos pos
    ){
//        Minecraft.getInstance().particleEngine.add(new ParticleTornado(world, maxAge, x, z, radius, y, velX, velZ, block).setBlockPos(pos));
    }
}
