package profhugo.terra.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class StaminaGui extends Gui {

	String text = "Stamina: ";
	Minecraft mc;

	public StaminaGui(Minecraft mc) {
		this.mc = mc;
	}

	public void updateStamina(float stamina, float maxStamina) {
		text = String.format("Stamina: %d/%d", (int) stamina, (int) maxStamina);
		ScaledResolution scaled = new ScaledResolution(mc);
		int width = scaled.getScaledWidth();
		int height = scaled.getScaledHeight();
		this.drawCenteredString(mc.fontRendererObj, text, width / 2, (height / 2) + (height / 4),
				Integer.parseInt("26b52f", 16));
	}

	public void removeGui() {
	 
	}
}
