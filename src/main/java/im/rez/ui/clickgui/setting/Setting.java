package im.rez.ui.clickgui.setting;

import java.util.function.Supplier;

public class Setting {
    private String name;
    private Supplier<Boolean> visibility;

    public void setVisibility(Supplier<Boolean> visibility) {
        this.visibility = visibility;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Supplier<Boolean> getVisibility() {
        return visibility;
    }
}
