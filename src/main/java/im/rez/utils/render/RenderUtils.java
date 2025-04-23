package im.rez.utils.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.optifine.util.TextureUtils;
import im.rez.utils.color.ColorUtils;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_COLOR_TEX;
import static im.rez.utils.Wrapper.mc;
import static im.rez.utils.render.RenderUtils.IntColor.rgb;
import static org.lwjgl.opengl.GL11.*;
import im.rez.utils.Shader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Objects;
import com.jhlabs.image.GaussianFilter;
import org.lwjgl.opengl.GL11;

import static com.mojang.blaze3d.platform.GlStateManager.*;
import static org.lwjgl.opengl.GL11.GL_QUADS;

public class RenderUtils {
    private static final Shader ROUNDED = new Shader("rounded.frag");
    private static final HashMap<Integer, Integer> shadowCache = new HashMap<>();
    // Мусор
    static BufferBuilder buffer = Tessellator.getInstance().getBuffer();
    static Tessellator tessellator = Tessellator.getInstance();
    public static void resetColor() {
        glColor4f(1f, 1f, 1f, 1f);
    }
    public static void drawSetup() {
        disableTexture();
        enableBlend();
        blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }
    public static void drawFinish() {
        enableTexture();
        disableBlend();
        resetColor();
    }

    public static float animate(float target, float current, float speed) {
        float diff = target - current;
        if (diff > speed) {
            return current + speed;
        } else if (diff < -speed) {
            return current - speed;
        }
        return target;
    }

