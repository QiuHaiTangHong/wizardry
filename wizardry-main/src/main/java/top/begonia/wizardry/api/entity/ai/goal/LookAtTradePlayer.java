package top.begonia.wizardry.api.entity.ai.goal;

import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import top.begonia.wizardry.core.entity.living.wizard.impl.WizardEntity;

public class LookAtTradePlayer extends LookAtPlayerGoal {
    private final WizardEntity wizard;

    public LookAtTradePlayer(WizardEntity wizard, float lookDistance) {
        super(wizard, Player.class, lookDistance);
        this.wizard = wizard;
    }

    @Override
    public boolean canUse() {
        Player tradingPlayer = this.wizard.getTradingPlayer();
        if (tradingPlayer == null) {
            return false;
        } else if (this.wizard.stillValid(tradingPlayer)) {
            this.lookAt = this.wizard.getTradingPlayer();
            return true;
        } else {
            return false;
        }
    }
}
