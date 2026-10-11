package smarthome.model;

import smarthome.bridge.*;
import smarthome.command.CommandInvoker;
import smarthome.observer.DeviceListener;
import smarthome.observer.EnergyMonitor;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

// Қонақүй бөлмесі: құрылғылар, команда тарихы, энергия мониторы
public class Room {
    public enum Status {
        FREE("Бос"), OCCUPIED("Қонақ бар");
        public final String label;
        Status(String label) { this.label = label; }
    }

    private final String number, type;
    private Status status = Status.FREE;
    private final Map<String, Device> devices = new LinkedHashMap<>();
    private final CommandInvoker invoker = new CommandInvoker();
    private final EnergyMonitor energy;
    private final List<Runnable> changeListeners = new CopyOnWriteArrayList<>();

    public Room(String number, String type) {
        this.number = number;
        this.type = type;
        for (Device d : new Device[]{new Light(), new AirConditioner(), new Curtains(),
                new Television(), new DoorLock()}) {
            devices.put(d.getId(), d);
        }
        energy = new EnergyMonitor(this);
        addDeviceListener(energy);
        addDeviceListener((d, e) -> fireChange());
    }

    public String getNumber() { return number; }
    public String getType() { return type; }
    public Status getStatus() { return status; }
    public Device getDevice(String id) { return devices.get(id); }
    public Collection<Device> getDevices() { return devices.values(); }
    public CommandInvoker getInvoker() { return invoker; }
    public EnergyMonitor getEnergy() { return energy; }

    public void setStatus(Status s) {
        if (status != s) { status = s; fireChange(); }
    }

    public void addDeviceListener(DeviceListener l) {
        for (Device d : devices.values()) d.addListener(l);
    }

    public void addChangeListener(Runnable r) { changeListeners.add(r); }
    private void fireChange() { for (Runnable r : changeListeners) r.run(); }

    public double totalWatts() {
        double sum = 0;
        for (Device d : devices.values()) sum += d.getPowerWatts();
        return sum;
    }
}
