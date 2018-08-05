package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.avatar.common.world.AvatarFireExplosion;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_LEFT_CLICK;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)

public class StatCtrlInfernoPunch extends StatusControl {
	public StatCtrlInfernoPunch() {
		super(15, CONTROL_LEFT_CLICK, CrosshairPosition.LEFT_OF_CROSSHAIR);
	}


	@Override
	public boolean execute(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = ctx.getWorld();
		Bender bender = ctx.getBender();
		AbilityData abilityData = ctx.getData().getAbilityData("inferno_punch");
		int i = 0;

		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
			float damageModifier = (float) (bender.calcPowerRating(Firebending.ID) / 100);
			float damage = STATS_CONFIG.InfernoPunchDamage * 1.5F + (2 * damageModifier);
			float knockBack = 0.75F;
			int fireTime = 4;
			Vector direction = Vector.getLookRectangular(entity);
			List<Entity> hit = Raytrace.entityRaytrace(world, Vector.getEyePos(entity), Vector.getLookRectangular(entity).times(1.5), 8, entity1 -> entity1 != entity);
			if (!hit.isEmpty()) {
				for (Entity e : hit) {
					if (!(e instanceof EntityItem) && !(e instanceof EntityXPOrb) && entity.getHeldItemMainhand() == ItemStack.EMPTY) {
						if (world instanceof WorldServer) {
							WorldServer World = (WorldServer) e.getEntityWorld();
							World.spawnParticle(EnumParticleTypes.FLAME, e.posX, e.posY + e.getEyeHeight(), e.posZ, 50, 0.05, 0.05, 0.05, 0.05);

						}
						world.playSound(null, e.posX, e.posY, e.posZ, SoundEvents.ENTITY_GHAST_SHOOT,
								SoundCategory.HOSTILE, 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
						AxisAlignedBB box = new AxisAlignedBB(e.posX + 2, e.posY + 2, e.posZ + 2, e.posX - 2, e.posY - 2, e.posZ - 2);
						List<Entity> nearby = world.getEntitiesWithinAABB(Entity.class, box);
						if (!nearby.isEmpty()) {
							for (Entity living : nearby) {
								if (living != entity && !(e instanceof EntityItem) && !(e instanceof EntityXPOrb)) {
									if (world instanceof WorldServer) {
										WorldServer World = (WorldServer) e.getEntityWorld();
										World.spawnParticle(EnumParticleTypes.FLAME, living.posX, living.posY + living.getEyeHeight(), living.posZ, 50, 0.05, 0.05, 0.05, 0.01);

									}
									living.attackEntityFrom(AvatarDamageSource.causeFireDamage(living, entity), damage - (i / 2));
									living.setFire(fireTime - (i / 2));
									living.motionX += direction.x() * (knockBack - (i / 2));
									living.motionY += direction.y() * knockBack >= 0 ? knockBack / 2 + (direction.y() * knockBack / 2) : knockBack / 2;
									living.motionZ += direction.z() * knockBack;
									living.isAirBorne = true;
									// this line is needed to prevent a bug where players will not be pushed in multiplayer
									AvatarUtils.afterVelocityAdded(e);
									i++;

								}
							}
						}

						e.attackEntityFrom(AvatarDamageSource.causeFireDamage(e, entity), 10000000);//damage - (i / 2));
						e.setFire(fireTime - (i / 2));
						e.motionX += direction.x() * (knockBack - (i / 2));
						e.motionY += direction.y() * knockBack >= 0 ? knockBack / 2 + (direction.y() * knockBack / 2) : knockBack / 2;
						e.motionZ += direction.z() * knockBack;
						e.isAirBorne = true;
						// this line is needed to prevent a bug where players will not be pushed in multiplayer
						AvatarUtils.afterVelocityAdded(e);
						i++;

					}
				}
				return true;
			}

		}
		return false;

	}


