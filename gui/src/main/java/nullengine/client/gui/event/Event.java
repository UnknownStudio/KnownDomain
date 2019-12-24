package nullengine.client.gui.event;

public abstract class Event implements Cloneable {

    private final EventType<? extends Event> eventType;
    private final EventTarget target;

    private boolean consumed;

    public Event(EventType<? extends Event> eventType, EventTarget target) {
        this.eventType = eventType;
        this.target = target;
    }

    public EventType<? extends Event> getEventType() {
        return eventType;
    }

    public EventTarget getTarget() {
        return target;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public void consume() {
        consumed = true;
    }

    public static Event fireEvent(Event event, EventTarget eventTarget) {
        return EventUtils.fireEvent(event, eventTarget);
    }

    public Event fireEvent(EventTarget eventTargets) {
        return EventUtils.fireEvent(this, eventTargets);
    }

    public Event fireEvent() {
        return EventUtils.fireEvent(this, target);
    }
}
