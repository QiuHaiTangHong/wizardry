package top.begonia.wizardry.core.data.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.data.runtime.SpellContextFlow;
import top.begonia.wizardry.core.data.player.WizardPlayerDataOperator;
import top.begonia.wizardry.core.data.spell.definition.spell.part.SpellContext;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;

public record CastContinuousSpellPacket(
        int playerId,
        AbstractSpell spell,
        SpellContext context,
        int duration
) implements CustomPacketPayload {
    public static final Type<CastContinuousSpellPacket> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(Wizardry.MODID, "cast_continuous_spell"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CastContinuousSpellPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, CastContinuousSpellPacket::playerId,
            ByteBufCodecs.registry(WizardrySpells.SPELLS_KEY), CastContinuousSpellPacket::spell,
            SpellContext.STREAM_CODEC, CastContinuousSpellPacket::context,
            ByteBufCodecs.VAR_INT, CastContinuousSpellPacket::duration,
            CastContinuousSpellPacket::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(CastContinuousSpellPacket payload, @NonNull IPayloadContext context) {
        context.enqueueWork(() -> {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.level != null) {
                Entity entity = mc.level.getEntity(payload.playerId());
                if (entity instanceof Player targetPlayer) {
                    WizardPlayerDataOperator wizardData = WizardPlayerDataOperator.get(targetPlayer);
                    wizardData.startCastingContinuousSpell(payload.spell(), payload.context(), payload.duration());
                }
            }
        });
    }
}
