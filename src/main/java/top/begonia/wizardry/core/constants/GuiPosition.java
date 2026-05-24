package top.begonia.wizardry.core.constants;

import top.begonia.wizardry.Wizardry;

public enum GuiPosition {

    BOTTOM_LEFT("Bottom left", false, false, false),
    TOP_LEFT("Top left", false, true, false),
    TOP_RIGHT("Top right", true, true, false),
    BOTTOM_RIGHT("Bottom right", true, false, false),
    FOLLOW_BOTTOM("Follow wand, bottom", false, false, true),
    FOLLOW_TOP("Follow wand, top", false, true, true),
    OPPOSITE_BOTTOM("Opposite wand, bottom", true, false, true),
    OPPOSITE_TOP("Opposite wand, top", true, true, true);

    public static final String[] names;

    static {
        names = new String[values().length];
        for (GuiPosition position : values()) {
            names[position.ordinal()] = position.name;
        }
    }

    public final String name;
    public final boolean flipX;
    public final boolean flipY;
    public final boolean dynamic;

    GuiPosition(String name, boolean flipX, boolean flipY, boolean dynamic) {
        this.name = name;
        this.flipX = flipX;
        this.flipY = flipY;
        this.dynamic = dynamic;
    }

    public static GuiPosition fromName(String name) {

        for (GuiPosition position : values()) {
            if (position.name.equalsIgnoreCase(name)) return position;
        }

        Wizardry.LOGGER.info("Invalid string for the spell HUD position. Using default (bottom left) instead.");
        return BOTTOM_LEFT;
    }

}
