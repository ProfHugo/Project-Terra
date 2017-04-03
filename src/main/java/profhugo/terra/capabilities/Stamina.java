package profhugo.terra.capabilities;

public class Stamina implements IStamina {

	public static final float MAX_STAMINA = 100f;
	private float stamina = MAX_STAMINA;

	@Override
	public void deductStamina(float value) {
		this.stamina -= value;

	}

	@Override
	public void addStamina(float value) {
		this.stamina += value;
		if (stamina > MAX_STAMINA)
			stamina = MAX_STAMINA;

	}

	@Override
	public void setStamina(float value) {
		this.stamina = value;

	}

	@Override
	public float getStamina() {
		return this.stamina;
	}

}
