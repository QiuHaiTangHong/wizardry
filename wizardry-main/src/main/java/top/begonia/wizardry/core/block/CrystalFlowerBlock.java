package top.begonia.wizardry.core.block;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.api.particle.options.QuadParticleOptions;
import top.begonia.wizardry.client.WizardryClient;
import top.begonia.wizardry.core.config.ServerConfig;
import top.begonia.wizardry.core.registry.WizardryBlocks;
import top.begonia.wizardry.core.registry.WizardryParticles;

@EventBusSubscriber(modid = Wizardry.MODID)
public class CrystalFlowerBlock extends BushBlock {

    protected static final VoxelShape SHAPE = Block.box(4.8D, 0.0D, 4.8D, 11.2D, 9.6D, 11.2D);

    public CrystalFlowerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void animateTick(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull RandomSource random) {
        if (random.nextBoolean() && level instanceof ClientLevel clientLevel) {
            WizardryClient.particleManager.getParticle(
                    clientLevel,
                    new QuadParticleOptions(WizardryParticles.SPARKLE.get()),
                    pos.getX() + random.nextDouble(), pos.getY() + random.nextDouble() / 2 + 0.5, pos.getZ() + random.nextDouble()
            ).ifPresent(p -> p.speed(0.0f, 0.01f, 0.0f)
                    .time(20 + random.nextInt(10))
                    .color(0.5f + (random.nextFloat() / 2),
                            0.5f + (random.nextFloat() / 2),
                            0.5f + (random.nextFloat() / 2)
                    )
                    .spawn()
            );
        }
    }

    @SubscribeEvent
    public static void onBonemealEvent(@NonNull BonemealEvent event) {
        Level level = event.getLevel();
        BlockState clickedState = event.getState();
        if (ServerConfig.bonemealGrowsCrystalFlowers && clickedState.is(Blocks.GRASS_BLOCK)) {
            RandomSource random = level.getRandom();
            if (random.nextFloat() >= 0.45F) {
                return;
            }
            BlockPos originPos = event.getPos();
            BlockPos targetPos = originPos.offset(
                    random.nextInt(8) - random.nextInt(8),
                    random.nextInt(4) - random.nextInt(4),
                    random.nextInt(8) - random.nextInt(8)
            );
            boolean isAir = level.isEmptyBlock(targetPos);
            boolean isSafeHeight = !level.dimensionType().hasCeiling() || targetPos.getY() < 127;

            if (isAir && isSafeHeight) {
                BlockState flowerState = WizardryBlocks.CRYSTAL_FLOWER.get().defaultBlockState();
                if (flowerState.canSurvive(level, targetPos)) {
                    level.setBlock(targetPos, flowerState, 3);
                    event.setSuccessful(true);
                }
            }
        }
    }
}
