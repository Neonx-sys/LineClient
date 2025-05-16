package com.mojang.text2speech;

import com.google.common.collect.Queues;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.WString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;

public class NarratorWindows implements Narrator {
    // JNA SPECIFIC CODE
    private static boolean libraryFound = false;
    private static final Logger LOGGER = LogManager.getLogger();
    private static long voice;
    private static boolean stopping;

    static {
        String result = "";

        try {
            Native.register(SAPIWrapperSolutionDLL.class, NativeLibrary.getInstance("SAPIWrapper_x64"));
            libraryFound = true;
            LOGGER.info("Narrator library for x64 successfully loaded");
            voice = SAPIWrapperSolutionDLL.init();
            if (voice == 0) {
                result += "ERROR : Couldn't create a voice\n";
            }
        } catch (final UnsatisfiedLinkError e) {
            result += "ERROR : Couldn't load Narrator library : " + e.getMessage() + "\n";
        } catch (final Throwable e) {
            result += "ERROR : Generic error while loading narrator : " + e.getMessage() + "\n";
        }

        if (!libraryFound) {
            try {
                Native.register(SAPIWrapperSolutionDLL.class, NativeLibrary.getInstance("SAPIWrapper_x86"));
                libraryFound = true;
                LOGGER.info("Narrator library for x86 successfully loaded");
                voice = SAPIWrapperSolutionDLL.init();
                if (voice == 0) {
                    result += "ERROR : Couldn't create a voice\n";
                }
            } catch (final UnsatisfiedLinkError e) {
                result += "ERROR : Couldn't load Narrator library : " + e.getMessage() + "\n";
            } catch (final Throwable e) {
                result += "ERROR : Generic error while loading narrator : " + e.getMessage() + "\n";
            }
        }

        if (!libraryFound) {
            LOGGER.warn(result);
        }
    }

    private static class SAPIWrapperSolutionDLL {
        public static native long init();
        public static native void uninit(long voice);
        public static native void queue(long voice, WString msg, boolean clear);
    }
    // END OF JNA SPECIFIC CODE

    private final NarratorThread narratorThread = new NarratorThread();
    private boolean crashed = false;

    public NarratorWindows() {
        final Thread thread = new Thread(narratorThread);
        thread.setName("Narrator");
        thread.start();
    }

    private static class NarratorThread extends Thread {
        protected final Queue<Message> msgs = Queues.newConcurrentLinkedQueue();

        @Override
        public void run() {
            while (!stopping) {
                if (msgs.peek() != null) {
                    msgs.poll().apply();
                }

                try {
                    Thread.sleep(100);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void add(final Message msg) {
            msgs.add(msg);
        }

        public void clear() {
            msgs.clear();
        }
    }

    private class Message {
        final String text;
        final boolean interrupt;

        private Message(final String text, final boolean interrupt) {
            this.text = text;
            this.interrupt = interrupt;
        }

        public void apply() {
            SAPIWrapperSolutionDLL.queue(voice, new WString(text.replaceAll("[<>]", "")), interrupt);
        }
    }

    @Override
    public void say(final String msg, final boolean interrupt) {
        if (crashed) {
            return;
        }

        try {
            narratorThread.add(new Message(msg, interrupt));
        } catch (Throwable e) {
            crashed = true;
            LOGGER.error(String.format("Narrator crashed : %s", e));
        }
    }

    @Override
    public void clear() {
        narratorThread.clear();
        narratorThread.add(new Message("", true));
    }

    @Override
    public boolean active() {
        return libraryFound;
    }

    @Override
    public void destroy() {
        stopping = true;
        try {
            narratorThread.join();
        } catch (final InterruptedException ignoed) {
        }
        SAPIWrapperSolutionDLL.uninit(voice);
    }
}