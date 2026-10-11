package smarthome.bridge;

public class Television extends BaseDevice {
    public Television() { super("tv", "Теледидар", 0, 100, 30, "%", true, true); }

    @Override public double getPowerWatts() { return on ? 80 + level * 0.5 : 0; }
    @Override public String getStatusText() { return on ? "Қосулы · дыбыс " + level + "%" : "Өшірулі"; }
    @Override protected String levelEvent() { return "дыбыс: " + level + "%"; }
}
