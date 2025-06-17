package line.lineclient.module.modules.render;

import line.lineclient.event.Event;
import line.lineclient.event.events.EventRender;
import line.lineclient.module.Category;
import line.lineclient.module.Module;
import line.lineclient.ui.clickgui.setting.settings.CheckBoxSetting;
import line.lineclient.ui.clickgui.setting.settings.ValueSetting;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.IParticleData;

import java.util.ArrayList;
import java.util.List;

public class HitParticles extends Module {
    private final CheckBoxSetting enchantmentHit = new CheckBoxSetting("Render enchantments hits", false);
    private final CheckBoxSetting enchantments = new CheckBoxSetting("Render enchantments", false);
    private final CheckBoxSetting crit = new CheckBoxSetting("Render crit hits", false);
    private final CheckBoxSetting cloud = new CheckBoxSetting("Render cloud hits", false);
    private final CheckBoxSetting crimsonSpore = new CheckBoxSetting("Render crimson_spore hits", false);
    private final CheckBoxSetting explosion = new CheckBoxSetting("Render enchantments hits", false);
    private final CheckBoxSetting blood = new CheckBoxSetting("Render bloods hits", false);
    private final CheckBoxSetting obsidian = new CheckBoxSetting("Render obsidian hits", false);
    private final CheckBoxSetting nectar = new CheckBoxSetting("Render nectar hits", false);
    private final CheckBoxSetting bubblePop = new CheckBoxSetting("Render bubbles pops hits", false);
    private final CheckBoxSetting campfireSmoke = new CheckBoxSetting("Render campfire smoke hits", false);
    private final CheckBoxSetting composter = new CheckBoxSetting("Render composter hits", false);
    private final CheckBoxSetting fireWork = new CheckBoxSetting("Render fireWork hits", false);
    private final CheckBoxSetting swords = new CheckBoxSetting("Render swords hits", false);


    private final ValueSetting particleCount = new ValueSetting("Particle count", 2f, 1f, 100f, 1f);
    private final ValueSetting particleSpeedMultiplier = new ValueSetting("Particle speed multiplier", 1f, 0.2f, 10f, 0.2f);
    private final ValueSetting particleOffsetMultiplier = new ValueSetting("Particle offset multiplier", 2f, 1f, 10f, 1f);

    public HitParticles() {
        super("HitParticles", Category.RENDER, "Hit particles", -1, 1);
        addSetting(enchantmentHit);
        addSetting(enchantments);
        addSetting(crit);
        addSetting(swords);
        addSetting(cloud);
        addSetting(crimsonSpore);
        addSetting(explosion);
        addSetting(blood);
        addSetting(obsidian);
        addSetting(nectar);
        addSetting(bubblePop);
        addSetting(campfireSmoke);
        addSetting(composter);
        addSetting(fireWork);

        addSetting(particleCount);
        addSetting(particleSpeedMultiplier);
        addSetting(particleOffsetMultiplier);
    }

    public void event(Event e) {
        if (e instanceof EventRender) {
            EventRender event = (EventRender) e;
            if (event.getPartialTicks() == 0) {
                return;
            }
        }
    }

    public List<BasicParticleType> getSelectedParticles() {
        List<BasicParticleType> particles = new ArrayList<>();
        if (enchantmentHit.get()) particles.add(ParticleTypes.ENCHANTED_HIT);
        if (enchantments.get()) particles.add(ParticleTypes.ENCHANT);
        if (crit.get()) particles.add(ParticleTypes.CRIT);
        if (swords.get()) particles.add(ParticleTypes.SWEEP_ATTACK);
        if (cloud.get()) particles.add(ParticleTypes.CLOUD);
        if (crimsonSpore.get()) particles.add(ParticleTypes.CRIMSON_SPORE);
        if (explosion.get()) particles.add(ParticleTypes.EXPLOSION);
        if (blood.get()) particles.add(ParticleTypes.FALLING_LAVA);
        if (obsidian.get()) particles.add(ParticleTypes.LANDING_OBSIDIAN_TEAR);
        if (nectar.get()) particles.add(ParticleTypes.FALLING_NECTAR);
        if (bubblePop.get()) particles.add(ParticleTypes.BUBBLE_POP);
        if (campfireSmoke.get()) particles.add(ParticleTypes.CAMPFIRE_COSY_SMOKE);
        if (composter.get()) particles.add(ParticleTypes.COMPOSTER);
        if (fireWork.get()) particles.add(ParticleTypes.FIREWORK);
        return particles;
    }


    public int GetParticleCount() {
        System.out.println(particleCount.getValue());
        return (int) particleCount.getValue();
    }
    public int GetParticleSpeedMultiplier() {
        return (int) particleSpeedMultiplier.getValue();
    }
    public int GetParticleOffsetMultiplier() {
        return (int) particleOffsetMultiplier.getValue();
    }
}
