package smarthome.facade;

import smarthome.bridge.AdvancedRemote;
import smarthome.bridge.Device;
import smarthome.command.*;
import smarthome.model.Room;
import smarthome.singleton.SmartHomeHub;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// FACADE: бөлмені басқарудың бәрі (пульттер, командалар, журнал, сценарийлер) бір қарапайым интерфейсте
public class SmartRoomFacade {
    private final Room room;
    private final SmartHomeHub hub = SmartHomeHub.getInstance();
    private final Map<String, AdvancedRemote> remotes = new LinkedHashMap<>();

    public SmartRoomFacade(Room room) {
        this.room = room;
        for (Device d : room.getDevices()) remotes.put(d.getId(), new AdvancedRemote(d)); // Bridge
    }

    public Room getRoom() { return room; }

    // ---------- ішкі көмекші әдістер ----------
    private Command on(String id) { return new PowerOnCommand(remotes.get(id)); }
    private Command off(String id) { return new PowerOffCommand(remotes.get(id)); }
    private Command level(String id, int v) { return new SetLevelCommand(remotes.get(id), v); }

    private Command status(Room.Status s) {
        Room.Status prev = room.getStatus();
        return new ActionCommand("Мәртебе: " + s.label, () -> room.setStatus(s), () -> room.setStatus(prev));
    }

    private void run(Command c) {
        boolean merged = room.getInvoker().run(c);
        if (!merged) hub.getLog().addCommand(room.getNumber(), c.getName());
    }

    private void macro(String name, Command... cs) {
        run(new MacroCommand(name, new ArrayList<>(Arrays.asList(cs))));
    }

    // ---------- сценарийлер ----------
    public void welcome() {
        macro("Қош келдіңіз", status(Room.Status.OCCUPIED), off("door"),
                on("light"), level("light", 80), on("ac"), level("ac", 23),
                level("curtains", 100), off("tv"));
    }

    public void nightMode() {
        macro("Түнгі режим", on("light"), level("light", 10), on("ac"), level("ac", 21),
                level("curtains", 0), off("tv"), on("door"));
    }

    public void movieMode() {
        macro("Кино режимі", on("light"), level("light", 15), on("ac"), level("ac", 23),
                level("curtains", 0), on("tv"), level("tv", 35));
    }

    public void morningMode() {
        macro("Таңғы режим", on("light"), level("light", 60), on("ac"), level("ac", 24),
                level("curtains", 100), off("tv"));
    }

    public void ecoMode() {
        macro("Үнемдеу режимі", off("light"), off("tv"), on("ac"), level("ac", 27),
                level("curtains", 0), on("door"));
    }

    public void shutdownEmpty() {
        macro("Бос бөлмені өшіру", off("light"), off("ac"), off("tv"), level("curtains", 0), on("door"));
    }

    public void checkOut() {
        macro("Check-out", off("light"), off("ac"), off("tv"), level("curtains", 0),
                on("door"), status(Room.Status.FREE));
    }

    // ---------- жеке құрылғылар ----------
    public void toggle(String id) {
        run(remotes.get(id).isOn() ? off(id) : on(id));
    }

    public void setPower(String id, boolean value) {
        if (remotes.get(id).isOn() != value) run(value ? on(id) : off(id));
    }

    public void setLevel(String id, int value) {
        Device d = remotes.get(id).getDevice();
        if (d.hasPower() && !d.isOn()) run(on(id));
        run(level(id, value));
    }

    // ---------- Undo / Redo ----------
    public void undo() {
        Command c = room.getInvoker().undo();
        if (c != null) hub.getLog().addCommand(room.getNumber(), "Undo: " + c.getName());
    }

    public void redo() {
        Command c = room.getInvoker().redo();
        if (c != null) hub.getLog().addCommand(room.getNumber(), "Redo: " + c.getName());
    }

    public boolean canUndo() { return room.getInvoker().canUndo(); }
    public boolean canRedo() { return room.getInvoker().canRedo(); }
    public String undoName() { return room.getInvoker().peekUndoName(); }
    public String redoName() { return room.getInvoker().peekRedoName(); }

    public List<Device> devices() { return new ArrayList<>(room.getDevices()); }
}
