package profhugo.terra.handler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import profhugo.terra.ProjectTerra;
import profhugo.terra.capabilities.IStamina;
import profhugo.terra.capabilities.Stamina;
import profhugo.terra.capabilities.StaminaProvider;
import profhugo.terra.util.WeaponsUtil;

import java.util.Random;

public class EntityHandler {
	public boolean hasWorldLoaded = false;
	public static final ResourceLocation STAMINA_CAP = new ResourceLocation(ProjectTerra.MODID, "stamina");
//	private int rightClickDelay = 0;
//	private int chargeTick = 0;

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onWorldLoad(WorldEvent.Load event) {

		if (hasWorldLoaded)
			return;

	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onUpdate(LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (!(entity instanceof EntityPlayerMP) || entity.getEntityWorld().isRemote)
			return;
		IStamina stamPack = entity.getCapability(StaminaProvider.STAMINA_CAP, null);
		float attackProgress = ((EntityPlayer) entity).getCooledAttackStrength(0);
		if (attackProgress >= 1 && !entity.isSprinting() && (entity.onGround || entity.isElytraFlying())) {
			if (entity.isActiveItemStackBlocking()) {
				stamPack.addStamina(1f);
			} else {
				stamPack.addStamina(3f);
			}

		}
//		ItemStack mainHand = entity.getActiveItemStack();
//		if (mainHand != null) {
//			if (entity.getActiveItemStack().getItemUseAction() == null) {
//				rightClickDelay--;
//			} else {
//				rightClickDelay -= 2;
//			}
//		} else {
//			rightClickDelay--;
//		}

	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntityHurt(LivingHurtEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		DamageSource source = event.getSource();
		if (source == DamageSource.inFire || source == DamageSource.lava || source == DamageSource.cactus
				|| source == DamageSource.lightningBolt) {
			event.setAmount(event.getAmount() / 20);
		}
		entity.hurtResistantTime = 0;
		entity.hurtTime = 1;

	}

	@SubscribeEvent
	public void onBlock(LivingHurtEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (entity == null || entity.getEntityWorld().isRemote || !(entity instanceof EntityPlayer)
				|| !((EntityPlayer) event.getEntity()).isActiveItemStackBlocking())
			return;
		IStamina stamPack = entity.getCapability(StaminaProvider.STAMINA_CAP, null);
		float stamina = stamPack.getStamina();
		float cost = event.getAmount() * 40;
		if (stamina > 0) {
			stamPack.deductStamina(cost);
			String message = String.format("You blocked a hit with %d stamina!", (int) cost);
			entity.addChatMessage(new TextComponentString(message));
		} else {
			String message = String.format("You got guardbroken!", (int) cost);
			entity.addChatMessage(new TextComponentString(message));
			((EntityPlayer) entity).getCooldownTracker().setCooldown(Items.SHIELD, 100);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onAttack(AttackEntityEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (entity.getEntityWorld().isRemote)
			return;
		if (entity instanceof EntityPlayer) {
			IStamina stamPack = entity.getCapability(StaminaProvider.STAMINA_CAP, null);
			float stamina = stamPack.getStamina();
			ItemStack weapon = entity.getHeldItemMainhand();
			float cost = 40;
			Random rng = new Random();
			double attackDamage = WeaponsUtil.getAttackDamage(weapon, EntityEquipmentSlot.MAINHAND);
			double attackSpeed = WeaponsUtil.getAttackSpeed(weapon, EntityEquipmentSlot.MAINHAND);
			float attackProgress = ((EntityPlayer) entity).getCooledAttackStrength(0);
			cost = (float) ((Math.log10(attackSpeed / 14.8)) / Math.log10(0.91));
			if (attackProgress > 0.6f && stamina > 0) {
				cost *= attackProgress;
			} else {
				cost *= 0.6f;
				if (rng.nextBoolean()) {
					entity.addChatMessage(new TextComponentString("The attack is so weak, it got deflected!"));
					event.setCanceled(true);
				}
			}
			float coolDown = ((EntityPlayer) entity).getCooldownTracker().getCooldown(weapon.getItem(), 0);
			if (stamina > 0 && coolDown == 0) {
				stamPack.deductStamina(cost);
				String message = String.format(
						"That attack costed %d stamina, you have %d stamina left. Weapon attack speed is %f and attack damage is %f.",
						(int) cost, (int) stamPack.getStamina(), (float) attackSpeed, (float) attackDamage);
				entity.addChatMessage(new TextComponentString(message));
			} else {
				String message = String.format("You are too tired to attack! You have %d stamina right now!",
						(int) cost);
				entity.addChatMessage(new TextComponentString(message));
				((EntityPlayer) entity).getCooldownTracker().setCooldown(weapon.getItem(), 20);
				event.setCanceled(true);
			}

		}

	}

//	@SubscribeEvent
//	public void onRightClick(PlayerInteractEvent.EntityInteract event) {
//		EntityLivingBase entity = (EntityLivingBase) event.getEntity();
//		EntityLivingBase target = (EntityLivingBase) event.getTarget();
//		if (!(entity instanceof EntityLivingBase) || !(target instanceof EntityLivingBase))
//			return;
//		if (!(entity.getEntityWorld().isRemote) && rightClickDelay <= 0) {
//			target.attackEntityFrom(DamageSource.generic, 4);
//			entity.addChatMessage(new TextComponentString("DING!"));
//			rightClickDelay = 10;
//		}
//	}

	@SubscribeEvent
	public void onPlayerClone(PlayerEvent.Clone event) {
		EntityPlayer player = event.getEntityPlayer();
		IStamina stam = player.getCapability(StaminaProvider.STAMINA_CAP, null);
		stam.setStamina(Stamina.MAX_STAMINA);
	}

}
