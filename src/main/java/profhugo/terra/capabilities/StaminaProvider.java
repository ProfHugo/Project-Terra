package profhugo.terra.capabilities;

import java.util.concurrent.Callable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class StaminaProvider implements ICapabilitySerializable<NBTBase> {
	@CapabilityInject(IStamina.class)
	public static final Capability<IStamina> STAMINA_CAP = null;

	private IStamina instance = STAMINA_CAP.getDefaultInstance();
	private static final StaminaStorage INSTANCE = new StaminaStorage();

	public static void init() {
		CapabilityManager.INSTANCE.register(IStamina.class, INSTANCE, new StaminaFactory());
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == STAMINA_CAP;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return capability == STAMINA_CAP ? STAMINA_CAP.<T>cast(this.instance) : null;
	}

	private static class StaminaFactory implements Callable<IStamina> {
		@Override
		public IStamina call() throws Exception {
			return new Stamina();
		}
	}

	@Override
	public NBTBase serializeNBT() {
		return STAMINA_CAP.getStorage().writeNBT(STAMINA_CAP, this.instance, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
		STAMINA_CAP.getStorage().readNBT(STAMINA_CAP, this.instance, null, nbt);
	}
}