package com.mojang.text2speech;

import ca.weblite.objc.Client;
import ca.weblite.objc.NSObject;
import ca.weblite.objc.Proxy;
import ca.weblite.objc.annotations.Msg;
import com.google.common.collect.Queues;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;

public class NarratorOSX extends NSObject implements Narrator {
    private static final Logger LOGGER = LogManager.getLogger();

    private final Proxy synth = Client.getInstance().sendProxy("NSSpeechSynthesizer", "alloc");
    private boolean speaking;
    private boolean crashed;

    private final Queue<String> queue = Queues.newConcurrentLinkedQueue();

    public NarratorOSX() {
        super("NSObject");
        synth.send("init");
        synth.send("setDelegate:", this);
    }

    private void startSpeaking(final String message) {
        synth.send("startSpeakingString:", message);
    }

    @Msg(selector = "speechSynthesizer:didFinishSpeaking:", signature = "v@:B")
    public void didFinishSpeaking(final boolean naturally) {
        if (queue.isEmpty()) {
            speaking = false;
        } else {
            startSpeaking(queue.poll());
        }
    }

    @Override
    public void say(final String msg, final boolean interrupt) {
        if (crashed) {
            return;
        }

        try {
            if (interrupt) {
                synth.send("stopSpeaking");
            }
            if (speaking) {
                queue.offer(msg);
            } else {
                speaking = true;
                startSpeaking(msg);
            }
        } catch (Throwable e) {
            crashed = true;
            LOGGER.error(String.format("Narrator crashed : %s", e));
        }
    }

    @Override
    public void clear() {
        queue.clear();
        synth.send("stopSpeaking");
    }

    @Override
    public boolean active() {
        return true;
    }

    @Override
    public void destroy() {

    }
}
