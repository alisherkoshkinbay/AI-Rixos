package smarthome.bridge;

import smarthome.observer.DeviceListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

// Құрылғылардың ортақ базасы. OBSERVER: күй өзгергенде тыңдаушыларға хабарлайды
public abstract class BaseDevice implements Device {
    private final String id, name, unit;
    private final int min, max;
    private final boolean hasPower, hasLevel;
    protected boolean on;
    protected int level;
    private final List<DeviceListener> listeners = new CopyOnWriteArrayList<>();

    protected BaseDevice(String id, String name, int min, int max, int def,
                         String unit, boolean hasPower, boolean hasLevel) {
        this.id = id; this.name = name; this.min = min; this.max = max;
        this.level = def; this.unit = unit; this.hasPower = hasPower; this.hasLevel = hasLevel;
    }

    @Override public String getId() { return id; }
    @Override public String getName() { return name; }
    @Override public int getMin() { return min; }
    @Override public int getMax() { return max; }
    @Override public String getUnit() { return unit; }
    @Override public boolean hasPower() { return hasPower; }
    @Override public boolean hasLevel() { return hasLevel; }
    @Override public boolean isOn() { return on; }
    @Override public int getLevel() { return level; }
    @Override public String getOnLabel() { return "қосу"; }
    @Override public String getOffLabel() { return "өшіру"; }
    @Override public void addListener(DeviceListener l) { listeners.add(l); }

    @Override public void on() { if (on) return; on = true; fire(onEvent()); }
    @Override public void off() { if (!on) return; on = false; fire(offEvent()); }

    @Override
    public void setLevel(int v) {
        int n = Math.max(min, Math.min(max, v));
        if (n == level) return;
        level = n;
        fire(levelEvent());
    }

    protected String onEvent() { return "қосылды"; }
    protected String offEvent() { return "өшірілді"; }
    protected String levelEvent() { return "деңгей: " + level + unit; }

    protected void fire(String event) {
        for (DeviceListener l : listeners) l.onChange(this, event);
    }
}
