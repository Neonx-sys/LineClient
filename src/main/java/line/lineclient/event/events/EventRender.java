package line.lineclient.event.events;

import com.mojang.blaze3d.matrix.MatrixStack;
import line.lineclient.event.Event;
import net.minecraft.client.MainWindow;
import net.minecraft.util.math.vector.Matrix4f;

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