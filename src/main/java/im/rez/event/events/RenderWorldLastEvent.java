package im.rez.event.events;

import com.mojang.blaze3d.matrix.MatrixStack;
import im.rez.event.Event;

public class RenderWorldLastEvent extends Event {
    private final MatrixStack matrixStack;
    private final float partialTicks;

    public RenderWorldLastEvent(MatrixStack matrixStack, float partialTicks) {
        this.matrixStack = matrixStack;
        this.partialTicks = partialTicks;
    }

    public MatrixStack getMatrixStack() {
        return matrixStack;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
