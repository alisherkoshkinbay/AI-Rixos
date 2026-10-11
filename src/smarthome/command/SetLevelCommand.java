package smarthome.command;

import smarthome.bridge.AdvancedRemote;

public class SetLevelCommand implements Command {
    private final AdvancedRemote remote;
    private int level;
    private int previous;
    private long lastTouch;

    public SetLevelCommand(AdvancedRemote remote, int level) {
        this.remote = remote;
        this.level = level;
    }

    @Override
    public void execute() {
        previous = remote.getLevel();
        remote.setLevel(level);
        lastTouch = System.currentTimeMillis();
    }

    @Override public void undo() { remote.setLevel(previous); }

    @Override
    public String getName() {
        return remote.getDevice().getName() + " → " + level + remote.getDevice().getUnit();
    }

    @Override
    public boolean mergeWith(Command other) {
        if (!(other instanceof SetLevelCommand)) return false;
        SetLevelCommand o = (SetLevelCommand) other;
        if (o.remote.getDevice() != remote.getDevice()) return false;
        if (System.currentTimeMillis() - lastTouch > 800) return false;
        remote.setLevel(o.level);
        level = o.level;
        lastTouch = System.currentTimeMillis();
        return true;
    }
}
