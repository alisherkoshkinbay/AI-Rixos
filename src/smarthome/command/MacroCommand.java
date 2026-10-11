package smarthome.command;

import java.util.List;

// Сценарий = бірнеше команданың жиынтығы (Composite-ке ұқсас). Undo кері ретпен
public class MacroCommand implements Command {
    private final String name;
    private final List<Command> commands;

    public MacroCommand(String name, List<Command> commands) {
        this.name = name; this.commands = commands;
    }

    @Override public void execute() { for (Command c : commands) c.execute(); }

    @Override
    public void undo() {
        for (int i = commands.size() - 1; i >= 0; i--) commands.get(i).undo();
    }

    @Override public String getName() { return name; }
}
