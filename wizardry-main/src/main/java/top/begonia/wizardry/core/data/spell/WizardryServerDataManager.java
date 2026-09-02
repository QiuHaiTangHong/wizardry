package top.begonia.wizardry.core.data.spell;

import net.minecraft.resources.FileToIdConverter;
import net.neoforged.fml.ModLoader;
import top.begonia.wizardry.api.data.AbstractWizardryDataManager;
import top.begonia.wizardry.api.event.data.RegisterDataParserEvent;

public class WizardryServerDataManager extends AbstractWizardryDataManager {
    private static final WizardryServerDataManager INSTANCE = new WizardryServerDataManager();

    public static WizardryServerDataManager getInstance() {
        return INSTANCE;
    }

    public WizardryServerDataManager() {
        super(FileToIdConverter.json("custom"));
    }

    public void fireRegisterEvents() {
        ModLoader.postEvent(new RegisterDataParserEvent.CommonRegisterDataParserEvent(this.getParserRegistry()));
    }
}
