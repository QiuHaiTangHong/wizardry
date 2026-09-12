package top.begonia.wizardry.core.entity.ai.goal;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import top.begonia.wizardry.core.entity.living.WizardEntity;

import java.util.EnumSet;

public class TradePlayer extends Goal {
    @Override
    public boolean canUse() {
        if (!this.wizard.isAlive()) {
            return false;
        } else if (this.wizard.isInWater()) {
            return false;
        } else if (this.wizard.noPhysics) {
            return false;
        } else if (this.wizard.getDeltaMovement().lengthSqr() > 0.0001D) {
            return false;
        } else {
            Player entityplayer = this.wizard.getTradingPlayer();
            if (entityplayer == null) {
                return false;
            } else if (this.wizard.position().distanceToSqr(entityplayer.position()) > 16.0D) {
                return false;
            } else {
                return entityplayer.hasContainerOpen();
            }
        }
    }

    private final WizardEntity wizard;

    public TradePlayer(WizardEntity wizard) {
        this.wizard = wizard;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canContinueToUse() {
        if (this.wizard.getTradingPlayer() == null) {
            return false;
        }
        return this.wizard.stillValid(this.wizard.getTradingPlayer());
    }

    @Override
    public void start() {
        this.wizard.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.wizard.setTradingPlayer(null);
    }
}
