package smarthome.facade;

import smarthome.bridge.Device;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Мәтіндік көмекші: "жарықты 50 қой", "кондиционер өшір", "кино режимі" сияқты командаларды түсінеді.
// Facade-тің клиенті ретінде жұмыс істейді
public class VoiceAssistant {
    private final SmartRoomFacade f;

    public VoiceAssistant(SmartRoomFacade facade) { this.f = facade; }

    public String execute(String raw) {
        String t = raw == null ? "" : raw.toLowerCase(Locale.ROOT).trim();
        if (t.isEmpty()) return "Команда жазыңыз, мысалы: «жарықты 50 қой».";

        if (has(t, "undo", "болдырма")) { f.undo(); return "Соңғы әрекет болдырылмады (Undo)."; }
        if (has(t, "redo", "қайта орында")) { f.redo(); return "Әрекет қайта орындалды (Redo)."; }
        if (has(t, "қош кел", "welcome")) { f.welcome(); return "«Қош келдіңіз» сценарийі іске қосылды."; }
        if (has(t, "түнгі", "night", "ұйқы")) { f.nightMode(); return "Түнгі режим қосылды."; }
        if (has(t, "кино", "movie", "фильм")) { f.movieMode(); return "Кино режимі қосылды. Жағымды көріңіз!"; }
        if (has(t, "таңғы", "morning", "ояту", "таң")) { f.morningMode(); return "Таңғы режим қосылды."; }
        if (has(t, "үнемде", "eco")) { f.ecoMode(); return "Үнемдеу режимі қосылды."; }
        if (has(t, "check-out", "checkout", "check out", "шығу")) { f.checkOut(); return "Check-out орындалды."; }

        String id = findDevice(t);
        if (id == null) return "Түсінбедім. Мысалы: «кондиционер 22», «перде аш», «теледидар өшір».";

        Integer num = findNumber(t);
        Device d = f.getRoom().getDevice(id);

        if (id.equals("door")) {
            if (has(t, "unlock", "аш")) { f.setPower("door", false); return "Есік құлпы ашылды."; }
            if (has(t, "lock", "құлыпта", "жап", "close")) { f.setPower("door", true); return "Есік құлыпталды."; }
            return "Есік үшін «құлыптa» немесе «аш» деп жазыңыз.";
        }
        if (id.equals("curtains")) {
            if (num != null) { f.setLevel(id, num); return "Перде " + d.getLevel() + "% ашылды."; }
            if (has(t, "аш", "open")) { f.setLevel(id, 100); return "Перде ашылды."; }
            if (has(t, "жап", "close")) { f.setLevel(id, 0); return "Перде жабылды."; }
            return "Перде үшін «аш», «жап» немесе пайыз жазыңыз.";
        }
        if (num != null) {
            f.setLevel(id, num);
            return d.getName() + ": " + d.getLevel() + d.getUnit() + " орнатылды.";
        }
        if (has(t, "өшір", "off", "өш")) { f.setPower(id, false); return d.getName() + " өшірілді."; }
        if (has(t, "қос", "on", "іске")) { f.setPower(id, true); return d.getName() + " қосылды."; }
        return d.getName() + " үшін «қос», «өшір» немесе сан жазыңыз.";
    }

    private String findDevice(String t) {
        if (has(t, "жарық", "шам", "light", "lamp")) return "light";
        if (has(t, "кондиционер", "салқын", "температура", "ac")) return "ac";
        if (has(t, "перде", "штор", "curtain")) return "curtains";
        if (has(t, "теледидар", "тв", "tv", "television")) return "tv";
        if (has(t, "есік", "құлып", "door", "lock", "unlock")) return "door";
        return null;
    }

    private Integer findNumber(String t) {
        Matcher m = Pattern.compile("(\\d{1,3})").matcher(t);
        return m.find() ? Integer.valueOf(m.group(1)) : null;
    }

    // латын сөздер бүтін сөз ретінде, қазақша сөздер бөлігі бойынша табылады
    private boolean has(String t, String... keys) {
        for (String k : keys) {
            if (k.chars().allMatch(c -> c < 128 && (Character.isLetter(c) || c == ' ' || c == '-'))) {
                if (Pattern.compile("\\b" + Pattern.quote(k) + "\\b", Pattern.UNICODE_CHARACTER_CLASS)
                        .matcher(t).find()) return true;
            } else if (t.contains(k)) {
                return true;
            }
        }
        return false;
    }
}
