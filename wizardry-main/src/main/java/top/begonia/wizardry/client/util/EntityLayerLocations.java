package top.begonia.wizardry.client.util;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;
import top.begonia.wizardry.Wizardry;

public final class EntityLayerLocations{
    public static final ModelLayerLocation WIZARD_ENTITY = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(
                    Wizardry.MODID,
                    "wizard"
            ),
            "main"
    );
    public static final ModelLayerLocation HAMMER_ENTITY = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(
                    Wizardry.MODID,
                    "hammer"
            ),
            "main"
    );
    public static final ModelLayerLocation REMNANT_ENTITY = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(
                    Wizardry.MODID,
                    "remnant"
            ),
            "main"
    );
    public static final ModelLayerLocation ICE_BARRIER_ENTITY = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(
                    Wizardry.MODID,
                    "ice_barrier"
            ),
            "main"
    );
}
