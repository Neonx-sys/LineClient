package line.lineclient.utils.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;

import static line.lineclient.utils.Wrapper.mc;

public class WorldRenderUtils {

    public static void translateCamera(MatrixStack matrixStack, Vector3d pos) {
        double camX = mc.gameRenderer.getActiveRenderInfo().getProjectedView().x;
        double camY = mc.gameRenderer.getActiveRenderInfo().getProjectedView().y;
        double camZ = mc.gameRenderer.getActiveRenderInfo().getProjectedView().z;

        GL11.glTranslated(
                pos.x - camX,
                pos.y - camY,
                pos.z - camZ
        );
    }

    public static void drawRect(float x1, float y1, float x2, float y2) {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x2, y1);
        GL11.glVertex2f(x2, y2);
        GL11.glVertex2f(x1, y2);
        GL11.glEnd();
    }

    public static void drawBox(double x, double y, double z, double width, double height, int color) {
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        GL11.glColor4f(red, green, blue, alpha);
        GL11.glBegin(GL11.GL_QUADS);

        GL11.glVertex3d(x - width, y, z - width);
        GL11.glVertex3d(x + width, y, z - width);
        GL11.glVertex3d(x + width, y + height, z - width);
        GL11.glVertex3d(x - width, y + height, z - width);

        GL11.glVertex3d(x - width, y, z + width);
        GL11.glVertex3d(x + width, y, z + width);
        GL11.glVertex3d(x + width, y + height, z + width);
        GL11.glVertex3d(x - width, y + height, z + width);

        GL11.glVertex3d(x - width, y, z - width);
        GL11.glVertex3d(x - width, y, z + width);
        GL11.glVertex3d(x - width, y + height, z + width);
        GL11.glVertex3d(x - width, y + height, z - width);

        GL11.glVertex3d(x + width, y, z - width);
        GL11.glVertex3d(x + width, y, z + width);
        GL11.glVertex3d(x + width, y + height, z + width);
        GL11.glVertex3d(x + width, y + height, z - width);

        GL11.glEnd();
    }

    public static Vector3d getRenderPosition(Vector3d pos) {
        double renderX = pos.x - mc.getRenderManager().renderPosX;
        double renderY = pos.y - mc.getRenderManager().renderPosY;
        double renderZ = pos.z - mc.getRenderManager().renderPosZ;
        return new Vector3d(renderX, renderY, renderZ);
    }
}
