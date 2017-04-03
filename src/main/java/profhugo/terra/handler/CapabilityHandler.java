package profhugo.terra.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import profhugo.terra.ProjectTerra;
import profhugo.terra.capabilities.StaminaProvider;

public class CapabilityHandler {
	public static final ResourceLocation STAMINA_CAP = new ResourceLocation(ProjectTerra.MODID, "stamina");

	@SuppressWarnings("deprecation")
	@SubscribeEvent
	public void attachCapability(AttachCapabilitiesEvent.Entity event) {
		if (!(event.getEntity() instanceof EntityPlayer))
			return;
		event.addCapability(STAMINA_CAP, new StaminaProvider());
	}
}
