package top.begonia.wizardry;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.time.Month;

@Mod(Wizardry.MODID)
public class Wizardry {
    public static final String MODID = "wizardry";
    public static String VERSION = "unknown";
    public static String MC_VERSION = "unknown";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static boolean tisTheSeason = LocalDate.now().getMonth() == Month.DECEMBER && LocalDate.now().getDayOfMonth() >= 24 && LocalDate.now().getDayOfMonth() <= 26;
}
