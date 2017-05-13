package profhugo.terra.handler;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import profhugo.terra.gui.StaminaGui;

public class GuiHandler {

	StaminaGui stamGui = new StaminaGui(Minecraft.getMinecraft());
	private static float currentStam;
	private static float currentStamCap;

	@SubscribeEvent
	public void onRenderGui(RenderGameOverlayEvent.Post event) {
		if (event.getType() != ElementType.EXPERIENCE)
			return;
		
		stamGui.updateStamina(currentStam, currentStamCap);
	}
	
	public static void syncStamina(float f1, float f2) {
		currentStam = f1;
		currentStamCap = f2;
	}

}
