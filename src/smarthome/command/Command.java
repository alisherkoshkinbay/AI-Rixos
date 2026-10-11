package smarthome.command;

// COMMAND: әрекет объект ретінде (орындау, болдырмау, атауы бар)
public interface Command {
    void execute();
    void undo();
    String getName();

    // Слайдерді сүйреген кезде көп команда жиналып қалмас үшін біріктіру
    default boolean mergeWith(Command other) { return false; }
}
