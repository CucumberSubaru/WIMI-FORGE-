package com.backpackinv.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

/** Подписки на события клиента. В Forge-версии миксины не нужны: хватает штатных событий. */
public final class BackpackInvClient {
    private BackpackInvClient() {}

    public static void init(IEventBus modBus) {
        modBus.addListener(BackpackInvClient::registerModels);
        InventoryAnimation.reloadConfig();
        MinecraftForge.EVENT_BUS.addListener(BackpackInvClient::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(BackpackInvClient::onScreenOpening);
        MinecraftForge.EVENT_BUS.addListener(BackpackInvClient::onRenderHand);
    }

    private static void registerModels(ModelEvent.RegisterAdditional event) {
        event.register(InventoryAnimation.BODY_MODEL);
        event.register(InventoryAnimation.LID_MODEL);
    }

    private static void onClientTick(TickEvent.ClientTickEvent.Post event) {
        InventoryAnimation.tick(Minecraft.getInstance());
    }

    /** Перехватываем открытие инвентаря и вместо него запускаем анимацию. */
    private static void onScreenOpening(ScreenEvent.Opening event) {
        if (event.getNewScreen() instanceof InventoryScreen && InventoryAnimation.handleOpenRequest()) {
            event.setCanceled(true);
        }
    }

    /** Пока идёт анимация, вместо ванильных рук рисуем свои с рюкзаком. */
    private static void onRenderHand(RenderHandEvent event) {
        if (!InventoryAnimation.isActive()) {
            return;
        }
        event.setCanceled(true);
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        BackpackRenderer.render(event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(),
                event.getPartialTick(), player);
    }
}
