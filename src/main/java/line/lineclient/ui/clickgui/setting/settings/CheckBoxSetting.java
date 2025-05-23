package line.lineclient.ui.clickgui.setting.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import line.lineclient.ui.clickgui.setting.Setting;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
public class CheckBoxSetting extends Setting {
    private boolean value;

    public CheckBoxSetting(String name, boolean value) {
        this.setName(name);
        this.value = value;
        this.setVisibility(() -> true);
    }

    public boolean isValue() {
        return value;
    }

    public boolean get() {
        return this.value;
    }

    public void setValue(boolean b) {
        this.value = b;
    }

    public boolean isEnabled() {
        return this.value;
    }

    public void setEnabled(boolean enabled) {
        this.value = enabled;
    }
}