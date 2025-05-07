package line.lineclient.ui.clickgui.setting.settings;

import line.lineclient.ui.clickgui.setting.Setting;

public class ValueSetting extends Setting {
    private float value;
    private float minimum;
    private float maximum;
    private float increment;
    private boolean dragging;

    public ValueSetting(String name, float value, float minimum, float maximum, float increment) {
        this.setName(name);
        this.value = value;
        this.minimum = minimum;
        this.maximum = maximum;
        this.increment = increment;
        this.setVisibility(() -> true);
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        value = Math.round(Math.max(minimum, Math.min(maximum, value)) * (1.0f / increment)) / (1.0f / increment);
        this.value = value;
    }

    public float getMinimum() {
        return minimum;
    }

    public void setMinimum(float minimum) {
        this.minimum = minimum;
    }

    public float getMaximum() {
        return maximum;
    }

    public void setMaximum(float maximum) {
        this.maximum = maximum;
    }

    public float getIncrement() {
        return increment;
    }

    public void setIncrement(float increment) {
        this.increment = increment;
    }

    public boolean isDragging() {
        return dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    // Додаємо метод для отримання відсотків
    public float getPercentage() {
        return ((value - minimum) / (maximum - minimum));
    }
}