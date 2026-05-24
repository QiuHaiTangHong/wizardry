package top.begonia.wizardry.core.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
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

    private static final Map<TabsEnum, List<Consumer<BuildCreativeModeTabContentsEvent>>> TABS_CONTENTS = new EnumMap<>(TabsEnum.class);

    static {
        for (TabsEnum type : TabsEnum.values()) {
            TABS_CONTENTS.put(type, new ArrayList<>());
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

        @Contract(" -> new")
        public @NonNull MutableComponent getDisplayName() {
            return Component.translatable("itemGroup." + this.tab);
        }

        @Override
        public String toString() {
            return this.tab;
        }
    }

    public static void addToTabs(TabsEnum tabsEnum, Supplier<? extends ItemLike> item) {
        TABS_CONTENTS.get(tabsEnum).add(event -> event.accept(item.get()));
    }

    public static void addSpecialToTabs(TabsEnum tabsEnum, Consumer<BuildCreativeModeTabContentsEvent> populator) {
        TABS_CONTENTS.get(tabsEnum).add(populator);
    }

    public static void addItemsToEvent(BuildCreativeModeTabContentsEvent event, TabsEnum tabsEnum) {
        List<Consumer<BuildCreativeModeTabContentsEvent>> contents = TABS_CONTENTS.get(tabsEnum);
        for (Consumer<BuildCreativeModeTabContentsEvent> populator : contents) {
            populator.accept(event);
        }
    }

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Wizardry.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> GEAR = CREATIVE_TABS.register(TabsEnum.GEAR.toString(), () -> CreativeModeTab.builder()
            .title(TabsEnum.GEAR.getDisplayName())
            .icon(() -> new ItemStack(WizardryItems.ARMOUR.get()))
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
