package smarthome.ui;

import smarthome.bridge.Device;
import smarthome.facade.SmartRoomFacade;
import javax.swing.*;
import java.awt.*;

// Құрылғы картасы: белгіше, атауы, күйі, ауыстырғыш, слайдер
public class DeviceCard extends RoundedPanel {
    private final Device device;
    private final JLabel name = new JLabel(), status = new JLabel();
    private final ToggleSwitch toggle;
    private final FlatSlider slider;
    private final Color accent;
    private boolean active;

    public DeviceCard(Device d, SmartRoomFacade facade) {
        super(18, Theme.CARD, Theme.BORDER);
        this.device = d;
        this.accent = accentFor(d.getId());
        setLayout(null);

        name.setText(d.getName());
        name.setFont(Theme.font(Font.BOLD, 14));
        name.setForeground(Theme.TEXT);
        status.setFont(Theme.font(Font.PLAIN, 12));
        status.setForeground(Theme.MUTED);
        add(name);
        add(status);

        if (d.hasPower()) {
            toggle = new ToggleSwitch(accent, () -> facade.toggle(d.getId()));
            add(toggle);
        } else {
            toggle = null;
        }
        if (d.hasLevel()) {
            slider = new FlatSlider(d.getMin(), d.getMax(), d.getLevel(), accent,
                    v -> facade.setLevel(d.getId(), v));
            add(slider);
        } else {
            slider = null;
        }
        setPreferredSize(new Dimension(300, d.hasLevel() ? 90 : 62));
        refresh();
    }

    public int cardHeight() { return device.hasLevel() ? 90 : 62; }

    private static Color accentFor(String id) {
        switch (id) {
            case "light": return Theme.AMBER;
            case "ac": return Theme.BLUE;
            case "curtains": return Theme.GOLD;
            case "tv": return Theme.PURPLE;
            default: return Theme.GREEN;
        }
    }

    @Override
    public void doLayout() {
        int w = getWidth();
        name.setBounds(68, 12, Math.max(10, w - 68 - 70), 20);
        status.setBounds(68, 32, Math.max(10, w - 68 - 70), 18);
        if (toggle != null) toggle.setBounds(w - 14 - 46, 18, 46, 26);
        if (slider != null) slider.setBounds(14, 58, w - 28, 24);
    }

    public void refresh() {
        active = device.hasPower() ? device.isOn() : device.getLevel() > 0;
        status.setText(device.getStatusText());
        if (toggle != null) toggle.setSelected(device.isOn());
        if (slider != null) slider.setValue(device.getLevel());
        setBorderColor(active ? Theme.alpha(accent, 90) : Theme.BORDER);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        Theme.quality(g);
        Color c = active ? accent : Theme.MUTED;
        g.setColor(Theme.alpha(c, active ? 42 : 26));
        g.fillRoundRect(14, 11, 40, 40, 14, 14);
        String ic = device.getId().equals("door") ? (device.isOn() ? "lock" : "unlock") : device.getId();
        Icons.draw(g, ic, 34, 31, 24, c);
        g.dispose();
    }
}
