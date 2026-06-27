package top.begonia.wizardry.client.data.manager;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModLoader;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.api.data.AbstractWizardryDataManager;
import top.begonia.wizardry.core.api.event.data.RegisterDataParserEvent;

import java.util.*;

public class WizardryClientDataManager extends AbstractWizardryDataManager {
    private static final WizardryClientDataManager INSTANCE = new WizardryClientDataManager();
    private final String langPrefix;
    private final String expectedLanguagePath;

    private WizardryClientDataManager() {
        super(FileToIdConverter.json("custom"));
        String currentLang = Minecraft.getInstance().getLanguageManager().getSelected().toLowerCase(Locale.ROOT);
        this.langPrefix = "texts/" + currentLang + "/";
        this.expectedLanguagePath = "texts/" + currentLang + "/";
    }

    public void fireRegisterEvents() {
        ModLoader.postEvent(new RegisterDataParserEvent.ClientRegisterDataParserEvent(this.getParserRegistry()));
    }

    public static WizardryClientDataManager getInstance() {
        return INSTANCE;
    }

    @Override
    protected Identifier cleanseIdentifier(@NonNull Identifier originalId) {
        String originalPath = originalId.getPath();
        if (originalPath.startsWith(this.langPrefix)) {
            return originalId.withPath(originalPath.substring(this.langPrefix.length()));
        }
        return originalId;
    }

    @Override
    protected Dist getSupportedDist() {
        return Dist.CLIENT;
    }

    @Override
    protected boolean listerFilter(Identifier finalId, Identifier location, Resource resource) {
        String path = location.getPath();
        if (path.contains("texts/")) {
            return path.contains(this.expectedLanguagePath);
        }
        return true;
    }
}
