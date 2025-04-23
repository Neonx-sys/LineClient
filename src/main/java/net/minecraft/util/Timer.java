package net.minecraft.util;


public class Timer {
    public float renderPartialTicks;
    public float elapsedPartialTicks;
    public float timerSpeed = 1.0F; // Changed to float with default normal speed
    private long lastSyncSysClock;
    private final float tickLength;

    public Timer(float ticks, long lastSyncSysClock) {
        this.tickLength = 1000.0F / ticks;
        this.lastSyncSysClock = lastSyncSysClock;
    }

    public int getPartialTicks(long gameTime) {
        this.elapsedPartialTicks = (float)(gameTime - this.lastSyncSysClock) / this.tickLength * timerSpeed; // Added timerSpeed multiplication
        this.lastSyncSysClock = gameTime;
        this.renderPartialTicks += this.elapsedPartialTicks;
        int i = (int)this.renderPartialTicks;
        this.renderPartialTicks -= (float)i;
        return i;
    }
}