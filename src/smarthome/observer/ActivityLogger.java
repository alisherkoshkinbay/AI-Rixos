package smarthome.observer;

import smarthome.bridge.Device;

// OBSERVER: құрылғы өзгерген сайын журналға жазады
public class ActivityLogger implements DeviceListener {
    private final String room;
    private final ActivityLog log;

    public ActivityLogger(String room, ActivityLog log) { this.room = room; this.log = log; }

    @Override
    public void onChange(Device device, String event) {
        log.addEvent(room, device.getName(), event);
    }
}
