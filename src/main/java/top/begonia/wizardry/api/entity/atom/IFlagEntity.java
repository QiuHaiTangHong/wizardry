package top.begonia.wizardry.api.entity.atom;

import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.entity.utils.EntityFlags;

public interface IFlagEntity {
    int getFlags();

    void setFlags(int flags);

    default boolean getFlag(@NonNull EntityFlags flag) {
        int flags = this.getFlags();
        return (flags & flag.flag()) != 0;
    }

    default void setFlag(EntityFlags flag, boolean value) {
        int flags = getFlags();
        if (value) {
            flags |= flag.flag();
        } else {
            flags &= ~flag.flag();
        }
        this.setFlags(flags);
    }
}
