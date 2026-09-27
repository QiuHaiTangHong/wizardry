package top.begonia.wizardry.core.worldgen.structure;

import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class WizardryUndergroundStructure extends Structure {
    protected WizardryUndergroundStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected @NonNull Optional<GenerationStub> findGenerationPoint(@NonNull GenerationContext generationContext) {
        return Optional.empty();
    }

    @Override
    public StructureType<?> type() {
        return null;
    }
}
