package im.rez.ui.clickgui.setting.settings;


import im.rez.ui.clickgui.setting.Setting;

public class ValueSetting extends Setting {

    private float value, min, max, increment;
    public boolean dragged = false;

    public ValueSetting(String name, float value, float min, float max, float increment) {
        this.setName(name);
        this.min = min;
        this.max = max;
        this.increment = increment;
        setValue(value);
        this.setVisibility(() -> true);
    }

    public void setDragged(boolean dragged) {
        this.dragged = dragged;
    }

    public void setMax(float max) {
        this.max = max;
        if (value > max) {
            value = max;
        }
    }

    public void setMin(float min) {
        this.min = min;
        if (value < min) {
            value = min;
        }
    }

    public void setValue(float value) {
        if (value < min) {
            this.value = min;
        } else if (value > max) {
            this.value = max;
        } else {
            this.value = value;
        }
    }

    public float getMax() {
        return max;
    }

    public float getMin() {
        return min;
    }

    public float getIncrement() {
        return increment;
    }

    public float getValue() {
        return value;
    }
}