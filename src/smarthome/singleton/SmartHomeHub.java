package smarthome.singleton;

import smarthome.model.Room;
import smarthome.observer.ActivityLog;
import smarthome.observer.ActivityLogger;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

// SINGLETON: бүкіл қонақүйдегі жалғыз орталық хаб (бөлмелер + ортақ журнал)
public class SmartHomeHub {
    private static SmartHomeHub instance;

    private final Map<String, Room> rooms = new LinkedHashMap<>();
    private final ActivityLog log = new ActivityLog();

    private SmartHomeHub() {
        //            бөлме         түрі               қонақ  жарық ac  перде tv  құлыптаулы
        addRoom(new Room("101", "Standard"),     false, -1,  -1,  0,   -1, true);
        addRoom(new Room("102", "Standard"),     true,  60,  23,  100, -1, true);
        addRoom(new Room("201", "Deluxe"),       true,  30,  22,  0,   25, true);
        addRoom(new Room("202", "Deluxe"),       false, -1,  -1,  0,   -1, true);
        addRoom(new Room("305", "Suite"),        true,  80,  23,  100, -1, true);
        addRoom(new Room("401", "Presidential"), false, -1,  -1,  0,   -1, true);
    }

    public static synchronized SmartHomeHub getInstance() {
        if (instance == null) instance = new SmartHomeHub();
        return instance;
    }

    private void addRoom(Room r, boolean occupied, int light, int ac, int curtains, int tv, boolean locked) {
        if (light > 0) { r.getDevice("light").setLevel(light); r.getDevice("light").on(); }
        if (ac > 0) { r.getDevice("ac").setLevel(ac); r.getDevice("ac").on(); }
        r.getDevice("curtains").setLevel(curtains);
        if (tv > 0) { r.getDevice("tv").setLevel(tv); r.getDevice("tv").on(); }
        if (!locked) r.getDevice("door").off();
        if (occupied) r.setStatus(Room.Status.OCCUPIED);
        r.getEnergy().prefill();
        r.addDeviceListener(new ActivityLogger(r.getNumber(), log)); // Observer қосылады
        rooms.put(r.getNumber(), r);
    }

    public Room getRoom(String number) { return rooms.get(number); }
    public Collection<Room> getRooms() { return rooms.values(); }
    public ActivityLog getLog() { return log; }
}
