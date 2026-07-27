package top.begonia.wizardry.core.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.inventory.menu.ArcaneWorkbenchMenu;
import top.begonia.wizardry.core.inventory.menu.BookshelfMenu;

public final class WizardryMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, Wizardry.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<ArcaneWorkbenchMenu>> ARCANE_WORKBENCH = MENUS.register(
            "arcane_workbench",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new ArcaneWorkbenchMenu(windowId, inv, data.readBlockPos()))
    );
    public static final DeferredHolder<MenuType<?>, MenuType<BookshelfMenu>> BOOKSHELF = MENUS.register(
            "bookshelf",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new BookshelfMenu(windowId, inv, data.readBlockPos()))
    );

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
