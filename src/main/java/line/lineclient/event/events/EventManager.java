package line.lineclient.event.events;

import line.lineclient.event.Event;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private static final List<EventListener> listeners = new ArrayList<>();

    public static void register(EventListener listener) {
        listeners.add(listener);
    }

    public static void call(Event event) {
        for (EventListener listener : listeners) {
            listener.onEvent(event);
        }
    }
}
