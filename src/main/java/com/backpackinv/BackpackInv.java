package com.backpackinv;

import com.backpackinv.client.BackpackInvClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(BackpackInv.MOD_ID)
public class BackpackInv {
    public static final String MOD_ID = "wini";
    /** Папка ресурсов: assets/backpackinv/... */
    public static final String ASSET_NAMESPACE = "backpackinv";

    public BackpackInv(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();

        if (FMLEnvironment.dist == Dist.CLIENT) {
            BackpackInvClient.init(modBus);
        }
    }
}
