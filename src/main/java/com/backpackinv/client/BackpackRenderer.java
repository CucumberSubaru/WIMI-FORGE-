package com.backpackinv.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class BackpackRenderer {
    private BackpackRenderer() {}

    public static void render(PoseStack poseStack, MultiBufferSource buffer, int light, float partialTicks, LocalPlayer player) {
        BackpackInvConfig cfg = InventoryAnimation.config();
        float bag = InventoryAnimation.bagProgress(partialTicks);
        float lid = InventoryAnimation.lidProgress(partialTicks);
        float pitch = Mth.lerp(partialTicks, player.xRotO, player.getXRot());
        float tilt = cfg.useVanillaTilt ? calculateMapTilt(pitch) : cfg.fixedTilt;

        poseStack.pushPose();

        poseStack.translate(0.0F, 0.04F + (1.0F - bag) * -1.2F + tilt * -0.5F, -0.72F);
        poseStack.mulPose(Axis.XP.rotationDegrees(tilt * -85.0F));

        // наклон при доставании
        float raiseT = InventoryAnimation.raiseTilt(partialTicks);
        float lowerT = InventoryAnimation.lowerFactor(partialTicks);

        // доставание и убирание: одинаковый набор движений (смещение, наклон, поворот)
        poseStack.translate(cfg.raiseShiftX * raiseT + cfg.lowerShiftX * lowerT, 0.0F, 0.0F);
        poseStack.mulPose(Axis.XP.rotationDegrees(cfg.raiseTiltAngle * raiseT + cfg.lowerTiltAngle * lowerT));
        poseStack.mulPose(Axis.ZP.rotationDegrees(cfg.raiseRollAngle * raiseT + cfg.lowerRollAngle * lowerT));

        if (cfg.showHands && !player.isInvisible()) {
            renderHand(poseStack, buffer, light, player, HumanoidArm.LEFT, cfg, 0.0F);
            renderHand(poseStack, buffer, light, player, HumanoidArm.RIGHT, cfg, lid);
        }

        // рюкзак между руками
        poseStack.pushPose();
        poseStack.translate(cfg.offsetX, cfg.offsetY, cfg.offsetZ);
        poseStack.mulPose(Axis.XP.rotationDegrees(cfg.rotX));
        poseStack.mulPose(Axis.YP.rotationDegrees(cfg.rotY));
        poseStack.mulPose(Axis.ZP.rotationDegrees(cfg.rotZ));
        poseStack.scale(cfg.scale, cfg.scale, cfg.scale);
        poseStack.translate(0.0F, 0.21875F, 0.0F); // центрируем модель по высоте

        // корпус
        renderPart(poseStack, buffer, light, InventoryAnimation.bodyStack());

        // крышка поворачивается вокруг задней верхней кромки корпуса
        poseStack.pushPose();
        // точка шарнира задана в координатах модели (до сдвига центровки): y = (5.5 - 8) / 16, z = (6 - 8) / 16
        poseStack.translate(0.0F, -0.15625F, -0.125F);
        poseStack.mulPose(Axis.XP.rotationDegrees(-cfg.lidOpenAngle * lid));
        poseStack.translate(0.0F, 0.15625F, 0.125F);
        renderPart(poseStack, buffer, light, InventoryAnimation.lidStack());
        poseStack.popPose();

        poseStack.popPose();
        poseStack.popPose();
    }

    /** Та же формула наклона, что у ванильной карты в двух руках. */
    private static float calculateMapTilt(float pitch) {
        float f = 1.0F - pitch / 45.0F + 0.1F;
        f = Mth.clamp(f, 0.0F, 1.0F);
        return -Mth.cos(f * (float) Math.PI) * 0.5F + 0.5F;
    }

    private static void renderPart(PoseStack poseStack, MultiBufferSource buffer, int light, ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        mc.getItemRenderer().renderStatic(
                stack,
                ItemDisplayContext.FIXED,
                light,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                mc.level,
                0
        );
    }

    private static void renderHand(PoseStack poseStack, MultiBufferSource buffer, int light, LocalPlayer player,
                                   HumanoidArm side, BackpackInvConfig cfg, float lid) {
        float f = side == HumanoidArm.RIGHT ? 1.0F : -1.0F;

        poseStack.pushPose();

        // положение руки в плоскости рюкзака (x вправо, y вверх, z к камере)
        poseStack.translate(f * -cfg.handInward, cfg.handUp, cfg.handForward);
        poseStack.mulPose(Axis.XP.rotationDegrees(cfg.handExtraRotX));
        poseStack.mulPose(Axis.YP.rotationDegrees(f * cfg.handExtraRotY));
        poseStack.mulPose(Axis.ZP.rotationDegrees(f * cfg.handExtraRotZ));

        // правая рука тянется к крышке и откидывает её
        if (side == HumanoidArm.RIGHT && lid != 0.0F) {
            poseStack.translate(0.0F, cfg.openHandUp * lid, cfg.openHandBack * lid);
            poseStack.mulPose(Axis.XP.rotationDegrees(cfg.openHandRotX * lid));
        }

        // дальше та же поза, что у ванильной руки с картой
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(92.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(45.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(f * -41.0F));
        poseStack.translate(f * 0.3F, -1.1F, 0.45F);

        PlayerRenderer renderer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
        if (side == HumanoidArm.RIGHT) {
            renderer.renderRightHand(poseStack, buffer, light, player);
        } else {
            renderer.renderLeftHand(poseStack, buffer, light, player);
        }

        poseStack.popPose();
    }
}
