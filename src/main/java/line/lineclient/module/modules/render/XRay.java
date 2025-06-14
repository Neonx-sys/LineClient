package line.lineclient.module.modules.render;

import line.lineclient.event.Event;
import line.lineclient.event.events.EventRender;
import line.lineclient.module.Category;
import line.lineclient.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import line.lineclient.utils.render.RenderUtils;
import com.mojang.blaze3d.matrix.MatrixStack;

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
        if (e instanceof EventRender)
        {
            enabled=true;
        }
        else
        {
            enabled=false;
        }
    }

    public static void render(float partialTicks) {
        if (!enabled || mc.world == null || mc.player == null) return;

        ClientWorld world = mc.world;
        BlockPos playerPos = mc.player.getPosition();
        int px = playerPos.getX();
        int py = playerPos.getY();
        int pz = playerPos.getZ();

        int minY = MathHelper.clamp(py - radius, 0, 255);
        int maxY = MathHelper.clamp(py + radius, 0, 255);

        double radiusSq = radius * radius;

        for (int x = px - radius; x <= px + radius; x += density) {
            for (int z = pz - radius; z <= pz + radius; z += density) {
                for (int y = minY; y <= maxY; y++) {
                    double dx = x - px;
                    double dy = y - py;
                    double dz = z - pz;

                    if (dx * dx + dy * dy + dz * dz > radiusSq) continue;

                    BlockPos pos = new BlockPos(x, y, z);
                    Block block = world.getBlockState(pos).getBlock();

                    if (enabledOres.getOrDefault(block, false)) {
                        int color = oreColors.getOrDefault(block, 0xFFFFFF);
                        RenderUtils.drawBlockBox(pos, color);
                    }
                }
            }
        }
    }
}
