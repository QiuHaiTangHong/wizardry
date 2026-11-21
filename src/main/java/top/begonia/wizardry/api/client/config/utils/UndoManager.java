package top.begonia.wizardry.api.client.config.utils;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A class representing an undo/redo buffer.<p>
 * <p>
 * Every undo step is represented as 2 actions, one to initially execute when the step is added and
 * to redo after an undo, and one to execute to undo the step. Both get a captured parameter to make
 * defining them inline or reusing the code portion easier.
 */
public final class UndoManager {
    public record Step<T>(Consumer<T> run, T newValue, Consumer<T> undo, T oldValue) {
        private void runUndo() {
            undo.accept(oldValue);
        }

        private void runRedo() {
            run.accept(newValue);
        }
    }

    private final List<UndoManager.Step<?>> undoes = new ArrayList<>();
    private final List<UndoManager.Step<?>> redoes = new ArrayList<>();

    public void undo() {
        if (this.canUndo()) {
            UndoManager.Step<?> step = undoes.removeLast();
            step.runUndo();
            redoes.add(step);
        }
    }

    public void redo() {
        if (this.canRedo()) {
            UndoManager.Step<?> step = redoes.removeLast();
            step.runRedo();
            undoes.add(step);
        }
    }

    private void add(UndoManager.Step<?> step, boolean execute) {
        undoes.add(step);
        redoes.clear();
        if (execute) {
            step.runRedo();
        }
    }

    @Contract("_, _, _, _ -> new")
    public <T> UndoManager.@NonNull Step<T> step(Consumer<T> run, T newValue, Consumer<T> undo, T oldValue) {
        return new UndoManager.Step<>(run, newValue, undo, oldValue);
    }

    public <T> void add(Consumer<T> run, T newValue, Consumer<T> undo, T oldValue) {
        add(step(run, newValue, undo, oldValue), true);
    }

    public <T> void addNoExecute(Consumer<T> run, T newValue, Consumer<T> undo, T oldValue) {
        add(step(run, newValue, undo, oldValue), false);
    }

    public void add(UndoManager.Step<?>... steps) {
        add(ImmutableList.copyOf(steps));
    }

    public void add(final List<UndoManager.Step<?>> steps) {
        add(new UndoManager.Step<>(n -> steps.forEach(UndoManager.Step::runRedo), null, n -> steps.forEach(UndoManager.Step::runUndo), null), true);
    }

    public boolean canUndo() {
        return !undoes.isEmpty();
    }

    public boolean canRedo() {
        return !redoes.isEmpty();
    }
}
