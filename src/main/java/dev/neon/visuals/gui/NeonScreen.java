package dev.neon.visuals.gui;

import dev.neon.visuals.Config;
import dev.neon.visuals.module.HitParticles;
import dev.neon.visuals.module.PotionHud;
import dev.neon.visuals.module.TargetHud;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

/** Меню Neon Visuals. Правый Shift. HUD-блоки двигаются ЛКМ прямо на экране. */
public class NeonScreen extends Screen {
    private static final int[] PALETTE = {0xFF00E5FF, 0xFFFF2BD6, 0xFF8A2BE2, 0xFF3DFF7A, 0xFFFF8A00, 0xFFFFFFFF};
    private static final float[] SWAP = {0f, 0.25f, 0.5f, 0.75f, 1f};

    private int dragging = 0; // 1 target, 2 potion
    private int offX, offY;
    private boolean wasDown;
    private int panelX, panelY, panelW = 230;

    public NeonScreen() { super(Text.literal("Neon Visuals")); }

    @Override
    protected void init() {
        Config c = Config.I;
        panelX = width - panelW - 8;
        panelY = 8;
        int y = panelY + 22;
        int x = panelX + 6, w = panelW - 12;

        y = toggle(x, y, w, "Target HUD", () -> c.targetHud, v -> c.targetHud = v);
        y = toggle(x, y, w, "  Armor + durability", () -> c.targetArmor, v -> c.targetArmor = v);
        y = toggle(x, y, w, "  Head only", () -> c.targetHeadOnly, v -> c.targetHeadOnly = v);
        y = toggle(x, y, w, "Potion HUD", () -> c.potionHud, v -> c.potionHud = v);
        y = cycle(x, y, w, "Accent", () -> "#" + Integer.toHexString(c.accent & 0xFFFFFF),
                () -> c.accent = next(PALETTE, c.accent));

        y = toggle(x, y, w, "Hit Particles", () -> c.hitParticles, v -> c.hitParticles = v);
        y = cycle(x, y, w, "  Type", () -> HitParticles.NAMES[c.hitParticleType % HitParticles.NAMES.length],
                () -> c.hitParticleType = (c.hitParticleType + 1) % HitParticles.NAMES.length);
        y = stepper(x, y, w, "  Count", () -> String.valueOf(c.hitParticleCount),
                () -> c.hitParticleCount = Math.max(1, c.hitParticleCount - 2),
                () -> c.hitParticleCount = Math.min(60, c.hitParticleCount + 2));

        y = toggle(x, y, w, "Item Swap", () -> c.itemSwap, v -> c.itemSwap = v);
        y = cycle(x, y, w, "  Swap anim", () -> (int) (c.swapFactor * 100) + "%", () -> {
            int i = 0;
            for (int k = 0; k < SWAP.length; k++) if (Math.abs(SWAP[k] - c.swapFactor) < 0.01f) i = k;
            c.swapFactor = SWAP[(i + 1) % SWAP.length];
        });

        y = toggle(x, y, w, "Fog", () -> c.fog, v -> c.fog = v);
        y = cycle(x, y, w, "  Fog color", () -> "#" + Integer.toHexString(c.fogColor & 0xFFFFFF),
                () -> c.fogColor = next(PALETTE, c.fogColor));
        y = stepper(x, y, w, "  Fog density", () -> String.valueOf(c.fogAlpha),
                () -> c.fogAlpha = Math.max(5, c.fogAlpha - 10), () -> c.fogAlpha = Math.min(200, c.fogAlpha + 10));

        y = toggle(x, y, w, "Sword Adjust", () -> c.swordAdjust, v -> c.swordAdjust = v);
        y = stepper(x, y, w, "  X", () -> f(c.swX), () -> c.swX -= .05f, () -> c.swX += .05f);
        y = stepper(x, y, w, "  Y", () -> f(c.swY), () -> c.swY -= .05f, () -> c.swY += .05f);
        y = stepper(x, y, w, "  Z", () -> f(c.swZ), () -> c.swZ -= .05f, () -> c.swZ += .05f);
        y = stepper(x, y, w, "  Scale", () -> f(c.swScale), () -> c.swScale = Math.max(.2f, c.swScale - .05f), () -> c.swScale = Math.min(2.5f, c.swScale + .05f));
        y = stepper(x, y, w, "  Rot X", () -> f(c.swRotX), () -> c.swRotX -= 5, () -> c.swRotX += 5);
        y = stepper(x, y, w, "  Rot Y", () -> f(c.swRotY), () -> c.swRotY -= 5, () -> c.swRotY += 5);
        y = stepper(x, y, w, "  Rot Z", () -> f(c.swRotZ), () -> c.swRotZ -= 5, () -> c.swRotZ += 5);
    }

