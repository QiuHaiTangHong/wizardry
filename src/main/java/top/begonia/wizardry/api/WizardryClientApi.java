package top.begonia.wizardry.api;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.api.data.IResultData;
import top.begonia.wizardry.api.data.manager.ClientDataManager;
import top.begonia.wizardry.api.particle.extension.builder.ParticleBuilder;
import top.begonia.wizardry.api.particle.manager.WizardryParticleManager;
import top.begonia.wizardry.api.particle.options.IParticleOptionsExtension;
import top.begonia.wizardry.client.gui.widget.Skin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WizardryClientApi {
    private static final WizardryClientApi INSTANCE = new WizardryClientApi();
    private final ClientDataManager dataManager = new ClientDataManager();
    private final WizardryParticleManager particleManager = new WizardryParticleManager();
    private final Map<String, Skin> skins = new HashMap<>();

    private WizardryClientApi() {
    }

    public static WizardryClientApi getInstance() {
        return WizardryClientApi.INSTANCE;
    }

    public void init(@NonNull IEventBus modEventBus, @NonNull ModContainer modContainer) {
        modEventBus.addListener(this::onRegisterReloadListeners);
    }

    private void onRegisterReloadListeners(final @NonNull AddClientReloadListenersEvent event) {
        event.addListener(
                Identifier.fromNamespaceAndPath(Wizardry.MODID, "data_manager"),
                this.dataManager
        );
    }

    public ClientDataManager dataManager() {
        return this.dataManager;
    }

    public WizardryParticleManager particleManager() {
        return this.particleManager;
    }

    public <T extends IResultData> Optional<T> getData(Identifier identifier, Class<T> type) {
        return this.dataManager.getData(identifier, type);
    }

    public <T extends IResultData> Map<Identifier, T> getAllDataByType(Class<T> expectedType) {
        return this.dataManager.getAllDataByType(expectedType);
    }

    public void spawnHealParticles(ClientLevel level, LivingEntity entity) {
        this.particleManager.spawnHealParticles(level, entity);
    }

    public void spawnShockParticles(ClientLevel level, double x, double y, double z) {
        this.particleManager.spawnShockParticles(level, x, y, z);
    }

    @NotNull
    public <T extends IParticleOptionsExtension> Optional<ParticleBuilder> getParticle(
            ClientLevel clientLevel,
            @NonNull T options,
            double x, double y, double z
    ) {
        return this.particleManager.getParticle(clientLevel, options, x, y, z);
    }

    @NotNull
    public <T extends IParticleOptionsExtension> Optional<ParticleBuilder> getParticle(
            ClientLevel clientLevel,
            @NonNull RandomSource random,
            @NonNull T options,
            double x, double y, double z,
            double radius, boolean move
    ) {
        return this.particleManager.getParticle(
                clientLevel,
                random,
                options,
                x, y, z,
                radius, move
        );
    }
}
