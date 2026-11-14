package top.begonia.wizardry.api.data;

public interface IResultData {
    default boolean isResult() {
        return true;
    }

    Class<? extends IResultData> getDataClass();
}
