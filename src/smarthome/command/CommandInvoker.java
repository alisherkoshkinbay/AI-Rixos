package smarthome.command;

import java.util.ArrayDeque;
import java.util.Deque;

// COMMAND: invoker. Undo / Redo стектерін басқарады
public class CommandInvoker {
    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();

    /** @return true, егер команда алдыңғысымен біріктірілсе */
    public boolean run(Command c) {
        redoStack.clear();
        if (!undoStack.isEmpty() && undoStack.peek().mergeWith(c)) return true;
        c.execute();
        undoStack.push(c);
        if (undoStack.size() > 100) undoStack.removeLast();
        return false;
    }

    public Command undo() {
        if (undoStack.isEmpty()) return null;
        Command c = undoStack.pop();
        c.undo();
        redoStack.push(c);
        return c;
    }

    public Command redo() {
        if (redoStack.isEmpty()) return null;
        Command c = redoStack.pop();
        c.execute();
        undoStack.push(c);
        return c;
    }

    public boolean canUndo() { return !undoStack.isEmpty(); }
    public boolean canRedo() { return !redoStack.isEmpty(); }
    public String peekUndoName() { return undoStack.isEmpty() ? "" : undoStack.peek().getName(); }
    public String peekRedoName() { return redoStack.isEmpty() ? "" : redoStack.peek().getName(); }
}
