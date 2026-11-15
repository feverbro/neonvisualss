package dev.neon.visuals.module;

import dev.neon.visuals.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

import java.util.concurrent.ThreadLocalRandom;

public class HitParticles {
    public static final String[] NAMES = {"Crit", "Enchant Hit", "Flame", "Heart", "End Rod", "Totem"};
    private static final ParticleEffect[] TYPES = {
            ParticleTypes.CRIT, ParticleTypes.ENCHANTED_HIT, ParticleTypes.FLAME,
            ParticleTypes.HEART, ParticleTypes.END_ROD, ParticleTypes.TOTEM_OF_UNDYING
    };

    public static void spawn(Entity target) {
        Config c = Config.I;
        if (!c.hitParticles) return;
        var world = MinecraftClient.getInstance().world;
        if (world == null) return;
        var r = ThreadLocalRandom.current();
        ParticleEffect type = TYPES[Math.floorMod(c.hitParticleType, TYPES.length)];
        for (int i = 0; i < c.hitParticleCount; i++) {
            double x = target.getX() + (r.nextDouble() - 0.5) * target.getWidth();
            double y = target.getY() + r.nextDouble() * target.getHeight();
            double z = target.getZ() + (r.nextDouble() - 0.5) * target.getWidth();
            world.addParticleClient(type, x, y, z,
                    (r.nextDouble() - 0.5) * 0.4, r.nextDouble() * 0.35, (r.nextDouble() - 0.5) * 0.4);
        }
    }
}
