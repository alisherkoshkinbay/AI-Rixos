package smarthome.bridge;

// "on" = құлыптаулы
public class DoorLock extends BaseDevice {
    public DoorLock() {
        super("door", "Есік құлпы", 0, 0, 0, "", true, false);
        this.on = true;
    }

    @Override public double getPowerWatts() { return 0; }
    @Override public String getStatusText() { return on ? "Құлыптаулы" : "Ашық"; }
    @Override protected String onEvent() { return "құлыпталды"; }
    @Override protected String offEvent() { return "құлып ашылды"; }
    @Override public String getOnLabel() { return "құлыптау"; }
    @Override public String getOffLabel() { return "құлыпты ашу"; }
}
