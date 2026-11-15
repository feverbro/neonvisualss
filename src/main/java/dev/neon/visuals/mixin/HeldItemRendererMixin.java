package dev.neon.visuals.mixin;

import dev.neon.visuals.Config;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

    /** Item Swap: множитель анимации смены предмета (0 = мгновенно, 1 = ваниль). */
    @ModifyVariable(method = "applyEquipOffset", at = @At("HEAD"), argsOnly = true)
    private float neon$swap(float equipProgress) {
        Config c = Config.I;
        return c.itemSwap ? equipProgress * c.swapFactor : equipProgress;
    }

    /** Sword Adjust: сдвиг / масштаб / поворот предмета в руке. */
    @Inject(method = "applyEquipOffset", at = @At("TAIL"))
    private void neon$adjust(MatrixStack m, Arm arm, float equipProgress, CallbackInfo ci) {
        Config c = Config.I;
        if (!c.swordAdjust) return;
        int side = arm == Arm.RIGHT ? 1 : -1;
        m.translate(c.swX * side, c.swY, c.swZ);
        m.scale(c.swScale, c.swScale, c.swScale);
        m.multiply(RotationAxis.POSITIVE_X.rotationDegrees(c.swRotX));
        m.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(c.swRotY * side));
        m.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(c.swRotZ * side));
    }
}
