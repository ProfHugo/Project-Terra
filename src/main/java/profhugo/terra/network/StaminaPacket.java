package profhugo.terra.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import profhugo.terra.handler.GuiHandler;

public class StaminaPacket extends PacketBase<StaminaPacket> {

	private float currentStam;
	private float currentStamCap;

	public StaminaPacket() {
	}

	public StaminaPacket(float currentStam, float currentStamCap) {
		this.currentStam = currentStam;
		this.currentStamCap = currentStamCap;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		currentStam = buffer.readFloat();
		currentStamCap = buffer.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		buffer.writeFloat(currentStam);
		buffer.writeFloat(currentStamCap);
	}

	@Override
	public void handleClientSide(StaminaPacket message, EntityPlayer player) {
		Minecraft mainThread = Minecraft.getMinecraft();
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				GuiHandler.syncStamina(message.currentStam, message.currentStamCap);
			}
		});

	}

	@Override
	public void handleServerSide(StaminaPacket Message, EntityPlayer player) {
		return;
	}

}
