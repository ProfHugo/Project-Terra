package profhugo.terra.proxy;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import profhugo.terra.capabilities.IStamina;
import profhugo.terra.capabilities.Stamina;
import profhugo.terra.capabilities.StaminaStorage;
import profhugo.terra.handler.CapabilityHandler;
import profhugo.terra.handler.EntityHandler;
import profhugo.terra.handler.GuiHandler;

public class CommonProxy {
	public void registerItemRenderer(Item item, int meta, String id) {
	}

	public void preInit() {

	}

	public void init() {
		CapabilityManager.INSTANCE.register(IStamina.class, new StaminaStorage(), Stamina.class);
		MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
		MinecraftForge.EVENT_BUS.register(new EntityHandler());
		MinecraftForge.EVENT_BUS.register(new GuiHandler());

	}

	public void postInit() {

	}
}
