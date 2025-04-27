package line.lineclient.utils;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;

public interface Wrapper {
    Tessellator TESSELLATOR = Tessellator.getInstance();
    BufferBuilder BUILDER = TESSELLATOR.getBuffer();
    Minecraft mc = Minecraft.getInstance();
    MainWindow window = mc.getMainWindow();
}
