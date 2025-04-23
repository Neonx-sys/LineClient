package im.rez.event.events;

import im.rez.event.Event;

public class EventRotation extends Event {
    public float yaw, pitch;

    public EventRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    @Override
    public void setCancel(boolean value) {
        super.setCancel(value);
    }
}
