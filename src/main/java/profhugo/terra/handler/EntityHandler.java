package profhugo.terra.handler;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
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

public class EntityHandler {
	public boolean hasWorldLoaded = false;
	public static final ResourceLocation STAMINA_CAP = new ResourceLocation(ProjectTerra.MODID, "stamina");

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
		FoodStats hungerPack = ((EntityPlayer) entity).getFoodStats();
		float hungerMod =  (20 - hungerPack.getFoodLevel()) * 2.5f;
		stamPack.setMaxStamina(Stamina.STAMINA_ROOF - hungerMod);
		float attackProgress = ((EntityPlayer) entity).getCooledAttackStrength(0);
		float regenRate = stamPack.getMaxStamina() / 40 - (entity.getTotalArmorValue() / 20);
		ItemStack heldItem = entity.getActiveItemStack();
		if (attackProgress >= 1 && !entity.isSprinting() && (entity.onGround || entity.isElytraFlying())
				&& (heldItem != null ? !heldItem.getItemUseAction().equals(EnumAction.BOW) : true)) {
			if (entity.isActiveItemStackBlocking()) {
				stamPack.addStamina(regenRate / 3);
			} else {
				stamPack.addStamina(regenRate);
			}

		} else {
			stamPack.addStamina(0);
		}

	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onBowDrawTick(ArrowNockEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (event.getWorld().isRemote)
			return;
		IStamina stamPack = entity.getCapability(StaminaProvider.STAMINA_CAP, null);
		float stamina = stamPack.getStamina();
		if (stamina <= 0) {
			String message = String.format("You are too tired to draw!");
			entity.addChatMessage(new TextComponentString(message));
			event.setCanceled(true);
		}

	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onBowShootTick(ArrowLooseEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (event.getWorld().isRemote)
			return;
		IStamina stamPack = entity.getCapability(StaminaProvider.STAMINA_CAP, null);
		float stamina = stamPack.getStamina();
		float cost = Math.min(event.getCharge() * 0.75f, 35);
		if (stamina > 0) {
			stamPack.deductStamina(cost);
			String message = String.format("You shot an arrow using %d stamina!", (int) cost);
			entity.addChatMessage(new TextComponentString(message));
		} else {
			String message = String.format("You are too tired to release the arrow!");
			entity.addChatMessage(new TextComponentString(message));
			event.setCharge(0);
			event.setCanceled(true);
		}

	}

	@SubscribeEvent
	public void onHandRender(RenderSpecificHandEvent event) {
		if (event.getHand().equals(EnumHand.OFF_HAND))
			return;
	}

	// @SubscribeEvent
	// public void onPlayerRender(RenderPlayerEvent.Pre event) {
	// Just an idea, reserved for blue
	// EntityPlayer player = event.getEntityPlayer();
	// AxisAlignedBB box = new AxisAlignedBB(player.posX - 5, player.posY - 5,
	// player.posZ - 5, player.posX + 5,
	// player.posY + 5, player.posZ + 5);
	// List<Entity> nearbyEntities =
	// player.getEntityWorld().getEntitiesWithinAABBExcludingEntity(player,
	// box);
	// for (Entity e : nearbyEntities) {
	// if (e instanceof EntityLivingBase) {
	// return;
	// }
	// }
	// event.setCanceled(true);
	// }

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onUpdateTwo(LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (!(entity instanceof EntityPlayerMP) || entity.getEntityWorld().isRemote)
			return;

	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntityHurt(LivingHurtEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		DamageSource source = event.getSource();
		if (source.equals(DamageSource.inFire) || source.equals(DamageSource.lava) || source.equals(DamageSource.cactus)
				|| source.equals(DamageSource.lightningBolt) || source.equals(DamageSource.inWall)) {
			event.setAmount(event.getAmount() / 20);
		}
		entity.hurtResistantTime = 0;
		entity.hurtTime = 1;

	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onAttackBlocked(LivingHurtEvent event) {
		DamageSource source = event.getSource();
		Entity target = event.getEntity();
		Entity attacker = source.getSourceOfDamage();
		if (target.getEntityWorld().isRemote || !(target instanceof EntityLivingBase)
				|| !((EntityLivingBase) target).isActiveItemStackBlocking() || attacker == null)
			return;
		IStamina targetStamPack = target.getCapability(StaminaProvider.STAMINA_CAP, null);
		float stamina = targetStamPack.getStamina();
		float cost = 5;
		if (attacker instanceof EntityArrow) {
			cost = 15f;
			if (stamina > 0) {
				targetStamPack.deductStamina(cost);
				String message = String.format("You blocked an arrow with %d stamina!", (int) cost);
				target.addChatMessage(new TextComponentString(message));
			} else {
				String message = String.format("You got guardbroken!");
				target.addChatMessage(new TextComponentString(message));
				((EntityPlayer) target).getCooldownTracker().setCooldown(Items.SHIELD, 100);
			}
			return;
		} else if (attacker instanceof EntityLivingBase) {
			ItemStack weapon = ((EntityLivingBase) attacker).getHeldItemMainhand();
			if (source instanceof EntityDamageSource || source instanceof EntityDamageSourceIndirect) {
				double attackDamage = WeaponsUtil.getAttackDamage(weapon, EntityEquipmentSlot.MAINHAND);
				double attackSpeed = WeaponsUtil.getAttackSpeed(weapon, EntityEquipmentSlot.MAINHAND);
				cost = (float) (attackDamage * Math.pow(5, -attackSpeed + 1) * 3);
			}
			if (stamina > 0) {
				targetStamPack.deductStamina(cost);
				String message = String.format("You blocked a hit with %d stamina!", (int) cost);
				target.addChatMessage(new TextComponentString(message));
			} else {
				String message = String.format("You got guardbroken!");
				target.addChatMessage(new TextComponentString(message));
				((EntityPlayer) target).getCooldownTracker().setCooldown(Items.SHIELD, 100);
			}
			return;
		} else {
			cost = event.getAmount() * 2;
			if (stamina > 0) {
				targetStamPack.deductStamina(cost);
				String message = String.format("You blocked a hit from the enviroment with %d stamina!", (int) cost);
				target.addChatMessage(new TextComponentString(message));
			} else {
				String message = String.format("You got guardbroken!");
				target.addChatMessage(new TextComponentString(message));
				((EntityPlayer) target).getCooldownTracker().setCooldown(Items.SHIELD, 100);
			}
		}

	}

	@SubscribeEvent(priority = EventPriority.LOW)
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
			double attackSpeed = WeaponsUtil.getAttackSpeed(weapon, EntityEquipmentSlot.MAINHAND);
			float attackProgress = ((EntityPlayer) entity).getCooledAttackStrength(0);
			cost = (float) ((Math.log10(attackSpeed / 14.8)) / Math.log10(0.91));
			if (attackProgress > 0.6f && stamina > 0) {
				cost *= attackProgress;
			} else {
				cost *= 0.6f;
				if (rng.nextBoolean() && weapon != null) {
					entity.addChatMessage(
							new TextComponentString("You swung your weapon too quickly, you cut yourself with it!"));
					entity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) entity), rng.nextInt(4));
				}
			}
			if (stamina > 0) {
				stamPack.deductStamina(cost);
				String message = String.format("You attacked with %d stamina.", (int) cost,
						(int) stamPack.getStamina());
				entity.addChatMessage(new TextComponentString(message));
			} else {
				String message = String.format("You are too tired to attack!", (int) cost);
				entity.addChatMessage(new TextComponentString(message));
				((EntityPlayer) entity).getCooldownTracker().setCooldown(weapon.getItem(), 20);
				event.setCanceled(true);
			}

		}

	}

	@SubscribeEvent
	public void onPlayerClone(PlayerEvent.Clone event) {
		EntityPlayer player = event.getEntityPlayer();
		IStamina stam = player.getCapability(StaminaProvider.STAMINA_CAP, null);
		stam.setMaxStamina(Stamina.STAMINA_ROOF);
		stam.setStamina(stam.getMaxStamina());
	}

}
