package profhugo.terra.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class StaminaProvider implements ICapabilitySerializable<NBTBase> {
	@CapabilityInject(IStamina.class)
	public static final Capability<IStamina> STAMINA_CAP = null;

	private IStamina instance = STAMINA_CAP.getDefaultInstance();

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == STAMINA_CAP;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (STAMINA_CAP != null) {
			return STAMINA_CAP.<T>cast(this.instance);
		} else {
			return null;
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