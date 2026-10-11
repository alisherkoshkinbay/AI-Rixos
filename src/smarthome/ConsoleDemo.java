package smarthome;

import smarthome.facade.SmartRoomFacade;
import smarthome.model.Room;
import smarthome.observer.ConsoleLogger;
import smarthome.singleton.SmartHomeHub;

// Консольдік демо (терезесіз)
public class ConsoleDemo {
    public static void main(String[] args) {
        Room room = SmartHomeHub.getInstance().getRoom("101");
        room.addDeviceListener(new ConsoleLogger());   // Observer
        SmartRoomFacade facade = new SmartRoomFacade(room);

        System.out.println(">> Қош келдіңіз");
        facade.welcome();
        print(room);

        System.out.println(">> Кино режимі");
        facade.movieMode();
        print(room);

        System.out.println(">> Undo");
        facade.undo();
        print(room);

        System.out.println(">> Check-out");
        facade.checkOut();
        print(room);

        System.out.println("Singleton бір объект па? "
                + (SmartHomeHub.getInstance() == SmartHomeHub.getInstance()));
    }

    private static void print(Room room) {
        System.out.println("--- Бөлме " + room.getNumber() + " (" + room.getStatus().label + ") ---");
        room.getDevices().forEach(d -> System.out.println("   " + d.getName() + ": " + d.getStatusText()));
        System.out.printf("   Қуат: %.0f Вт%n", room.totalWatts());
    }
}
