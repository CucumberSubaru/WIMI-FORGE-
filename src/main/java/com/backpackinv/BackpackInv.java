package com.backpackinv;

import com.backpackinv.client.BackpackInvClient;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(BackpackInv.MOD_ID)
public class BackpackInv {
    public static final String MOD_ID = "wini";
    /** Папка ресурсов: assets/backpackinv/... */
    public static final String ASSET_NAMESPACE = "backpackinv";

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ASSET_NAMESPACE);

    /** Служебные предметы: нужны только чтобы нарисовать корпус и крышку рюкзака в руках. Получить их в игре нельзя. */
    public static final RegistryObject<Item> BACKPACK_BODY = ITEMS.register("backpack_body", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BACKPACK_LID = ITEMS.register("backpack_lid", () -> new Item(new Item.Properties()));

    public BackpackInv(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();
        ITEMS.register(modBus);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            BackpackInvClient.init();
        }
    }
}
