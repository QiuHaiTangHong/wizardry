package top.begonia.wizardry;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.apache.maven.artifact.versioning.Restriction;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.time.Month;

@Mod(Wizardry.MODID)
public class Wizardry {
    private static final String DEFAULT_VALUE = "unknown";
    public static final String MODID = "wizardry";
    public static String VERSION = DEFAULT_VALUE;
    public static String MC_VERSION = DEFAULT_VALUE;
    public static final Logger LOGGER = LogUtils.getLogger();
    public static boolean tisTheSeason = LocalDate.now().getMonth() == Month.DECEMBER && LocalDate.now().getDayOfMonth() >= 24 && LocalDate.now().getDayOfMonth() <= 26;

    public static void onInit(@NonNull ModContainer modContainer) {
        Wizardry.VERSION = modContainer
                .getModInfo()
                .getVersion()
                .toString();
        Wizardry.MC_VERSION = modContainer
                .getModInfo()
                .getDependencies()
                .stream()
                .filter(modVersion -> modVersion.getModId().equals(Identifier.DEFAULT_NAMESPACE))
                .findFirst()
                .map(modVersion -> {
                    Restriction defaultArtifactVersion = modVersion.getVersionRange().getRestrictions().getFirst();
                    return defaultArtifactVersion.getLowerBound() + "-" + defaultArtifactVersion.getUpperBound();
                })
                .orElse(DEFAULT_VALUE);
    }
}