    public static void drawImage(MatrixStack stack, double x, double y, double z, double width, double height, int color1, int color2, int color3, int color4) {
        boolean blend = glIsEnabled(GL_BLEND);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE);
        glShadeModel(GL_SMOOTH);
        glAlphaFunc(GL_GREATER, 0);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        buffer.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP);
        buffer.pos(stack.getLast().getMatrix(), (float) x, (float) (y + height), (float) (z)).color((color1 >> 16) & 0xFF, (color1 >> 8) & 0xFF, color1 & 0xFF, color1 >>> 24).tex(0, 1 - 0.01f).lightmap(0, 240).endVertex();
        buffer.pos(stack.getLast().getMatrix(), (float) (x + width), (float) (y + height), (float) (z)).color((color2 >> 16) & 0xFF, (color2 >> 8) & 0xFF, color2 & 0xFF, color2 >>> 24).tex(1, 1 - 0.01f).lightmap(0, 240).endVertex();
        buffer.pos(stack.getLast().getMatrix(), (float) (x + width), (float) y, (float) z).color((color3 >> 16) & 0xFF, (color3 >> 8) & 0xFF, color3 & 0xFF, color3 >>> 24).tex(1, 0).lightmap(0, 240).endVertex();
        buffer.pos(stack.getLast().getMatrix(), (float) x, (float) y, (float) z).color((color4 >> 16) & 0xFF, (color4 >> 8) & 0xFF, color4 & 0xFF, color4 >>> 24).tex(0, 0).lightmap(0, 240).endVertex();
        tessellator.draw();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        glShadeModel(GL_FLAT);
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ZERO);
        if (!blend)
            GlStateManager.disableBlend();
    }

    public static void drawRoundedRect(double x, double y, double width, double height, double radius, Color color) {
        float[] c = ColorUtils.getColorComps(color);
        drawSetup();
        ROUNDED.load();
        ROUNDED.setUniformf("size", (float)width * 2, (float)height * 2);
        ROUNDED.setUniformf("round", (float)radius * 2);
        ROUNDED.setUniformf("color", c[0], c[1], c[2], c[3]);
        Shader.draw(x, y, width, height);
        ROUNDED.unload();
        drawFinish();
    }

    public static void drawOutline(double x, double y, double width, double height, float lineWidth, Color color) {
        drawSetup();

        glLineWidth(lineWidth);

        float[] c = ColorUtils.getColorComps(color);
        glColor4f(c[0], c[1], c[2], c[3]);

        buffer.begin(GL_LINE_LOOP, DefaultVertexFormats.POSITION);
        buffer.pos(x, y, 0).endVertex();
        buffer.pos(x + width, y, 0).endVertex();
        buffer.pos(x + width, y + height, 0).endVertex();
        buffer.pos(x, y + height, 0).endVertex();
        tessellator.draw();

        glLineWidth(1.0f);
        drawFinish();
    }

    public static void drawOutlineGradient(double x, double y, double width, double height, float lineWidth, Color color) {
        drawSetup();

        glLineWidth(lineWidth);

        buffer.begin(GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);

        float[] c1 = ColorUtils.getColorComps(color);


        buffer.pos(x, y, 0).color(c1[0], c1[1], c1[2], c1[3]).endVertex();
        buffer.pos(x + width, y, 0).color(c1[0], c1[1], c1[2], c1[3]).endVertex();
        buffer.pos(x + width, y + height, 0).color(c1[0], c1[1], c1[2], c1[3]).endVertex();
        buffer.pos(x, y + height, 0).color(c1[0], c1[1], c1[2], c1[3]).endVertex();

        tessellator.draw();

        glLineWidth(1.0f);
        drawFinish();
    }
    public static void drawShadow(float x, float y, float width, float height, int radius, int color) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(GL_GREATER, 0.01f);
        GlStateManager.disableAlphaTest();

        x -= radius;
        y -= radius;
        width = width + radius * 2;
        height = height + radius * 2;
        x -= 0.25f;
        y += 0.25f;

        int identifier = Objects.hash(width, height, radius);
        int textureId;

        if (shadowCache.containsKey(identifier)) {
            textureId = shadowCache.get(identifier);
            GlStateManager.bindTexture(textureId);
        } else {
            if (width <= 0) {
                width = 1;
            }

            if (height <= 0) {
                height = 1;
            }

            BufferedImage originalImage = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB_PRE);
            Graphics2D graphics = originalImage.createGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(radius, radius, (int) (width - radius * 2), (int) (height - radius * 2));
            graphics.dispose();

            GaussianFilter filter = new GaussianFilter(radius);
            BufferedImage blurredImage = filter.filter(originalImage, null);
            DynamicTexture texture = new DynamicTexture(TextureUtils.toNativeImage(blurredImage));
            texture.setBlurMipmap(true, true);
            try {
                textureId = texture.getGlTextureId();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            shadowCache.put(identifier, textureId);
        }

        float[] startColorComponents = rgb(color);

        buffer.begin(GlStateManager.GL_QUADS, POSITION_COLOR_TEX);
        buffer.pos(x, y, 0.0f)
                .color(startColorComponents[0], startColorComponents[1], startColorComponents[2], startColorComponents[3])
                .tex(0.0f, 0.0f)
                .endVertex();

        buffer.pos(x, y + (float) ((int) height), 0.0f)
                .color(startColorComponents[0], startColorComponents[1], startColorComponents[2], startColorComponents[3])
                .tex(0.0f, 1.0f)
                .endVertex();

        buffer.pos(x + (float) ((int) width), y + (float) ((int) height), 0.0f)
                .color(startColorComponents[0], startColorComponents[1], startColorComponents[2], startColorComponents[3])
                .tex(1.0f, 1.0f)
                .endVertex();

        buffer.pos(x + (float) ((int) width), y, 0.0f)
                .color(startColorComponents[0], startColorComponents[1], startColorComponents[2], startColorComponents[3])
                .tex(1.0f, 0.0f)
                .endVertex();

        tessellator.draw();
        GlStateManager.enableAlphaTest();
        GlStateManager.bindTexture(0);
        GlStateManager.disableBlend();
    }

    public static void drawCircle(float x, float y, float radius, Color color) {
        int segments = 50;
        float theta = (float) (2 * Math.PI / segments);
        float tangetial_factor = (float) Math.tan(theta);
        float radial_factor = (float) Math.cos(theta);

        float xx = radius;
        float yy = 0;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        GL11.glBegin(GL11.GL_POLYGON);
        for(int i = 0; i < segments; i++) {
            GL11.glVertex2f(x + xx, y + yy);

            float tx = -yy;
            float ty = xx;

            xx += tx * tangetial_factor;
            yy += ty * tangetial_factor;

            xx *= radial_factor;
            yy *= radial_factor;
        }
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void drawEntityESP(double x, double y, double z, double width, double height, float red, float green, float blue, float alpha) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glRotatef(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glColor4f(red, green, blue, alpha);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(-width, 0, 0.0D);
        GL11.glVertex3d(-width, height, 0.0D);
        GL11.glVertex3d(width, height, 0.0D);
        GL11.glVertex3d(width, 0, 0.0D);
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }



    public static class IntColor {
        public static float[] rgb(final int color) {
            return new float[]{
                    (color >> 16 & 0xFF) / 255f,
                    (color >> 8 & 0xFF) / 255f,
                    (color & 0xFF) / 255f,
                    (color >> 24 & 0xFF) / 255f
            };
        }
    }


}
