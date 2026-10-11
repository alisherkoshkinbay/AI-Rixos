package smarthome.observer;

import smarthome.bridge.Device;

public class ConsoleLogger implements DeviceListener {
    @Override
    public void onChange(Device device, String event) {
        System.out.println("   [LOG] " + device.getName() + ": " + event);
    }
}
