package profhugo.terra.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import profhugo.terra.handler.GuiHandler;

public class StaminaGui extends GuiIngame {

	String text = "Stamina: ";
	Minecraft mc;

	public StaminaGui(Minecraft mc) {
		super(mc);
		this.mc = mc;
	}

	public void updateStaminaText(float stamina, float maxStamina) {
		text = String.format("Stamina: %d/%d", (int) stamina, (int) maxStamina);
//		if (mc.thePlayer.capabilities.isCreativeMode) 
//				return;
		ScaledResolution scaled = new ScaledResolution(mc);
		int width = scaled.getScaledWidth();
		int height = scaled.getScaledHeight();
		this.drawCenteredString(mc.fontRendererObj, text, width / 2, (height / 2) + (height / 4),
				Integer.parseInt("26b52f", 16));
	}

	public void updateStaminaBar(float stamina, float maxStamina) {
		if (mc.thePlayer.capabilities.isCreativeMode) 
			return;
		ScaledResolution scaled = new ScaledResolution(mc);
		int bar1Width = (int) (GuiHandler.getLocalStamina()[1] * 0.8);
		int bar1Height = 12;
		int width = scaled.getScaledWidth();
		int height = scaled.getScaledHeight();
		int bar1x = width / 2 - 40;
		int bar1y = (int) height / 2 + height / 4 + bar1Height;
		mc.renderEngine.bindTexture(new ResourceLocation("project_terra", "green_bar.png"));
		this.drawTexturedModalRect(bar1x, bar1y - 2, 160, 20, bar1Width, bar1Height);
		this.drawTexturedModalRect(bar1x + 2, bar1y, 1, 1, bar1Width - 4, bar1Height - 4);
		//mc.renderEngine.bindTexture(new ResourceLocation("project_terra", "gray_bar.png"));
		//gameUI.drawTexturedModalRect(x, y - 50, 1, 1, (int) (GuiHandler.getLocalStamina()[1] * 0.8), 8);
	}

	public void removeGui() {
		
	}
}
