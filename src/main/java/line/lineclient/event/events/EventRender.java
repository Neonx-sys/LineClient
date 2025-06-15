package line.lineclient.event.events;

import com.mojang.blaze3d.matrix.MatrixStack;
import line.lineclient.event.Event;
import net.minecraft.client.MainWindow;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.math.vector.Matrix4f;

public class EventRender extends Event {
    public MatrixStack matrixStack;
    private final float partialTicks;
    public MainWindow scaledResolution;
    public Type type;
    public Matrix4f matrix;
    public ActiveRenderInfo activeRenderInfo;

    public EventRender(float partialTicks, MatrixStack stack, MainWindow scaledResolution, Type type,Matrix4f matrix) {
        this.partialTicks = partialTicks;
        this.scaledResolution = scaledResolution;
        this.matrixStack = stack;
        this.type = type;
        this.matrix = matrix;
    }

    public EventRender(MatrixStack matrixStack, float partialTicks) {
        this.partialTicks = partialTicks;
        this.matrixStack = matrixStack;
    }

    public MatrixStack getMatrixStack() {
        return matrixStack;
    }
    public float getPartialTicks() {
        return partialTicks;
    }

    public boolean isRender3D() {
        return this.type == Type.RENDER3D;
    }

    public boolean isRender2D() {
        return this.type == Type.RENDER2D;
    }

    public ActiveRenderInfo getActiveRenderInfo() {
        return this.activeRenderInfo;
    }

    public enum Type {
        RENDER3D, RENDER2D
    }
}