package top.begonia.wizardry.api.constants;

import top.begonia.wizardry.Wizardry;

public enum FriendlyFire {
    ALL("All", false, false),
    ONLY_PLAYERS("Only players", false, true),
    ONLY_OWNED("Only summoned/tamed creatures", true, false),
    NONE("None", true, true);

    public static final String[] names;

    static {
        names = new String[values().length];
        for (FriendlyFire setting : values()) {
            names[setting.ordinal()] = setting.name;
        }
    }

    public final String name;
    public final boolean blockPlayers;
    public final boolean blockOwned;

    FriendlyFire(String name, boolean blockPlayers, boolean blockOwned) {
        this.name = name;
        this.blockPlayers = blockPlayers;
        this.blockOwned = blockOwned;
    }

    public static FriendlyFire fromName(String name) {

        for (FriendlyFire setting : values()) {
            if (setting.name.equalsIgnoreCase(name)) return setting;
        }

        Wizardry.LOGGER.info("Invalid string for the friendly fire setting. Using default (all) instead.");
        return ALL;
    }

}
