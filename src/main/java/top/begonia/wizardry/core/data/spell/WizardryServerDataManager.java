package top.begonia.wizardry.core.data.spell;

import net.minecraft.resources.FileToIdConverter;
import net.neoforged.api.distmarker.Dist;
import top.begonia.wizardry.core.api.data.AbstractWizardryDataManager;

public class WizardryServerDataManager extends AbstractWizardryDataManager {
    public static final WizardryServerDataManager INSTANCE = new WizardryServerDataManager();

    public WizardryServerDataManager() {
        super(FileToIdConverter.json("custom"));
    }

    @Override
    protected Dist getSupportedDist() {
        return Dist.DEDICATED_SERVER;
    }
}
