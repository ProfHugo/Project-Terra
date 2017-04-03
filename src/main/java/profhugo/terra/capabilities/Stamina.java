package profhugo.terra.capabilities;

public class Stamina implements IStamina {

	public static final float STAMINA_ROOF = 100f;
	private float staminaCap = STAMINA_ROOF;
	private float stamina = staminaCap;

	@Override
	public void deductStamina(float value) {
		this.stamina -= value;

	}

	@Override
	public void addStamina(float value) {
		this.stamina += value;
		if (stamina > staminaCap)
			stamina = staminaCap;

	}

	@Override
	public void setStamina(float value) {
		this.stamina = value;

	}

	@Override
	public float getStamina() {
		return this.stamina;
	}

	@Override
	public float getMaxStamina() {
		return this.staminaCap;
	}

	@Override
	public void addMaxStamina(float value) {
		this.staminaCap += value;
		
	}

	@Override
	public void deductMaxStamina(float value) {
		this.staminaCap -= value;
		
	}

	@Override
	public void setMaxStamina(float value) {
		this.staminaCap = value;
		
	}

}
