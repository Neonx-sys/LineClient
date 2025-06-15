package line.lineclient.module.modules.render;

import line.lineclient.module.Category;
import line.lineclient.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.util.registry.Registry;

import java.util.Arrays;
import java.util.List;

public class XRay extends Module {
    private static final Minecraft mc = Minecraft.getInstance();
    private static final List<Block> visibleBlocks = Arrays.asList(
            Blocks.COAL_ORE,
            Blocks.IRON_ORE,
            Blocks.GOLD_ORE,
            Blocks.DIAMOND_ORE,
            Blocks.EMERALD_ORE,
            Blocks.REDSTONE_ORE,
            Blocks.LAPIS_ORE,
            Blocks.NETHER_GOLD_ORE,
            Blocks.NETHER_QUARTZ_ORE,
            Blocks.ANCIENT_DEBRIS
    );

    public XRay() {
        super("X-Ray", Category.RENDER, "Показує тільки руди", -1, 1);
    }

    @Override
    public void onEnable() {
        mc.worldRenderer.loadRenderers();

    }

    @Override
    public void onDisable() {
        mc.worldRenderer.loadRenderers();
    }

    public static boolean isVisible(Block block) {
        return visibleBlocks.contains(block);
    }
}
