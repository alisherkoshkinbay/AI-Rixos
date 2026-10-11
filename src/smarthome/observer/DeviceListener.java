package smarthome.observer;

import smarthome.bridge.Device;

// OBSERVER: құрылғы күйі өзгергенде хабарланатын тыңдаушы
public interface DeviceListener {
    void onChange(Device device, String event);
}
