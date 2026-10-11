package smarthome.bridge;

public class Light extends BaseDevice {
    public Light() { super("light", "Жарық", 5, 100, 80, "%", true, true); }

    @Override public double getPowerWatts() { return on ? 40.0 * level / 100 : 0; }
    @Override public String getStatusText() { return on ? "Қосулы · " + level + "%" : "Өшірулі"; }
    @Override protected String levelEvent() { return "жарықтылық: " + level + "%"; }
}
