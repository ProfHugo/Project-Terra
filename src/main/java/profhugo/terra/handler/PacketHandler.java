package profhugo.terra.handler;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import profhugo.terra.ProjectTerra;
import profhugo.terra.capabilities.IStamina;
import profhugo.terra.capabilities.StaminaProvider;
import profhugo.terra.network.StaminaPacket;

public class PacketHandler{

	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ProjectTerra.MODID);

	public static void init() {
		INSTANCE.registerMessage(StaminaPacket.class, StaminaPacket.class, 0, Side.CLIENT);
	}
	
	@SubscribeEvent
	public void onPlayerUpdate(LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (!(entity instanceof EntityPlayerMP) || entity.getEntityWorld().isRemote)
			return;
		IStamina stamPack = entity.getCapability(StaminaProvider.STAMINA_CAP, null);
		StaminaPacket netPacket = new StaminaPacket(stamPack.getStamina(), stamPack.getMaxStamina());
		INSTANCE.sendTo(netPacket, (EntityPlayerMP) entity);
	}
}
