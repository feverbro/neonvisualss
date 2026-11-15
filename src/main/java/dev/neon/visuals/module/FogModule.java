package dev.neon.visuals.module;

import dev.neon.visuals.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/** Цветная дымка: градиент от краёв экрана к центру (виньетка). */
public class FogModule {
    public static void render(DrawContext ctx) {
        Config c = Config.I;
        if (!c.fog) return;
        var mc = MinecraftClient.getInstance();
        if (mc.options.hudHidden && mc.currentScreen == null) return;
        int w = ctx.getScaledWindowWidth(), h = ctx.getScaledWindowHeight();
        int rgb = c.fogColor & 0xFFFFFF;
        int layers = 14;
        for (int i = 0; i < layers; i++) {
            float t = 1f - i / (float) layers;
            int a = (int) (c.fogAlpha * t * t);
            if (a <= 0) continue;
            int col = (a << 24) | rgb;
            int dx = (int) (w * 0.5f * (i / (float) layers) * 0.55f);
            int dy = (int) (h * 0.5f * (i / (float) layers) * 0.55f);
            ctx.fill(dx, dy, w - dx, dy + 1 + h / 90, col);
            ctx.fill(dx, h - dy - 1 - h / 90, w - dx, h - dy, col);
            ctx.fill(dx, dy, dx + 1 + w / 120, h - dy, col);
            ctx.fill(w - dx - 1 - w / 120, dy, w - dx, h - dy, col);
        }
    }
}
