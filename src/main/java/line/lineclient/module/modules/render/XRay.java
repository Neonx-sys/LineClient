package line.lineclient.module.modules.render;

import line.lineclient.event.Event;
import line.lineclient.event.events.EventRender;
import line.lineclient.module.Category;
import line.lineclient.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import line.lineclient.utils.render.RenderUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Vector3d;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class XRay extends Module {

    private static final Minecraft mc = Minecraft.getInstance();

    public static boolean enabled = false;

    public static int radius = 32;
    public static int density = 1;

    public static Map<Block, Boolean> enabledOres = new HashMap<>();
    public static Map<Block, Integer> oreColors = new HashMap<>();

    static {
        enabledOres.put(Blocks.COAL_ORE, true);
        enabledOres.put(Blocks.IRON_ORE, true);
        enabledOres.put(Blocks.GOLD_ORE, false);
        enabledOres.put(Blocks.DIAMOND_ORE, true);
        enabledOres.put(Blocks.EMERALD_ORE, false);
        enabledOres.put(Blocks.REDSTONE_ORE, false);
        enabledOres.put(Blocks.LAPIS_ORE, false);
        enabledOres.put(Blocks.ANCIENT_DEBRIS, false);
        enabledOres.put(Blocks.NETHER_QUARTZ_ORE, false);

        oreColors.put(Blocks.COAL_ORE, new Color(0, 0, 0).getRGB());
        oreColors.put(Blocks.IRON_ORE, new Color(200, 200, 200).getRGB());
        oreColors.put(Blocks.GOLD_ORE, new Color(255, 215, 0).getRGB());
        oreColors.put(Blocks.DIAMOND_ORE, new Color(0, 191, 255).getRGB());
        oreColors.put(Blocks.EMERALD_ORE, new Color(0, 255, 0).getRGB());
        oreColors.put(Blocks.REDSTONE_ORE, new Color(255, 0, 0).getRGB());
        oreColors.put(Blocks.LAPIS_ORE, new Color(0, 0, 255).getRGB());
        oreColors.put(Blocks.ANCIENT_DEBRIS, new Color(68, 33, 18).getRGB());
        oreColors.put(Blocks.NETHER_QUARTZ_ORE, new Color(255, 255, 255).getRGB());
    }

    public XRay()
    {
        super("X-Ray", Category.RENDER, "Тестировка модуля", -1, 1);
    }

    @Override
    public void event(Event e) {
        if (e instanceof EventRender) {
            if (((EventRender) e).isRender3D()) {
                for (int x = -25; x <= 25; x++) {
                    for (int y = -25; y <= 25; y++) {
                        for (int z = -25; z <= 25; z++) {
                            BlockPos pos = new BlockPos(mc.player.getPosX() + x, mc.player.getPosY() + y, mc.player.getPosZ() + z);
                            BlockState state = mc.world.getBlockState(pos);
                            Block block = state.getBlock();
                            if (block == Blocks.COAL_ORE) {
                                RenderUtils.Render3D.drawBlockBox(pos, new Color(12, 12, 12, 255).getRGB());
                            }
                            if (block == Blocks.IRON_ORE) {
                                RenderUtils.Render3D.drawBlockBox(pos, new Color(122, 122, 122, 255).getRGB());
                            }
                            /*if (block == Blocks.REDSTONE_ORE && ores.get(2)) {
                                RenderUtils.Render3D.drawBlockBox(pos, new Color(255, 82, 82, 255).getRGB());
                            }
                            if (block == Blocks.GOLD_ORE && ores.get(3)) {
                                RenderUtils.Render3D.drawBlockBox(pos, new Color(247, 255, 102, 255).getRGB());
                            }
                            if (block == Blocks.NETHER_GOLD_ORE && ores.get(3)) {
                                RenderUtils.Render3D.drawBlockBox(pos, new Color(247, 255, 102, 255).getRGB());
                            }*/
                            if (block == Blocks.EMERALD_ORE ) {
                                RenderUtils.Render3D.drawBlockBox(pos, new Color(116, 252, 101, 255).getRGB());
                            }
                            if (block == Blocks.DIAMOND_ORE) {
                                RenderUtils.Render3D.drawBlockBox(pos, new Color(77, 219, 255, 255).getRGB());
                            }
                            if (block == Blocks.ANCIENT_DEBRIS) {
                                RenderUtils.Render3D.drawBlockBox(pos, new Color(105, 60, 12, 255).getRGB());
                            }
                        }
                    }
                }
            }
        }
    }
}
