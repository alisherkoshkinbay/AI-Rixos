package smarthome.bridge;

public class Curtains extends BaseDevice {
    public Curtains() { super("curtains", "Перде", 0, 100, 0, "%", false, true); }

    @Override public double getPowerWatts() { return 0; }
    @Override public String getStatusText() {
        if (level == 0) return "Жабық";
        if (level == 100) return "Толық ашық";
        return "Ашық · " + level + "%";
    }
    @Override protected String levelEvent() { return "ашықтық: " + level + "%"; }
}
