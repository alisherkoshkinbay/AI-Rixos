package smarthome.bridge;

import smarthome.observer.DeviceListener;

// BRIDGE: implementor интерфейсі. Барлық ақылды құрылғы осы келісімді орындайды
public interface Device {
    String getId();
    String getName();
    void on();
    void off();
    boolean isOn();
    void setLevel(int level);
    int getLevel();
    int getMin();
    int getMax();
    String getUnit();
    boolean hasPower();      // қосу/өшіру ауыстырғышы бар ма
    boolean hasLevel();      // слайдері бар ма (жарықтылық, температура...)
    double getPowerWatts();  // қазіргі тұтынатын қуат
    String getStatusText();
    String getOnLabel();
    String getOffLabel();
    void addListener(DeviceListener l);
}
