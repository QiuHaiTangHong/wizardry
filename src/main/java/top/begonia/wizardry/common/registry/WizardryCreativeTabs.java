package top.begonia.wizardry.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.begonia.wizardry.Wizardry;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class WizardryCreativeTabs {
    private WizardryCreativeTabs() {
    }

    private static final Map<TabsEnum, List<Supplier<? extends ItemLike>>> TABS_CONTENTS = new EnumMap<>(TabsEnum.class);
    private static final Map<TabsEnum, List<Consumer<BuildCreativeModeTabContentsEvent>>> SPECIAL_CONTENTS = new EnumMap<>(TabsEnum.class);

    static {
        for (TabsEnum type : TabsEnum.values()) {
            TABS_CONTENTS.put(type, new ArrayList<>());
            SPECIAL_CONTENTS.put(type, new ArrayList<>());
        }
    }

    public enum TabsEnum {
        GEAR("gear"),
        SPELLS("spells"),
        WIZARDRY("");
        private final String tab;

        TabsEnum(String tab) {
            this.tab = Wizardry.MODID + tab;
        }

        public MutableComponent getDisplayName() {
            return Component.translatable("itemGroup." + this.tab);
        }

        @Override
        public String toString() {
            return this.tab;
        }
    }

    public static void addToTabs(TabsEnum tabsEnum, Supplier<? extends ItemLike> item) {
        TABS_CONTENTS.get(tabsEnum).add(item);
    }

    public static void addSpecialToTabs(TabsEnum tabsEnum, Consumer<BuildCreativeModeTabContentsEvent> populator) {
        SPECIAL_CONTENTS.get(tabsEnum).add(populator);
    }

    public static void addItemsToEvent(BuildCreativeModeTabContentsEvent event, TabsEnum tabsEnum) {
        List<Supplier<? extends ItemLike>> items = TABS_CONTENTS.get(tabsEnum);
        for (Supplier<? extends ItemLike> itemSupplier : items) {
            event.accept(itemSupplier.get());
        }
        List<Consumer<BuildCreativeModeTabContentsEvent>> specialPopulators = SPECIAL_CONTENTS.get(tabsEnum);
        for (Consumer<BuildCreativeModeTabContentsEvent> populator : specialPopulators) {
            populator.accept(event);
        }
    }

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Wizardry.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> GEAR = CREATIVE_TABS.register(TabsEnum.GEAR.toString(), () -> CreativeModeTab.builder()
            .title(TabsEnum.GEAR.getDisplayName())
            .icon(() -> new ItemStack(WizardryItems.WIZARD_HAT.get()))
            .build()
    );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SPELLS = CREATIVE_TABS.register(TabsEnum.SPELLS.toString(), () -> CreativeModeTab.builder()
            .title(TabsEnum.SPELLS.getDisplayName())
            .icon(() -> new ItemStack(WizardryItems.SPELL_BOOK.get()))
            .build()
    );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WIZARDRY = CREATIVE_TABS.register(TabsEnum.WIZARDRY.toString(), () -> CreativeModeTab.builder()
            .title(TabsEnum.WIZARDRY.getDisplayName())
            .icon(() -> new ItemStack(WizardryItems.WIZARD_HANDBOOK.get()))
            .build()
    );

    public static void register(net.neoforged.bus.api.IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}
