package dev.neon.visuals;

import dev.neon.visuals.gui.NeonScreen;
import dev.neon.visuals.module.FogModule;
import dev.neon.visuals.module.HitParticles;
import dev.neon.visuals.module.PotionHud;
import dev.neon.visuals.module.TargetHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.ActionResult;
import org.lwjgl.glfw.GLFW;

public class NeonVisuals implements ClientModInitializer {
    public static final String ID = "neonvisuals";
    private boolean keyDown;

    @Override
    public void onInitializeClient() {
        Config.load();

        // Правый Shift открывает меню (опрос клавиши — не зависит от API категорий биндов)
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            boolean down = InputUtil.isKeyPressed(client.getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT);
            if (down && !keyDown && client.currentScreen == null) client.setScreen(new NeonScreen());
            keyDown = down;
            TargetHud.tick(client);
        });

        // Хит: запоминаем цель + частицы
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            if (world.isClient()) {
                TargetHud.onAttack(entity);
                HitParticles.spawn(entity);
            }
            return ActionResult.PASS;
        });

        HudRenderCallback.EVENT.register((ctx, tick) -> {
            FogModule.render(ctx);
            TargetHud.render(ctx);
            PotionHud.render(ctx);
        });
    }
}
