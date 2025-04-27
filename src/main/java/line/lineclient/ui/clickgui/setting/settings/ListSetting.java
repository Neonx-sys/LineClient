package line.lineclient.ui.clickgui.setting.settings;

import line.lineclient.ui.clickgui.setting.Setting;

import java.util.ArrayList;
import java.util.List;

public class ListSetting extends Setting {

    private ArrayList<CheckBoxSetting> booleanSliders;

    public ListSetting(String name, List<CheckBoxSetting> booleanSliders) {
        this.booleanSliders = new ArrayList<>();
        this.setName(name);
        this.booleanSliders.addAll(booleanSliders);
        this.setVisibility(() -> true);
    }

    public CheckBoxSetting getByNameIgnoreCase(String name) {

        for(CheckBoxSetting slider : this.booleanSliders){
            if(slider.getName().equalsIgnoreCase(name)){
                return slider;
            }
        }

        return null;
    }

    public CheckBoxSetting getByName(String name) {

        for(CheckBoxSetting slider : this.booleanSliders){
            if(slider.getName().equals(name)){
                return slider;
            }
        }

        return null;
    }

    public CheckBoxSetting getSliderById(int id) {
        return this.booleanSliders.get(id);
    }
}
