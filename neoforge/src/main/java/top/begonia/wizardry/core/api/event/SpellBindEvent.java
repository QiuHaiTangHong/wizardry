package top.begonia.wizardry.core.api.event;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import top.begonia.wizardry.core.inventory.menu.ArcaneWorkbenchMenu;

public class SpellBindEvent extends Event implements ICancellableEvent {
    private final Player player;
    private final ArcaneWorkbenchMenu menu;

    public SpellBindEvent(Player player, ArcaneWorkbenchMenu menu) {
        super();
        this.player = player;
        this.menu = menu;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ArcaneWorkbenchMenu getMenu() {
        return this.menu;
    }
}
