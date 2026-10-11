package smarthome.bridge;

// BRIDGE: abstraction (пульт). Қандай құрылғы екенін білмейді, тек Device интерфейсімен жұмыс істейді
public class RemoteControl {
    protected final Device device;

    public RemoteControl(Device device) { this.device = device; }

    public Device getDevice() { return device; }
    public boolean isOn() { return device.isOn(); }
    public void on() { device.on(); }
    public void off() { device.off(); }
    public void toggle() { if (device.isOn()) device.off(); else device.on(); }
}
