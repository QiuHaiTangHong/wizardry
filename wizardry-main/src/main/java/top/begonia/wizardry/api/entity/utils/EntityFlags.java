package top.begonia.wizardry.api.entity.utils;

public enum EntityFlags {
    CHARGING(0x01);
    private final int flag;

    EntityFlags(int flag) {
        this.flag = flag;
    }

    public int flag() {
        return flag;
    }
}
