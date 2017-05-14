package profhugo.terra.handler;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityDragonFireball;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import profhugo.terra.ProjectTerra;
import profhugo.terra.capabilities.IStamina;
import profhugo.terra.capabilities.Stamina;
import profhugo.terra.capabilities.StaminaProvider;
import profhugo.terra.util.WeaponsUtil;

public class EntityHandler {

	public static final ResourceLocation STAMINA_CAP = new ResourceLocation(ProjectTerra.MODID, "stamina");

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onUpdate(LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (!(entity instanceof EntityPlayerMP) || entity.getEntityWorld().isRemote)
			return;
		IStamina stamPack = entity.getCapability(StaminaProvider.STAMINA_CAP, null);
		FoodStats hungerPack = ((EntityPlayer) entity).getFoodStats();
		float hungerMod = (20 - hungerPack.getFoodLevel()) * 2.5f;
		stamPack.setMaxStamina(Stamina.STAMINA_ROOF - hungerMod);
		float attackProgress = ((EntityPlayer) entity).getCooledAttackStrength(0);
		float regenRate = stamPack.getMaxStamina() / 40 - (entity.getTotalArmorValue() / 20);
		ItemStack heldItem = entity.getActiveItemStack();
		if (attackProgress >= 1 && !entity.isSprinting() && (entity.onGround || entity.isElytraFlying())
				&& (heldItem != null ? !heldItem.getItemUseAction().equals(EnumAction.BOW) : true)) {
			if (entity.isActiveItemStackBlocking()) {
				stamPack.addStamina(regenRate / 5);
			} else {
				stamPack.addStamina(regenRate);
			}

		} else if (entity.isSprinting() && stamPack.getStamina() > 0) {
			stamPack.deductStamina(1 - regenRate / 4);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onJump(LivingJumpEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		IStamina stamPack = entity.getCapability(StaminaProvider.STAMINA_CAP, null);
		float cost = (15 - stamPack.getMaxStamina() / 10) + entity.getTotalArmorValue();
		if (!(entity instanceof EntityPlayer) || entity.getEntityWorld().isRemote)
			return;
		if (stamPack.getStamina() > 0 && entity.onGround) {
			stamPack.deductStamina(cost);
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onClientJump(LivingJumpEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (!(entity instanceof EntityPlayer))
			return;
		if (GuiHandler.getLocalStamina()[0] <= 0) {
			entity.motionY *= 0.75;
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onClientUpdate(LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (!(entity instanceof EntityPlayer))
			return;
		if (GuiHandler.getLocalStamina()[0] <= 0) {
			entity.motionX *= 0.75;
			entity.motionZ *= 0.75;
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
		if (target.getEntityWorld().isRemote || source.isDamageAbsolute() || !(target instanceof EntityLivingBase)
				|| !((EntityLivingBase) target).isActiveItemStackBlocking() || attacker == null)
			return;
		Vec3d location = source.getDamageLocation();
		if (location != null) {
			Vec3d vec3d1 = target.getLook(1.0F);
			Vec3d vec3d2 = location.subtractReverse(new Vec3d(target.posX, target.posY, target.posZ)).normalize();
			vec3d2 = new Vec3d(vec3d2.xCoord, 0.0D, vec3d2.zCoord);
			if (vec3d2.dotProduct(vec3d1) >= 0.0D) {
				return;
			}
		}
		IStamina targetStamPack = target.getCapability(StaminaProvider.STAMINA_CAP, null);
		float stamina = targetStamPack.getStamina();
		float cost = 5;
		if (source.isExplosion()) {
			cost = Math.max(100 - 100 / 6 * target.getDistanceToEntity(attacker), 10);
			if (stamina > 0) {
				targetStamPack.deductStamina(cost);
				String message = String.format("You blocked an explosion with %d stamina!", (int) cost);
				target.addChatMessage(new TextComponentString(message));
			} else {
				String message = String.format("You got guardbroken!");
				target.addChatMessage(new TextComponentString(message));
				((EntityPlayer) target).getCooldownTracker().setCooldown(Items.SHIELD, 100);
			}
			return;
		} else if (source.isProjectile()) {
			if (attacker instanceof EntityArrow) {
				cost = (float) (((EntityArrow) attacker).getDamage() > 2.0f ? ((EntityArrow) attacker).getDamage() * 10f : 25f);
				if (stamina > 0) {
					targetStamPack.deductStamina(cost);
					String message = String.format("You blocked an arrow with %d stamina!", (int) cost);
					target.addChatMessage(new TextComponentString(message));
				} else {
					String message = String.format("You got guardbroken!");
					target.addChatMessage(new TextComponentString(message));
					((EntityPlayer) target).getCooldownTracker().setCooldown(Items.SHIELD, 100);
				}
			} else if (attacker instanceof EntityFireball) {
				cost = attacker instanceof EntityLargeFireball ? 50f
						: attacker instanceof EntityDragonFireball ? 60f
								: attacker instanceof EntityWitherSkull ? 40f : 15f;
				if (stamina > 0) {
					targetStamPack.deductStamina(cost);
					String message = String.format("You blocked a fireball with %d stamina!", (int) cost);
					target.addChatMessage(new TextComponentString(message));
				} else {
					String message = String.format("You got guardbroken!");
					target.addChatMessage(new TextComponentString(message));
					((EntityPlayer) target).getCooldownTracker().setCooldown(Items.SHIELD, 100);
				}
			} else {
				cost = 10f;
				if (stamina > 0) {
					targetStamPack.deductStamina(cost);
					String message = String.format("You blocked a projectile with %d stamina!", (int) cost);
					target.addChatMessage(new TextComponentString(message));
				} else {
					String message = String.format("You got guardbroken!");
					target.addChatMessage(new TextComponentString(message));
					((EntityPlayer) target).getCooldownTracker().setCooldown(Items.SHIELD, 100);
				}
			}
			return;
		} else if (attacker instanceof EntityLivingBase) {
			ItemStack weapon = ((EntityLivingBase) attacker).getHeldItemMainhand();
			if (source instanceof EntityDamageSource || source instanceof EntityDamageSourceIndirect) {
				double attackDamage = WeaponsUtil.getAttackDamage(weapon, EntityEquipmentSlot.MAINHAND);
				double attackSpeed = WeaponsUtil.getAttackSpeed(weapon, EntityEquipmentSlot.MAINHAND);
				cost = (float) (weapon != null ? Math.max(attackDamage * Math.pow(5, -attackSpeed + 1) * 3, 5)
						: event.getAmount() * 15);
				cost -= (cost / 2) * (((EntityLivingBase) target).getTotalArmorValue() / 20);
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
			if (attackProgress > 0.4f && stamina > 0) {
				cost *= attackProgress;
			} else if (stamina > 0) {
				cost *= 0.3f;
				stamPack.deductStamina(cost);
				if (rng.nextBoolean() && weapon != null) {
					entity.addChatMessage(new TextComponentString("You swung your weapon too quickly!"));
					event.setCanceled(true);
					return;
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
