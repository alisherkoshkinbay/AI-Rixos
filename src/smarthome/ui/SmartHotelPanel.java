package smarthome.ui;

import smarthome.facade.SmartRoomFacade;
import smarthome.model.Room;
import smarthome.singleton.SmartHomeHub;
import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

// Бүкіл қолданбаның негізгі панелі (терезеден тәуелсіз)
public class SmartHotelPanel extends JPanel {
    private final CardLayout cards = new CardLayout();
    private final JPanel center = new JPanel(cards);
    private final SidebarPanel sidebar;
    private final Map<String, SmartRoomFacade> facades = new LinkedHashMap<>();
    private String currentRoom = "305";

    public SmartHotelPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG);

        SmartHomeHub hub = SmartHomeHub.getInstance();
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(22, 22, 20, 14));

        for (Room r : hub.getRooms()) {
            SmartRoomFacade f = new SmartRoomFacade(r);
            facades.put(r.getNumber(), f);
            center.add(new RoomView(r, f), "room:" + r.getNumber());
        }
        center.add(new AdminOverviewPanel(hub, facades, this::openRoom), "admin");
        center.add(new LogView(hub.getLog()), "log");

        sidebar = new SidebarPanel(hub, this::showView, this::openRoom);
        add(sidebar, BorderLayout.WEST);
        add(center, BorderLayout.CENTER);
        openRoom(currentRoom);
    }

    private void showView(String view) {
        if (view.equals("room")) { openRoom(currentRoom); return; }
        cards.show(center, view);
        sidebar.setSelection(view, null);
    }

    private void openRoom(String number) {
        currentRoom = number;
        cards.show(center, "room:" + number);
        sidebar.setSelection("room", number);
    }

    public void showAdmin() { showView("admin"); }
    public void showLog() { showView("log"); }
    public void showRoom(String n) { openRoom(n); }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setPaint(new GradientPaint(0, 0, new Color(0x10141B), getWidth(), getHeight(), new Color(0x0A0D12)));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.dispose();
    }
}
