package im.rez.ui.ab;

import lombok.Getter;
import lombok.Setter;

public class AutoBuyConfig {
    @Getter @Setter private String itemName;
    @Getter @Setter private int maxPrice;
    @Getter @Setter private boolean enabled;
    @Getter @Setter private boolean exactMatch;

    public AutoBuyConfig(String itemName, int maxPrice, boolean enabled, boolean exactMatch) {
        this.itemName = itemName;
        this.maxPrice = maxPrice;
        this.enabled = enabled;
        this.exactMatch = exactMatch;
    }
}