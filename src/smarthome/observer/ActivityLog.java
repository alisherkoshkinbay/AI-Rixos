package smarthome.observer;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

// Барлық бөлмелердің оқиға журналы (Observer-лер осыған жазады)
public class ActivityLog {
    public enum Kind { EVENT, COMMAND }

    public static class Entry {
        public final LocalTime time = LocalTime.now();
        public final long millis = System.currentTimeMillis();
        public final Kind kind;
        public final String room, source, text;

        Entry(Kind kind, String room, String source, String text) {
            this.kind = kind; this.room = room; this.source = source; this.text = text;
        }
    }

    private final List<Entry> entries = new ArrayList<>();
    private final List<Runnable> listeners = new CopyOnWriteArrayList<>();

    public synchronized void addEvent(String room, String device, String text) {
        Entry e = new Entry(Kind.EVENT, room, device, text);
        if (!entries.isEmpty()) {
            Entry last = entries.get(entries.size() - 1);
            // слайдерді сүйреген кезде бірдей оқиғалар жиналмасын
            if (last.kind == Kind.EVENT && last.room.equals(room) && last.source.equals(device)
                    && e.millis - last.millis < 700) {
                entries.set(entries.size() - 1, e);
                fire();
                return;
            }
        }
        append(e);
    }

    public synchronized void addCommand(String room, String text) {
        append(new Entry(Kind.COMMAND, room, "Команда", text));
    }

    private void append(Entry e) {
        entries.add(e);
        if (entries.size() > 500) entries.remove(0);
        fire();
    }

    public synchronized List<Entry> snapshot() { return new ArrayList<>(entries); }
    public synchronized void clear() { entries.clear(); fire(); }
    public void addListener(Runnable r) { listeners.add(r); }
    private void fire() { for (Runnable r : listeners) r.run(); }
}
