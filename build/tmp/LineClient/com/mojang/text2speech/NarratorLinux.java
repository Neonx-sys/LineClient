package com.mojang.text2speech;

import com.google.common.collect.Queues;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;

public class NarratorLinux implements Narrator {
    // JNA SPECIFIC CODE
    private static boolean libraryFound = false;
    private static final Logger LOGGER = LogManager.getLogger();

    static {
        try {
            Native.register(FliteLibrary.class, NativeLibrary.getInstance("fliteWrapper"));
            FliteLibrary.init();
            libraryFound = true;
            LOGGER.info("Narrator library successfully loaded");
        } catch (final UnsatisfiedLinkError e) {
            LOGGER.warn("ERROR : Couldn't load Narrator library : " + e.getMessage());
        } catch (final Throwable e) {
            LOGGER.warn("ERROR : Generic error while loading narrator : " + e.getMessage());
        }
    }

    private static class FliteLibrary {
        public static native int init();

        public static native float say(String text);
    }
    // END OF JNA SPECIFIC CODE

    private final NarratorThread narratorThread = new NarratorThread();
    private boolean crashed = false;

    public NarratorLinux() {
        final Thread thread = new Thread(narratorThread);
        thread.setDaemon(true);
        thread.setName("Narrator");
        thread.start();
    }

    private static class NarratorThread implements Runnable {
        protected final Queue<String> msgs = Queues.newConcurrentLinkedQueue();

        @Override
        public void run() {
            while (true) {
                if (msgs.peek() != null) {
                    say(msgs.poll());
                }

                try {
                    Thread.sleep(100);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void add(final String msg) {
            msgs.add(msg);
        }

        public void clear() {
            msgs.clear();
        }

        private void say(final String text) {
            if (libraryFound) {
                FliteLibrary.say(text.replaceAll("[<>]", ""));
            }
        }
    }

    @Override
    public void say(final String msg, final boolean interrupt) {
        if (crashed) {
            return;
        }

        try {
            narratorThread.add(msg);
        } catch (Throwable e) {
            crashed = true;
            LOGGER.error(String.format("Narrator crashed : %s", e));
        }
    }

    @Override
    public void clear() {
        narratorThread.clear();
    }

    @Override
    public boolean active() {
        return libraryFound;
    }

    @Override
    public void destroy() {

    }
}