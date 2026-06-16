package top.begonia.wizardry.core.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.constants.ElementEnum;

import javax.annotation.Nullable;
import java.util.Objects;

public class ImbuementActivateEvent extends Event implements ICancellableEvent {

    private final ItemStack input;
    private final ElementEnum[] receptacleElements;
    @Nullable
    private final Level level;
    @Nullable
    private final Player lastUser;

    @NonNull
    private ItemStack result;

    public ImbuementActivateEvent(@NonNull ItemStack input, ElementEnum[] receptacleElements, @Nullable Level level, @Nullable Player lastUser, @NonNull ItemStack result) {
        super();
        this.input = Objects.requireNonNull(input, "Input ItemStack cannot be null");
        this.receptacleElements = receptacleElements;
        this.level = level;
        this.lastUser = lastUser;
        this.result = Objects.requireNonNull(result, "Initial result ItemStack cannot be null");
    }

    public @NonNull ItemStack getInput() {
        return this.input;
    }

    public ElementEnum[] getReceptacleElements() {
        return this.receptacleElements;
    }

    public @Nullable Level getLevel() {
        return this.level;
    }

    public @Nullable Player getLastUser() {
        return this.lastUser;
    }

    public @NonNull ItemStack getResult() {
        return this.result;
    }

    public void setResult(@NonNull ItemStack result) {
        this.result = Objects.requireNonNull(result, "Result ItemStack cannot be set to null, use event.setCanceled(true) instead");
    }
}
