package top.begonia.wizardry.common.registry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.common.data.player.WizardPlayerData;

import java.util.function.Supplier;

public final class WizardryAttachment {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Wizardry.MODID);

    public static final Supplier<AttachmentType<WizardPlayerData>> WIZARD_PLAYER_DATA = ATTACHMENT_TYPES.register(
            "wizard_player_data",
            () -> AttachmentType.builder(WizardPlayerData::getDefault)
                    .serialize(WizardPlayerData.CODEC)
                    .sync(WizardPlayerData.STREAM_CODEC)
                    .copyOnDeath()
                    .build()
    );

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
