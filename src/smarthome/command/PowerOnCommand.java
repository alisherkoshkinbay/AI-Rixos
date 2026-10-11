package smarthome.command;

import smarthome.bridge.RemoteControl;

public class PowerOnCommand implements Command {
    private final RemoteControl remote;
    private boolean wasOn;

    public PowerOnCommand(RemoteControl remote) { this.remote = remote; }

    @Override public void execute() { wasOn = remote.isOn(); remote.on(); }
    @Override public void undo() { if (!wasOn) remote.off(); }
    @Override public String getName() {
        return remote.getDevice().getName() + ": " + remote.getDevice().getOnLabel();
    }
}
