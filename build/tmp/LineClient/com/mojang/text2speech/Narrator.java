package com.mojang.text2speech;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;

public interface Narrator {
    void say(final String msg, final boolean interrupt);

    void clear();

    boolean active();

    void destroy();

    static Narrator getNarrator() {
        final Logger LOGGER = LogManager.getLogger();

        try {
            final String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
            if (osName.contains("linux")) {
                setJNAPath(":");
                return new NarratorLinux();
            } else if (osName.contains("win")) {
                setJNAPath(";");
                return new NarratorWindows();
            } else if (osName.contains("mac")) {
                setJNAPath(":");
                return new NarratorOSX();
            } else {
                return new NarratorDummy();
            }
        } catch (Throwable e) {
            LOGGER.error(String.format("Error while loading the narrator : %s", e));
            return new NarratorDummy();
        }
    }

    static void setJNAPath(String sep) {
        System.setProperty("jna.library.path", System.getProperty("jna.library.path") + sep + "./src/natives/resources/");
        System.setProperty("jna.library.path", System.getProperty("jna.library.path") + sep + System.getProperty("java.library.path"));
    }
}
