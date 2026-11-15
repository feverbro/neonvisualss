package dev.neon.visuals.module;

import dev.neon.visuals.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.EntityHitResult;

public class TargetHud {
    public static final int W = 150, H = 52;
    private static LivingEntity target;
    private static long lastHit;
    private static float animHp, animAppear;
    private static final EquipmentSlot[] SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND};

    public static void onAttack(Entity e) {
        if (e instanceof LivingEntity le) { target = le; lastHit = System.currentTimeMillis(); }
    }

    public static void tick(MinecraftClient mc) {
        if (mc.crosshairTarget instanceof EntityHitResult r && r.getEntity() instanceof LivingEntity le
                && mc.player != null && le != mc.player) {
            if (target != le) animHp = le.getHealth();
            target = le; lastHit = System.currentTimeMillis();
        }
        boolean alive = target != null && target.isAlive() && !target.isRemoved();
        boolean fresh = System.currentTimeMillis() - lastHit < 3000;
        animAppear += ((alive && fresh ? 1f : 0f) - animAppear) * 0.2f;
        if (alive) animHp += (target.getHealth() - animHp) * 0.15f;
    }

    public static void render(DrawContext ctx) {
        Config c = Config.I;
        var mc = MinecraftClient.getInstance();
        boolean preview = mc.currentScreen instanceof dev.neon.visuals.gui.NeonScreen;
        if (!c.targetHud || mc.player == null) return;
        LivingEntity t = target;
        if ((t == null || animAppear < 0.02f) && !preview) return;
        if (t == null) t = mc.player;
        float a = preview ? 1f : animAppear;
        int alpha = (int) (a * 255);
        int x = c.targetX, y = c.targetY + (int) ((1 - a) * 8);
        int acc = (alpha << 24) | (c.accent & 0xFFFFFF);

        panel(ctx, x, y, W, H, alpha, acc);
        var tr = mc.textRenderer;
        String name = t.getName().getString();
        ctx.drawText(tr, name, x + 8, y + 6, (alpha << 24) | 0xFFFFFF, true);

        float max = Math.max(1f, t.getMaxHealth());
        float hp = Math.min(max, preview && t == mc.player ? t.getHealth() : animHp);
        int bx = x + 8, by = y + 19, bw = W - 16;
        ctx.fill(bx, by, bx + bw, by + 6, (alpha / 2 << 24) | 0x101018);
        int fill = (int) (bw * (hp / max));
        float ratio = hp / max;
        int hpCol = ratio > 0.5f ? 0x3DFF7A : ratio > 0.25f ? 0xFFD23D : 0xFF3D4D;
        ctx.fill(bx, by, bx + fill, by + 6, (alpha << 24) | hpCol);
        ctx.fill(bx, by + 6, bx + fill, by + 7, acc);
        String hpText = String.format("%.1f / %.0f", t.getHealth(), max);
        ctx.drawText(tr, hpText, x + W - 8 - tr.getWidth(hpText), y + 6, (alpha << 24) | 0xBBBBCC, false);

        if (c.targetArmor) {
            int ix = x + 8;
            for (EquipmentSlot s : SLOTS) {
                if (c.targetHeadOnly && s != EquipmentSlot.HEAD) continue;
                ItemStack st = t.getEquippedStack(s);
                if (st.isEmpty()) { ix += 20; continue; }
                ctx.drawItem(st, ix, y + 30);
                if (st.isDamageable()) {
                    int left = st.getMaxDamage() - st.getDamage();
                    float d = left / (float) st.getMaxDamage();
                    int col = d > 0.5f ? 0x3DFF7A : d > 0.25f ? 0xFFD23D : 0xFF3D4D;
                    ctx.fill(ix, y + 47, ix + 16, y + 48, 0xFF202028);
                    ctx.fill(ix, y + 47, ix + (int) (16 * d), y + 48, 0xFF000000 | col);
                }
                ix += 20;
            }
        }
    }

    static void panel(DrawContext ctx, int x, int y, int w, int h, int alpha, int accent) {
        int bg = (Math.min(alpha, 170) << 24) | 0x0B0B14;
        ctx.fill(x + 1, y, x + w - 1, y + h, bg);
        ctx.fill(x, y + 1, x + w, y + h - 1, bg);
        ctx.fill(x + 1, y, x + w - 1, y + 1, accent);   // неоновая верхняя линия
        ctx.fill(x + 1, y + h - 1, x + w - 1, y + h, (alpha / 3 << 24) | (accent & 0xFFFFFF));
    }
}
