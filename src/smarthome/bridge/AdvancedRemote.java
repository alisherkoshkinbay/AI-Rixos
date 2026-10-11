package smarthome.bridge;

// Abstraction кеңейтіледі: құрылғы кодына тиіспей жаңа мүмкіндік қосылды
public class AdvancedRemote extends RemoteControl {
    public AdvancedRemote(Device device) { super(device); }

    public void setLevel(int level) { device.setLevel(level); }
    public int getLevel() { return device.getLevel(); }
    public void step(int delta) { device.setLevel(device.getLevel() + delta); }
}
