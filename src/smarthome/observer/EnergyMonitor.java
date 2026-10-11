package smarthome.observer;

import smarthome.bridge.Device;
import smarthome.model.Room;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

// OBSERVER: бөлменің қуат тұтынуын бақылайды (Вт, кВт·сағ, құны)
public class EnergyMonitor implements DeviceListener {
    public static final double TARIFF = 25.0;              // ₸ / кВт·сағ
    private static final double SIM_SECONDS_PER_TICK = 60; // демо: 1 нақты секунд = 1 минут

    private final Room room;
    private double kwh, watts;
    private final Deque<Double> samples = new ArrayDeque<>();

    public EnergyMonitor(Room room) { this.room = room; }

    @Override public void onChange(Device device, String event) { watts = room.totalWatts(); }

    public synchronized void tick() {
        watts = room.totalWatts();
        kwh += watts * SIM_SECONDS_PER_TICK / 3_600_000.0;
        samples.addLast(watts);
        while (samples.size() > 90) samples.removeFirst();
    }

    public synchronized void prefill() {
        watts = room.totalWatts();
        samples.clear();
        for (int i = 0; i < 60; i++) samples.addLast(watts);
    }

    public double getWatts() { return watts; }
    public synchronized double getKwh() { return kwh; }
    public synchronized double getCost() { return kwh * TARIFF; }
    public synchronized List<Double> getSamples() { return new ArrayList<>(samples); }
}
