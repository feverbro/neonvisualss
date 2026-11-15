package dev.neon.visuals.module;

import dev.neon.visuals.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PotionHud {
    public static final int W = 120, ROW = 16;

    public static void render(DrawContext ctx) {
        Config c = Config.I;
        var mc = MinecraftClient.getInstance();
        if (!c.potionHud || mc.player == null) return;
        List<StatusEffectInstance> list = new ArrayList<>(mc.player.getStatusEffects());
        list.sort(Comparator.comparingInt(StatusEffectInstance::getDuration).reversed());
        boolean preview = mc.currentScreen instanceof dev.neon.visuals.gui.NeonScreen;
        if (list.isEmpty() && !preview) return;
        var tr = mc.textRenderer;
        int x = c.potionX, y = c.potionY;
        TargetHud.panel(ctx, x, y, W, 14 + Math.max(1, list.size()) * ROW, 255, 0xFF000000 | c.accent);
        ctx.drawText(tr, "Potions", x + 6, y + 4, 0xFFFFFFFF, true);
        int ry = y + 16;
        if (list.isEmpty()) ctx.drawText(tr, "—", x + 6, ry, 0xFF888899, false);
        for (StatusEffectInstance e : list) {
            int col = 0xFF000000 | e.getEffectType().value().getColor();
            String n = e.getEffectType().value().getName().getString() + (e.getAmplifier() > 0 ? " " + (e.getAmplifier() + 1) : "");
            ctx.fill(x + 5, ry + 1, x + 8, ry + 10, col);
            ctx.drawText(tr, n, x + 12, ry + 1, 0xFFFFFFFF, false);
            String time = e.isInfinite() ? "∞" : fmt(e.getDuration());
            int tc = (!e.isInfinite() && e.getDuration() < 200) ? 0xFFFF4D5D : 0xFFAAAABB;
            ctx.drawText(tr, time, x + W - 6 - tr.getWidth(time), ry + 1, tc, false);
            ry += ROW;
        }
    }

    private static String fmt(int ticks) {
        int s = ticks / 20;
        return String.format("%d:%02d", s / 60, s % 60);
    }
}
