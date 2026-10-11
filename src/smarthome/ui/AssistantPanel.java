package smarthome.ui;

import smarthome.facade.VoiceAssistant;
import javax.swing.*;
import java.awt.*;

// Мәтіндік көмекші панелі
public class AssistantPanel extends RoundedPanel {
    private final JTextField field;
    private final JTextArea reply = new JTextArea();
    private JLabel titleLabel;
    private RoundedPanel box;
    private PillButton goBtn;

    public AssistantPanel(VoiceAssistant assistant, Runnable afterAction) {
        super(18, Theme.CARD, Theme.BORDER);
        setLayout(null);
        setPreferredSize(new Dimension(300, 150));

        titleLabel = Theme.label("КӨМЕКШІ", Font.BOLD, 11, Theme.MUTED);
        add(titleLabel);

        field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    Theme.quality(g2);
                    g2.setColor(Theme.alpha(Theme.MUTED, 170));
                    g2.setFont(getFont());
                    g2.drawString("кино режимі, жарық 50, перде аш...", 12, (getHeight() + g2.getFontMetrics().getAscent() - g2.getFontMetrics().getDescent()) / 2);
                    g2.dispose();
                }
            }
        };
        field.setFont(Theme.font(Font.PLAIN, 13));
        field.setForeground(Theme.TEXT);
        field.setCaretColor(Theme.GOLD);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 8));

        box = new RoundedPanel(20, Theme.CARD_HI, Theme.BORDER);
        box.setLayout(new BorderLayout());
        box.add(field, BorderLayout.CENTER);
        add(box);

        Runnable send = () -> {
            reply.setText(assistant.execute(field.getText()));
            field.setText("");
            if (afterAction != null) afterAction.run();
        };
        goBtn = new PillButton("Орындау", Theme.GOLD, true, send);
        add(goBtn);
        field.addActionListener(e -> send.run());

        reply.setEditable(false);
        reply.setOpaque(false);
        reply.setLineWrap(true);
        reply.setWrapStyleWord(true);
        reply.setFont(Theme.font(Font.PLAIN, 12));
        reply.setForeground(Theme.TEAL);
        reply.setText("Мысалы: «кондиционер 22», «теледидар өшір», «түнгі режим», «undo».");
        reply.setForeground(Theme.MUTED);
        add(reply);
    }

    @Override
    public void doLayout() {
        int w = getWidth();
        titleLabel.setBounds(16, 10, 200, 20);
        goBtn.setBounds(w - 14 - 78, 34, 78, 36);
        box.setBounds(14, 34, w - 14 - 78 - 14 - 8, 36);
        reply.setBounds(16, 80, w - 32, 60);
    }
}
