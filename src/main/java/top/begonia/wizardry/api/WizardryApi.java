package top.begonia.wizardry.api;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.api.data.IResultData;
import top.begonia.wizardry.api.data.manager.ServerDataManager;

import java.util.Optional;

public class WizardryApi {
    private static final WizardryApi INSTANCE = new WizardryApi();
    private final ServerDataManager dataManager = new ServerDataManager();

    private WizardryApi() {
    }

    public static WizardryApi getInstance() {
        return WizardryApi.INSTANCE;
    }

    public void init(@NonNull IEventBus modEventBus, @NonNull ModContainer modContainer) {
        NeoForge.EVENT_BUS.addListener(this::onAddReloadListeners);
    }

    private void onAddReloadListeners(@NonNull AddServerReloadListenersEvent event) {
        event.addListener(
                Identifier.fromNamespaceAndPath(Wizardry.MODID, "server_data_manager"),
                this.dataManager
        );
    }

    public ServerDataManager dataManager() {
        return this.dataManager;
    }

    public <T extends IResultData> Optional<T> getData(Identifier identifier, Class<T> type) {
        return this.dataManager.getData(identifier, type);
    }
}
