package line.lineclient.utils;

import com.mojang.blaze3d.platform.GlStateManager;
import line.lineclient.Client;
import org.lwjgl.opengl.GL30;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.GL_QUADS;


public class Shader implements Wrapper {

    public static final int VERTEX_SHADER;
    private int programId;

    static {
        VERTEX_SHADER = GlStateManager.createShader(GL30.GL_VERTEX_SHADER);
        GlStateManager.shaderSource(VERTEX_SHADER, getShaderSource("vertex.vert"));
        GlStateManager.compileShader(VERTEX_SHADER);
    }

    public Shader(String fragmentShaderName) {
        int programId = GlStateManager.createProgram();
        try {
            int fragmentShader = GlStateManager.createShader(GL30.GL_FRAGMENT_SHADER);
            GlStateManager.shaderSource(fragmentShader, getShaderSource(fragmentShaderName));
            GlStateManager.compileShader(fragmentShader);

            int isFragmentCompiled = GL30.glGetShaderi(fragmentShader, GL30.GL_COMPILE_STATUS);
            if(isFragmentCompiled == 0) {
                GlStateManager.deleteShader(fragmentShader);
            }

            GlStateManager.attachShader(programId, VERTEX_SHADER);
            GlStateManager.attachShader(programId, fragmentShader);
            GlStateManager.linkProgram(programId);
        } catch(Exception e) {
            e.printStackTrace();
        }
        this.programId = programId;
    }

    public void load() {
        GlStateManager.useProgram(programId);
    }

    public void unload() {
        GlStateManager.useProgram(0);
    }

    public int getUniform(String name) {
        return GL30.glGetUniformLocation(programId, name);
    }

    public void setUniformf(String name, float... args) {
        int loc = GL30.glGetUniformLocation(programId, name);
        switch (args.length) {
            case 1:
                GL30.glUniform1f(loc, args[0]);
                break;
            case 2:
                GL30.glUniform2f(loc, args[0], args[1]);
                break;
            case 3:
                GL30.glUniform3f(loc, args[0], args[1], args[2]);
                break;
            case 4:
                GL30.glUniform4f(loc, args[0], args[1], args[2], args[3]);
                break;
        }
    }

    public void setUniformi(String name, int... args) {
        int loc = GL30.glGetUniformLocation(programId, name);
        switch (args.length) {
            case 1:
                GL30.glUniform1i(loc, args[0]);
                break;
            case 2:
                GL30.glUniform2i(loc, args[0], args[1]);
                break;
            case 3:
                GL30.glUniform3i(loc, args[0], args[1], args[2]);
                break;
            case 4:
                GL30.glUniform4i(loc, args[0], args[1], args[2], args[3]);
                break;
        }
    }

    public void setUniformfb(String name, FloatBuffer buffer) {
        GL30.glUniform1fv(GL30.glGetUniformLocation(programId, name), buffer);
    }

    public static void draw() {
        draw(0, 0, window.getScaledWidth(), window.getScaledHeight());
    }

    public static void draw(double x, double y, double width, double height) {
        GL30.glBegin(GL_QUADS);
        GL30.glTexCoord2d(0, 0);
        GL30.glVertex2d(x, y);
        GL30.glTexCoord2d(0, 1);
        GL30.glVertex2d(x, y + height);
        GL30.glTexCoord2d(1, 1);
        GL30.glVertex2d(x + width, y + height);
        GL30.glTexCoord2d(1, 0);
        GL30.glVertex2d(x + width, y);
        GL30.glEnd();
    }

    public static String getShaderSource(String fileName) {
        String source = "";
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Client.class
                .getResourceAsStream("/Yougame/shaders/" + fileName)));
        source = bufferedReader.lines().filter(str -> !str.isEmpty()).map(str -> str = str.replace("\t", "")).collect(Collectors.joining("\n"));
        try {
            bufferedReader.close();
        } catch (IOException ignored) {

        }
        return source;
    }
}