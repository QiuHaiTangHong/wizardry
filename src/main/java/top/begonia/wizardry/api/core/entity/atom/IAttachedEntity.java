package top.begonia.wizardry.api.core.entity.atom;

import net.minecraft.core.BlockPos;
import org.jspecify.annotations.Nullable;

public interface IAttachedEntity {
    @Nullable BlockPos getBoundOrigin();

    void setBoundOrigin(@javax.annotation.Nullable BlockPos boundOriginIn);
}
