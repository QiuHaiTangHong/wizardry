package top.begonia.wizardry.api.core.data;

public interface IResultData {
    default boolean isResult() {
        return true;
    }

    Class<? extends IResultData> getDataClass();
}
