package smarthome.bridge;

public class AirConditioner extends BaseDevice {
    public AirConditioner() { super("ac", "Кондиционер", 16, 30, 24, "°C", true, true); }

    @Override public double getPowerWatts() { return on ? 600 + (30 - level) * 40 : 0; }
    @Override public String getStatusText() { return on ? "Салқындату · " + level + "°C" : "Өшірулі"; }
    @Override protected String levelEvent() { return "температура: " + level + "°C"; }
}