	@SubscribeEvent
	public static void onInfernoPunch(LivingAttackEvent event) {
		EntityLivingBase entity = (EntityLivingBase) event.getSource().getTrueSource();
		Entity target = event.getEntity();
		DamageSource source = event.getSource();
		World world = target.getEntityWorld();
		if (event.getSource().getTrueSource() == entity && (entity instanceof EntityBender || entity instanceof EntityPlayer)) {
			Bender ctx = Bender.get(entity);
			if (ctx.getData() != null) {
				Vector direction = Vector.getLookRectangular(entity);
				AbilityData abilityData = ctx.getData().getAbilityData("inferno_punch");
				float knockBack = 1F;
				int fireTime = 5;
				float damageModifier = (float) (ctx.calcPowerRating(Firebending.ID) / 100);
				float damage = STATS_CONFIG.InfernoPunchDamage + (2 * damageModifier);

				if (abilityData.getLevel() >= 1) {
					damage = 4 + (2 * damageModifier);
					knockBack = 1.125F;
					fireTime = 6;
				} else if (abilityData.getLevel() >= 2) {
					damage = 5 + (2 * damageModifier);
					knockBack = 1.25F;
					fireTime = 8;
				}
				if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
					damage = 10 + (2 * damageModifier);
					knockBack = 1.5F;
					fireTime = 15;
				}
				if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
					damage = STATS_CONFIG.InfernoPunchDamage * 1.333F + (2 * damageModifier);
					knockBack = 0.75F;
					fireTime = 4;
				}
				if (ctx.getData().hasStatusControl(INFERNO_PUNCH)) {
					if (entity.getHeldItemMainhand() == ItemStack.EMPTY && !(source.getDamageType().equals("avatar_groundSmash"))) {
						if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
							BlockPos blockPos = target.getPosition();
							AvatarFireExplosion fireExplosion = new AvatarFireExplosion(target.world, target, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 3F, true, false);
							fireExplosion.doExplosionA();
							if (world instanceof WorldServer) {
								WorldServer World = (WorldServer) target.getEntityWorld();
								World.spawnParticle(EnumParticleTypes.FLAME, target.posX, target.posY, target.posZ, 200, 0.05, 0.05, 0.05, 0.75);
								fireExplosion.doExplosionB(true);
							}
						}
						if (world instanceof WorldServer) {
							WorldServer World = (WorldServer) target.getEntityWorld();
							World.spawnParticle(EnumParticleTypes.FLAME, target.posX, target.posY + target.getEyeHeight(), target.posZ, 50, 0.05, 0.05, 0.05, 0.05);

						}

						world.playSound(null, target.posX, target.posY, target.posZ, SoundEvents.ENTITY_GHAST_SHOOT,
								SoundCategory.HOSTILE, 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);

						if ((!(target instanceof EntityItem) && !(target instanceof EntityXPOrb))) {
							target.attackEntityFrom(DamageSource.IN_FIRE, damage);
							target.setFire(fireTime);
							;
						}

						target.motionX += direction.x() * knockBack;
						target.motionY += direction.y() * knockBack >= 0 ? knockBack / 2 + (direction.y() * knockBack / 2) : knockBack / 2;
						target.motionZ += direction.z() * knockBack;
						target.isAirBorne = true;
						// this line is needed to prevent a bug where players will not be pushed in multiplayer
						AvatarUtils.afterVelocityAdded(target);
						if (!(target instanceof EntityDragon)) {
							ctx.getData().removeStatusControl(INFERNO_PUNCH);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onDragonHurt(LivingHurtEvent event) {
		EntityLivingBase entity = (EntityLivingBase) event.getSource().getTrueSource();
		Entity target = event.getEntity();
		if (entity instanceof EntityPlayer || entity instanceof EntityBender) {
			BendingData data = BendingData.get(entity);
			if (data != null) {
				AbilityData aD = AbilityData.get(entity, "inferno_punch");
				Bender ctx = Bender.get(entity);
				float damageModifier = (float) (ctx.calcPowerRating(Firebending.ID) / 100);
				float damage = STATS_CONFIG.InfernoPunchDamage + (2 * damageModifier);
				if (data.hasStatusControl(INFERNO_PUNCH) && !(event.getSource().getDamageType().equals("avatar_groundSmash")) &&
						!(event.getSource().getDamageType().equals("avatar_Air"))) {
					if (entity.getHeldItemMainhand() == ItemStack.EMPTY) {
						if (aD.getLevel() >= 1) {
							damage = 4 + (2 * damageModifier);
						} else if (aD.getLevel() >= 2) {
							damage = 5 + (2 * damageModifier);
						}
						if (aD.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
							damage = 10 + (2 * damageModifier);
						}
						if (aD.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
							damage = STATS_CONFIG.InfernoPunchDamage * 1.333F + (2 * damageModifier);
						}
						if (target instanceof EntityDragon) {
							event.setAmount(damage);
							data.removeStatusControl(INFERNO_PUNCH);
						}
					}
				}
			}
		}
	}
}
