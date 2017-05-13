package profhugo.terra.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import profhugo.terra.capabilities.StaminaProvider;
import profhugo.terra.handler.CapabilityHandler;
import profhugo.terra.handler.EntityHandler;
import profhugo.terra.handler.PacketHandler;

public class CommonProxy {

	public void registerItemRenderer(Item item, int meta, String id) {
	}

	public void preInit() {

	}

	public void init() {
		StaminaProvider.init();
		MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
		MinecraftForge.EVENT_BUS.register(new EntityHandler());
		MinecraftForge.EVENT_BUS.register(new PacketHandler());
		PacketHandler.init();
	}

	public void postInit() {

	}

	public EntityPlayer getClientPlayer() {
		return null;
	}

}
