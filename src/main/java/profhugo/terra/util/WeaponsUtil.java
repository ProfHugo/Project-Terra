package profhugo.terra.util;

import java.util.UUID;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.google.common.collect.Multimap;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

/**
 * This class provides methods to easily get, and set weapon attributes, such as
 * attack damage and attack speed without the hassle of converting multimap
 * entries to attribute modifiers.
 * 
 * @author ProfHugo
 */
public final class WeaponsUtil {

	// Copied from Item class since ther're protected
	public static final String ATTACK_DAMAGE_KEY = SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName();
	public static final String ATTACK_SPEED_KEY = SharedMonsterAttributes.ATTACK_SPEED.getAttributeUnlocalizedName();
	public static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	public static final UUID ATTACK_SPEED_MODIFIER = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

	private WeaponsUtil() {
	}

	/**
	 * Returns the total attack damage of the itemstack. Returns 1.0D if the
	 * stack is null (not holding any item) or if there's no attack damage
	 * attribute (holding a non-tool item).
	 * 
	 * @author ProfHugo
	 */
	public static double getAttackDamage(@Nullable ItemStack stack, @Nullable EntityEquipmentSlot slot) {
		double attackDamage = 1.0D;
		if (slot == null)
			slot = EntityEquipmentSlot.MAINHAND;
		if (stack != null) {
			Multimap<String, AttributeModifier> weaponAttributes = stack.getItem().getAttributeModifiers(slot, stack);
			if (!weaponAttributes.isEmpty()) {
				for (Entry<String, AttributeModifier> entry : weaponAttributes.entries()) {
					AttributeModifier attributemodifier = (AttributeModifier) entry.getValue();
					double val = attributemodifier.getAmount();
					if (entry.getKey() == ATTACK_DAMAGE_KEY) {
						attackDamage += val;
					}
				}
			}
		}
		return attackDamage;
	}

	/**
	 * Returns the attack speed of the itemstack. Returns 4.0D if the stack is
	 * null (not holding any item) or if there's no attack speed attribute
	 * (holding a non-tool item).
	 * 
	 * @author ProfHugo
	 */
	public static double getAttackSpeed(@Nullable ItemStack stack, @Nullable EntityEquipmentSlot slot) {
		double attackSpeed = 4.0D;
		if (slot == null)
			slot = EntityEquipmentSlot.MAINHAND;
		if (stack != null) {
			Multimap<String, AttributeModifier> weaponAttributes = stack.getItem().getAttributeModifiers(slot, stack);
			if (!weaponAttributes.isEmpty()) {
				for (Entry<String, AttributeModifier> entry : weaponAttributes.entries()) {
					AttributeModifier attributemodifier = (AttributeModifier) entry.getValue();
					double val = attributemodifier.getAmount();
					if (entry.getKey() == ATTACK_SPEED_KEY) {
						attackSpeed += val;
					}
				}
			}
		}
		return attackSpeed;
	}

	/**
	 * Set the TOTAL attack damage of the itemstack. If there wasn't an attack
	 * damage value it would just add one, otherwise it would replace the old
	 * value.
	 * 
	 * Note that in the code, it sets the amount to whatever was passed minus
	 * one. This is to account for the +1 base damage of just hitting something.
	 * 
	 * Example: 7.0D would be converted to 6.0D, and the code would use this
	 * value, since 1.0D + 6.0D is 7.0D, or the total attack damage.
	 * 
	 * @author ProfHugo
	 */
	public static void setAttackDamage(@Nullable ItemStack stack, double amount, @Nullable EntityEquipmentSlot slot) {
		if (slot == null)
			slot = EntityEquipmentSlot.MAINHAND;
		if (stack != null) {
			Multimap<String, AttributeModifier> weaponAttributes = stack.getItem().getAttributeModifiers(slot, stack);
			if (!weaponAttributes.isEmpty()) {
				if (weaponAttributes.containsKey(ATTACK_DAMAGE_KEY))
					weaponAttributes.removeAll(ATTACK_DAMAGE_KEY);
				weaponAttributes.put(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(),
						new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", amount - 1.0D, 0));
			}
		}
	}

	/**
	 * Set the TOTAL attack speed of the itemstack. If there wasn't an attack
	 * speed value it would just add one, otherwise it would replace the old
	 * value.
	 * 
	 * Note that in the code, it converts the amount to a negative modifier in
	 * terms of 4.0D plus modifier.
	 * 
	 * Example: 1.6D would be converted to -2.4D, and the code would use this
	 * value, since 4.0D + (-2.4D) is 1.6D, or the total attack speed.
	 * 
	 * @author ProfHugo
	 */
	public static void setAttackSpeed(@Nullable ItemStack stack, double amount, @Nullable EntityEquipmentSlot slot) {
		if (slot == null)
			slot = EntityEquipmentSlot.MAINHAND;
		if (stack != null) {
			Multimap<String, AttributeModifier> weaponAttributes = stack.getItem().getAttributeModifiers(slot, stack);
			if (!weaponAttributes.isEmpty()) {
				if (weaponAttributes.containsKey(ATTACK_SPEED_KEY))
					weaponAttributes.removeAll(ATTACK_SPEED_KEY);
				weaponAttributes.put(ATTACK_SPEED_KEY,
						new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -(4.0D - amount), 0));
			}
		}
	}
}
