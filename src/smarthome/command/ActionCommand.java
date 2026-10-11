package smarthome.command;

// Кез келген әрекетті команда етіп орайтын жалпы класс
public class ActionCommand implements Command {
    private final String name;
    private final Runnable doIt, undoIt;

    public ActionCommand(String name, Runnable doIt, Runnable undoIt) {
        this.name = name; this.doIt = doIt; this.undoIt = undoIt;
    }

    @Override public void execute() { doIt.run(); }
    @Override public void undo() { undoIt.run(); }
    @Override public String getName() { return name; }
}
