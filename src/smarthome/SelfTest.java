package smarthome;

import smarthome.facade.SmartRoomFacade;
import smarthome.facade.VoiceAssistant;
import smarthome.model.Room;
import smarthome.singleton.SmartHomeHub;

// Қарапайым автотест (JUnit керек емес): java -cp out smarthome.SelfTest
public class SelfTest {
    private static int failed = 0;

    private static void check(String name, boolean ok) {
        System.out.println((ok ? "OK    " : "FAIL  ") + name);
        if (!ok) failed++;
    }

    public static void main(String[] args) {
        SmartHomeHub hub = SmartHomeHub.getInstance();
        check("Singleton: бір ғана объект", hub == SmartHomeHub.getInstance());

        Room room = hub.getRoom("101");
        SmartRoomFacade f = new SmartRoomFacade(room);
        int[] events = {0};
        room.addDeviceListener((d, e) -> events[0]++);              // Observer

        f.welcome();
        check("Facade: welcome жарықты қосты", room.getDevice("light").isOn());
        check("Facade: welcome температураны 23 қойды", room.getDevice("ac").getLevel() == 23);
        check("Facade: бөлме 'Қонақ бар' болды", room.getStatus() == Room.Status.OCCUPIED);
        check("Observer: оқиғалар келді", events[0] > 0);

        f.movieMode();
        check("Command: кино режимінде теледидар қосулы", room.getDevice("tv").isOn());
        f.undo();
        check("Command: Undo теледидарды өшірді", !room.getDevice("tv").isOn());
        check("Command: Undo жарықты 80%-ға қайтарды", room.getDevice("light").getLevel() == 80);
        f.redo();
        check("Command: Redo кино режимін қайтарды", room.getDevice("tv").isOn());

        f.setLevel("light", 40);
        f.setLevel("light", 50);
        f.setLevel("light", 60);
        f.undo();
        check("Command: слайдер командалары біріктірілді (бір Undo)", room.getDevice("light").getLevel() == 15);

        VoiceAssistant va = new VoiceAssistant(f);
        va.execute("кондиционер 20");
        check("Assistant: 'кондиционер 20'", room.getDevice("ac").getLevel() == 20);
        va.execute("теледидарды өшір");
        check("Assistant: 'теледидарды өшір'", !room.getDevice("tv").isOn());
        va.execute("есікті аш");
        check("Assistant: 'есікті аш'", !room.getDevice("door").isOn());
        va.execute("перде 70");
        check("Assistant: 'перде 70'", room.getDevice("curtains").getLevel() == 70);

        f.checkOut();
        check("Check-out: қуат 0 Вт", room.totalWatts() == 0);
        check("Check-out: бөлме бос", room.getStatus() == Room.Status.FREE);
        check("Check-out: есік құлыптаулы", room.getDevice("door").isOn());

        System.out.println(failed == 0 ? "\nБарлық тест өтті" : "\nҚате: " + failed);
        System.exit(failed == 0 ? 0 : 1);
    }
}
