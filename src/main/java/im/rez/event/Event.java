package im.rez.event;

public class Event implements Cancel {
    boolean cancel;

    @Override
    public void setCancel(boolean value) {
        this.cancel = cancel;
    }

    @Override
    public boolean isCancel() {
        return cancel;
    }
}
