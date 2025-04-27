package line.lineclient.event.events;

import com.mojang.blaze3d.matrix.MatrixStack;
import line.lineclient.event.Event;

public class EventRender extends Event {
    public MatrixStack matrixStack;
    private final float partialTicks;
    public EventRender(MatrixStack matrixStack, float partialTicks){
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