    private static String f(float v) { return String.format("%.2f", v); }

    private static int next(int[] arr, int cur) {
        for (int i = 0; i < arr.length; i++) if ((arr[i] & 0xFFFFFF) == (cur & 0xFFFFFF)) return arr[(i + 1) % arr.length];
        return arr[0];
    }

    private int toggle(int x, int y, int w, String label, Supplier<Boolean> get, java.util.function.Consumer<Boolean> set) {
        ButtonWidget[] b = new ButtonWidget[1];
        b[0] = ButtonWidget.builder(Text.literal(label + ": " + (get.get() ? "ON" : "OFF")), btn -> {
            set.accept(!get.get());
            btn.setMessage(Text.literal(label + ": " + (get.get() ? "ON" : "OFF")));
        }).dimensions(x, y, w, 16).build();
        addDrawableChild(b[0]);
        return y + 18;
    }

    private int cycle(int x, int y, int w, String label, Supplier<String> val, Runnable action) {
        addDrawableChild(ButtonWidget.builder(Text.literal(label + ": " + val.get()), btn -> {
            action.run();
            btn.setMessage(Text.literal(label + ": " + val.get()));
        }).dimensions(x, y, w, 16).build());
        return y + 18;
    }

    private int stepper(int x, int y, int w, String label, Supplier<String> val, Runnable dec, Runnable inc) {
        ButtonWidget[] mid = new ButtonWidget[1];
        int bw = 20;
        addDrawableChild(ButtonWidget.builder(Text.literal("-"), b -> { dec.run(); mid[0].setMessage(Text.literal(label + ": " + val.get())); })
                .dimensions(x, y, bw, 16).build());
        mid[0] = ButtonWidget.builder(Text.literal(label + ": " + val.get()), b -> {})
                .dimensions(x + bw + 2, y, w - 2 * bw - 4, 16).build();
        addDrawableChild(mid[0]);
        addDrawableChild(ButtonWidget.builder(Text.literal("+"), b -> { inc.run(); mid[0].setMessage(Text.literal(label + ": " + val.get())); })
                .dimensions(x + w - bw, y, bw, 16).build());
        return y + 18;
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        Config c = Config.I;
        ctx.fill(0, 0, width, height, 0x88000000);
        ctx.fill(panelX, panelY, panelX + panelW, height - 8, 0xDD0B0B14);
        ctx.fill(panelX, panelY, panelX + panelW, panelY + 1, c.accent);
        ctx.drawText(textRenderer, "NEON VISUALS", panelX + 8, panelY + 8, c.accent, true);
        ctx.drawText(textRenderer, "ЛКМ — тащи HUD", panelX + panelW - 8 - textRenderer.getWidth("ЛКМ — тащи HUD"), panelY + 8, 0xFF8888AA, false);

        // Перетаскивание HUD: опрос ЛКМ (не зависит от сигнатур Screen#mouseClicked)
        boolean down = GLFW.glfwGetMouseButton(client.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        if (down && !wasDown) {
            if (in(mx, my, c.targetX, c.targetY, TargetHud.W, TargetHud.H)) { dragging = 1; offX = mx - c.targetX; offY = my - c.targetY; }
            else if (in(mx, my, c.potionX, c.potionY, PotionHud.W, 40)) { dragging = 2; offX = mx - c.potionX; offY = my - c.potionY; }
        }
        if (!down) { if (dragging != 0) Config.save(); dragging = 0; }
        wasDown = down;
        if (dragging == 1) { c.targetX = mx - offX; c.targetY = my - offY; }
        if (dragging == 2) { c.potionX = mx - offX; c.potionY = my - offY; }

        // Превью HUD рисуется из HudRenderCallback (видит NeonScreen как "preview")
        super.render(ctx, mx, my, delta);
    }

    private static boolean in(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    @Override public boolean shouldPause() { return false; }
    @Override public void close() { Config.save(); super.close(); }
}
