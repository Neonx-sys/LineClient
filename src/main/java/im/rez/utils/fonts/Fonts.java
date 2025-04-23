package im.rez.utils.fonts;

import lombok.SneakyThrows;
import im.rez.utils.fonts.common.Lang;
import im.rez.utils.fonts.styled.StyledFont;

public class Fonts {
    public static final String FONT_DIR = "/Yougame/fonts/";

    public static volatile StyledFont[] minecraft = new StyledFont[24];
    public static volatile StyledFont[] gilroyBold = new StyledFont[51];
    public static volatile StyledFont[] gilroy = new StyledFont[51];
    public static volatile StyledFont[] icons = new StyledFont[210];

    @SneakyThrows
    public static void init() {
        long time = System.currentTimeMillis();

        minecraft[8] = new StyledFont("mc.ttf", 8, 0.0f, 0.0f, 0.0f, false, Lang.ENG_RU);
        for (int i = 8; i < 51;i++) {
            Fonts.gilroyBold[i] = new StyledFont("gilroy-bold.ttf", i, 0.0f, 0.0f, 0.0f, true, Lang.ENG_RU);
        }
        for (int i = 8; i < 51;i++) {
            gilroy[i] = new StyledFont("gilroy.ttf", i, 0.0f, 0.0f, 0.0f, true, Lang.ENG_RU);
        }
        for (int i = 8; i < 210;i++) {
            icons[i] = new StyledFont("icons.ttf", i, 0.0f, 0.0f, 0.0f, true, Lang.ENG_RU);
        }
    }
}