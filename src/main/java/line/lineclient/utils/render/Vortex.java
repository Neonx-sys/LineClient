package line.lineclient.utils.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;
import static line.lineclient.utils.Wrapper.mc;

public class Vortex {

    public static void drawString3D(MatrixStack matrixStack, String text, Vector3d pos, int color, float scale) {
        GL11.glPushMatrix();

        GL11.glTranslated(
                pos.x - mc.getRenderManager().renderPosX,
                pos.y - mc.getRenderManager().renderPosY,
                pos.z - mc.getRenderManager().renderPosZ
        );

        GL11.glRotatef(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-scale * 0.015F, -scale * 0.015F, scale * 0.015F);

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);

        mc.fontRenderer.drawStringWithShadow(
                matrixStack,
                text,
                -mc.fontRenderer.getStringWidth(text) / 2,
                0,
                color
        );

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public static void drawBox(Vector3d pos, Vector3d dimensions, int color) {
        GL11.glPushMatrix();

        GL11.glTranslated(
                pos.x - mc.getRenderManager().renderPosX,
                pos.y - mc.getRenderManager().renderPosY,
                pos.z - mc.getRenderManager().renderPosZ
        );

        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        float alpha = (color >> 24 & 255) / 255.0F;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);

        GL11.glColor4f(red, green, blue, alpha);

        drawBoxSides(dimensions.x, dimensions.y, dimensions.z);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    private static void drawBoxSides(double width, double height, double depth) {
        GL11.glBegin(GL11.GL_QUADS);

        // Front
        GL11.glVertex3d(-width, 0, -depth);
        GL11.glVertex3d(-width, height, -depth);
        GL11.glVertex3d(width, height, -depth);
        GL11.glVertex3d(width, 0, -depth);

        // Back
        GL11.glVertex3d(-width, 0, depth);
        GL11.glVertex3d(width, 0, depth);
        GL11.glVertex3d(width, height, depth);
        GL11.glVertex3d(-width, height, depth);

        // Left
        GL11.glVertex3d(-width, 0, -depth);
        GL11.glVertex3d(-width, 0, depth);
        GL11.glVertex3d(-width, height, depth);
        GL11.glVertex3d(-width, height, -depth);

        // Right
        GL11.glVertex3d(width, 0, -depth);
        GL11.glVertex3d(width, height, -depth);
        GL11.glVertex3d(width, height, depth);
        GL11.glVertex3d(width, 0, depth);

        // Top
        GL11.glVertex3d(-width, height, -depth);
        GL11.glVertex3d(-width, height, depth);
        GL11.glVertex3d(width, height, depth);
        GL11.glVertex3d(width, height, -depth);

        // Bottom
        GL11.glVertex3d(-width, 0, -depth);
        GL11.glVertex3d(width, 0, -depth);
        GL11.glVertex3d(width, 0, depth);
        GL11.glVertex3d(-width, 0, depth);

        GL11.glEnd();
    }
}
