package profhugo.terra.capabilities;

public interface IStamina {
	public void deductStamina(float value);

	public void addStamina(float value);

	public void setStamina(float value);

	public float getStamina();

	public float getMaxStamina();
	
	public void addMaxStamina(float value);
	
	public void deductMaxStamina(float value);

	public void setMaxStamina(float value);
}
