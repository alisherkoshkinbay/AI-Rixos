package smarthome.command;

import smarthome.bridge.RemoteControl;

public class PowerOffCommand implements Command {
    private final RemoteControl remote;
    private boolean wasOn;

    public PowerOffCommand(RemoteControl remote) { this.remote = remote; }

    @Override public void execute() { wasOn = remote.isOn(); remote.off(); }
    @Override public void undo() { if (wasOn) remote.on(); }
    @Override public String getName() {
        return remote.getDevice().getName() + ": " + remote.getDevice().getOffLabel();
    }
}
