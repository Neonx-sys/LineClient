package line.lineclient.module.modules.render;

import line.lineclient.event.Event;
import line.lineclient.event.events.EventRender;
import line.lineclient.event.events.EventTick;
import line.lineclient.module.Category;
import line.lineclient.module.Module;
import line.lineclient.ui.clickgui.ClickGUI;
import line.lineclient.ui.clickgui.setting.settings.CheckBoxSetting;
import line.lineclient.ui.clickgui.setting.settings.ValueSetting;
import line.lineclient.utils.fonts.Fonts;
import line.lineclient.utils.render.RenderUtils;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import line.lineclient.module.modules.util.FriendManager;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static line.lineclient.utils.Wrapper.mc;

public class ESP extends Module {
    private final CheckBoxSetting showNearestOnly = new CheckBoxSetting("Тільки найближчі", false);
    private final ValueSetting maxDistance = new ValueSetting("Макс. відстань", 20f, 1f, 50f, 1f);
    private final ValueSetting maxPlayers = new ValueSetting("Кількість гравців", 5f, 1f, 10f, 1f);
    private final ValueSetting updateDelay = new ValueSetting("Затримка оновлення", 10f, 1f, 20f, 1f);
    private int tickCounter = 0;
    private List<PlayerInfo> playerInfoList = new ArrayList<>();

    public ESP() {
        super("ESP", Category.RENDER, "Показує здоров'я гравців", -1, 2);
        addSetting(showNearestOnly);
        addSetting(maxDistance);
        addSetting(maxPlayers);
        addSetting(updateDelay);
    }

    private static class PlayerInfo {
        String name;
        float health;
        double distance;
        PlayerEntity player;

        PlayerInfo(PlayerEntity player, String name, float health, double distance) {
            this.player = player;
            this.name = name;
            this.health = health;
            this.distance = distance;
        }
    }

    @Override
    public void event(Event e) {
        if (e instanceof EventTick) {
            handleTick();
        } else if (e instanceof EventRender) {
            renderHealthBars((EventRender) e);
        }
    }

    private void handleTick() {
        if (mc.player == null || mc.world == null) return;

        tickCounter++;
        if (tickCounter < updateDelay.getValue()) return;
        tickCounter = 0;

        playerInfoList.clear();
        
        List<PlayerInfo> tempList = mc.world.getPlayers().stream()
            .filter(player -> player != mc.player && player.isAlive())
            .map(player -> new PlayerInfo(
                player,
                player.getName().getString(),
                player.getHealth(),
                mc.player.getDistance(player)
            ))
            .sorted(Comparator.comparingDouble(p -> p.distance))
            .collect(Collectors.toList());

        // Фільтруємо гравців за відстанню
        tempList = tempList.stream()
            .filter(info -> info.distance <= maxDistance.getValue())
            .collect(Collectors.toList());

        // Якщо включена опція "Тільки найближчі"
        if (showNearestOnly.get()) {
            playerInfoList = tempList.stream()
                .limit((int) maxPlayers.getValue())
                .collect(Collectors.toList());
        } else {
            // Якщо опція вимкнена, показуємо всіх гравців в радіусі
            playerInfoList = tempList;
        }
    }

    private void renderHealthBars(EventRender e) {
        if (playerInfoList.isEmpty()) return;

        int screenWidth = mc.getMainWindow().getScaledWidth();
        int screenHeight = mc.getMainWindow().getScaledHeight();
        int panelWidth = 160;
        int panelHeight = 45;
        int padding = 5;
        
        // Обмежуємо кількість гравців що відображаються до 5
        int playersToShow = Math.min(5, playerInfoList.size());
        int totalHeight = playersToShow * (panelHeight + padding);
        
        int startY = screenHeight - totalHeight - padding;

        // Рендеримо тільки перших 5 гравців
        for (int i = 0; i < playersToShow; i++) {
            PlayerInfo info = playerInfoList.get(i);
            int yPos = startY + (i * (panelHeight + padding));
            
            // Фон панелі
            RenderUtils.drawRoundedRect(
                screenWidth - panelWidth - padding, yPos,
                panelWidth, panelHeight, 3,
                new Color(0, 0, 0, 150)
            );

            // Обличчя гравця
            if (info.player instanceof AbstractClientPlayerEntity) {
                renderPlayerFace((AbstractClientPlayerEntity)info.player, e, screenWidth - panelWidth + 5, yPos + 5);
            }

            // Індикатор здоров'я
            float healthPercent = info.health / 20f;
            Color healthColor = getHealthColor(healthPercent);
            RenderUtils.drawRoundedRect(
                screenWidth - panelWidth + 35, yPos + 5,
                (int)((panelWidth - 45) * healthPercent), 16, 3,
                healthColor
            );

            // Ім'я та здоров'я
            String namePrefix = FriendManager.isFriend(info.name) ? info.name + "|Friend" : info.name;
            String healthText = String.format("%s %.1f❤", namePrefix, info.health);

            int textColor = FriendManager.isFriend(info.name) ? 
                new Color(0, 255, 0).getRGB() : // Зелений для друзів
                Color.WHITE.getRGB();           // Білий для інших

            Fonts.gilroy[15].drawString(
                e.getMatrixStack(),
                healthText,
                screenWidth - panelWidth + 40,
                yPos + 8,
                textColor
            );

            // Броня
            renderArmor(info.player, screenWidth - panelWidth + 35, yPos + 25);

            // Предмети в руках
            renderHeldItems(info.player, screenWidth - 55, yPos + 25);
        }
    }

    private void renderPlayerFace(AbstractClientPlayerEntity player, EventRender e, int x, int y) {
        ResourceLocation skin = player.getLocationSkin();
        mc.getTextureManager().bindTexture(skin);

        AbstractGui.blit(
            e.getMatrixStack(),
            x, y,
            18, 18,
            8f, 8f,
            8, 8,
            64, 64
        );

        AbstractGui.blit(
            e.getMatrixStack(),
            x, y,
            18, 18,
            40f, 8f,
            8, 8,
            64, 64
        );
    }

    private void renderArmor(PlayerEntity player, int x, int y) {
        ItemRenderer itemRenderer = mc.getItemRenderer();
        int spacing = 16;
        
        for (int i = 0; i < 4; i++) {
            ItemStack armorStack = player.inventory.armorInventory.get(3 - i);
            if (!armorStack.isEmpty()) {
                itemRenderer.renderItemIntoGUI(armorStack, x + (i * spacing), y);
            }
        }
    }

    private void renderHeldItems(PlayerEntity player, int x, int y) {
        ItemRenderer itemRenderer = mc.getItemRenderer();
        
        // Предмет в основній руці
        ItemStack mainHand = player.getHeldItem(Hand.MAIN_HAND);
        if (!mainHand.isEmpty()) {
            itemRenderer.renderItemIntoGUI(mainHand, x, y);
        }
        
        // Предмет в другій руці
        ItemStack offHand = player.getHeldItem(Hand.OFF_HAND);
        if (!offHand.isEmpty()) {
            itemRenderer.renderItemIntoGUI(offHand, x + 20, y);
        }
    }

    private Color getHealthColor(float healthPercent) {
        if (healthPercent > 0.7f) {
            return new Color(46, 204, 113, 200);     // Зелений
        } else if (healthPercent > 0.3f) {
            return new Color(241, 196, 15, 200);     // Жовтий
        } else {
            return new Color(231, 76, 60, 200);      // Червоний
        }
    }
}