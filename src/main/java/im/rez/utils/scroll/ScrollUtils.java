package im.rez.utils.scroll;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11C;

import static org.lwjgl.opengl.GL11C.glBindTexture;

public class ScrollUtils {
    public static void setupRender() {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.shadeModel(7425);
        RenderSystem.depthMask(false);
    }
    public static void bindTexture(int texture) {
        glBindTexture(GL11C.GL_TEXTURE_2D, texture);
    }
    public static void StartScissor(float x, float y, float width, float height) {
        Minecraft mc = Minecraft.getInstance();
        double scale = mc.getMainWindow().getGuiScaleFactor();
        double finalHeight = (double)height * scale;
        double finalY = (double)((float)mc.getMainWindow().getScaledHeight() - y) * scale;
        double finalX = (double)x * scale;
        double finalWidth = (double)width * scale;
        RenderSystem.enableScissor((int)((int)finalX), (int)((int)(finalY - finalHeight)), (int)((int)finalWidth), (int)((int)finalHeight));
    }

    public static void stopScissor() {
        RenderSystem.disableScissor();
    }
    public static void endRender() {
        RenderSystem.depthMask( true);
        RenderSystem.shadeModel((int) 7424);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
    }
}
