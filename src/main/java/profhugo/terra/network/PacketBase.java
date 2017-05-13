package profhugo.terra.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import profhugo.terra.ProjectTerra;

public abstract class PacketBase<RequestBase extends IMessage>
		implements IMessage, IMessageHandler<RequestBase, IMessage> {

	public PacketBase() {
	}

	@Override
	public void fromBytes(ByteBuf buf) {
	}

	@Override
	public void toBytes(ByteBuf buf) {
	}

	@Override
	public IMessage onMessage(RequestBase message, MessageContext ctx) {
		if (ctx.side.equals(Side.SERVER)) {
			handleServerSide(message, ctx.getServerHandler().playerEntity);
		} else {
			handleClientSide(message, ProjectTerra.proxy.getClientPlayer());
		}
		return null;
	}

	public abstract void handleClientSide(RequestBase message, EntityPlayer player);

	public abstract void handleServerSide(RequestBase message, EntityPlayer player);

}
