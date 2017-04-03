package profhugo.terra.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import profhugo.terra.capabilities.IStamina;
import profhugo.terra.capabilities.StaminaProvider;
import profhugo.terra.gui.StaminaGui;

public class GuiHandler {
	
	StaminaGui stamGui = new StaminaGui(Minecraft.getMinecraft());
	float currentStam;
	float currentStamCap;
	
	@SubscribeEvent
	public void onRenderGui(RenderGameOverlayEvent.Post event) {
		if (event.getType() != ElementType.EXPERIENCE) return; 
		stamGui.updateStamina(currentStam, currentStamCap, Minecraft.getMinecraft());
	}
	
	@SubscribeEvent
	public void onPlayerUpdate(LivingUpdateEvent event){
		EntityLivingBase entity = event.getEntityLiving();
		if (!(entity instanceof EntityPlayerMP) || entity.getEntityWorld().isRemote)
			return;
		IStamina stamPack = entity.getCapability(StaminaProvider.STAMINA_CAP, null);
		currentStam = stamPack.getStamina();
		currentStamCap = stamPack.getMaxStamina();
	}
}
