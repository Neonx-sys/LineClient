package im.rez.ui.clickgui.setting.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import im.rez.ui.clickgui.setting.Setting;

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

    }
}

