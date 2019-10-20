package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.TickHandlerController;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_LEFT_CLICK;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class StatCtrlInfernoPunchSecond extends StatusControl {
	private ParticleSpawner particleSpawner;
	private Map<String, Integer> timesPunched = new HashMap<>();

	public StatCtrlInfernoPunchSecond() {
		super(18, CONTROL_LEFT_CLICK, CrosshairPosition.LEFT_OF_CROSSHAIR);
		particleSpawner = new NetworkParticleSpawner();
	}

	@Override
	public boolean execute(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = ctx.getWorld();
		Bender bender = ctx.getBender();
		AbilityData abilityData = ctx.getData().getAbilityData("inferno_punch");
		String uuid = Objects.requireNonNull(bender.getInfo().getId()).toString();

		if (!timesPunched.containsKey(uuid)) timesPunched.put(uuid, 0);

		int timesPunchedInt = timesPunched.get(uuid);
		HashSet<Entity> excluded = new HashSet<>();
		if (!ctx.getData().hasTickHandler(TickHandlerController.INFERNO_PUNCH_COOLDOWN)) {
			float damageModifier = (float) (bender.getDamageMult(Firebending.ID));
			float xpMod = abilityData.getTotalXp() / 100;
			float damage = STATS_CONFIG.infernoPunchSettings.damage * 1.5F * damageModifier * xpMod;
			float knockBack = STATS_CONFIG.infernoPunchSettings.knockbackMult * damageModifier * xpMod;
			int fireTime = (int) (STATS_CONFIG.infernoPunchSettings.fireTime * damageModifier * xpMod);
			Vector direction = Vector.getLookRectangular(entity);
			RayTraceResult result = AvatarUtils.standardEntityRayTrace(world, entity, null, Vector.getEyePos(entity).toMinecraft(),
					entity.getLookVec().scale(10).add(entity.getPositionVector()), 0.25F, false, excluded);
			if (result != null) {
				if (result.entityHit != null) {
					Entity e = result.entityHit;
					if (e != entity && canDamageEntity(e) && entity.getHeldItemMainhand() == ItemStack.EMPTY) {
						if (world instanceof WorldServer) {
							WorldServer World = (WorldServer) e.getEntityWorld();
							//Spawns the particles in a line towards where the player is looking
							double dist = entity.getDistance(e);
							for (double j = 0; j < 1; j += 1 / dist) {
								Vector startPos = Vector.getEyePos(entity).minusY(0.25);
								Vector distance = new Vector(result.hitVec.x, e.getEntityBoundingBox().minY + e.height / 2, result.hitVec.z).minus(startPos);
								distance = distance.times(j);
								particleSpawner.spawnParticles(world, AvatarParticles.getParticleFlames(), 4, 8,
										startPos.x() + distance.x(), startPos.y() + distance.y(), startPos.z() + distance.z(), 0, 0, 0, true);
							}
							//Spawns particles as if a fireball has slammed into the enemy
							World.spawnParticle(AvatarParticles.getParticleFlames(), result.hitVec.x, result.hitVec.y, result.hitVec.z, 35, 0.05, 0.05, 0.05, 0.075);

						}
						world.playSound(null, e.posX, e.posY, e.posZ, SoundEvents.ENTITY_GHAST_SHOOT,
								SoundCategory.HOSTILE, 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);

						e.attackEntityFrom(AvatarDamageSource.causeFireDamage(e, entity), damage + (timesPunchedInt / 2F));
						e.setFire(fireTime);
						e.motionX += direction.x() * knockBack;
						e.motionY += direction.y() * knockBack >= 0 ? (direction.y() * (knockBack / 8)) : knockBack / 8;
						e.motionZ += direction.z() * knockBack;
						e.isAirBorne = true;
						// this line is needed to prevent a bug where players will not be pushed in multiplayer
						AvatarUtils.afterVelocityAdded(e);
						timesPunchedInt++;
						timesPunched.replace(uuid, timesPunchedInt);
						ctx.getData().addTickHandler(TickHandlerController.INFERNO_PUNCH_COOLDOWN);
						AxisAlignedBB box = new AxisAlignedBB(e.posX + 2, e.posY + 2, e.posZ + 2, e.posX - 2, e.posY - 2, e.posZ - 2);
						List<Entity> nearby = world.getEntitiesWithinAABB(Entity.class, box);
						if (!nearby.isEmpty()) {
							for (Entity living : nearby) {
								if (living != entity && canDamageEntity(living) && e != living) {
									if (world instanceof WorldServer) {
										WorldServer World = (WorldServer) e.getEntityWorld();
										World.spawnParticle(AvatarParticles.getParticleFlames(), living.posX, living.posY + living.getEyeHeight(), living.posZ, 50, 0.05, 0.05, 0.05, 0.01);

									}
									living.attackEntityFrom(AvatarDamageSource.causeFireDamage(living, entity), damage + (timesPunchedInt / 2F));
									living.setFire(fireTime + (timesPunchedInt / 2));
									living.motionX += direction.x() * (knockBack + (timesPunchedInt / 2F));
									living.motionY += direction.y() * knockBack >= 0 ? (direction.y() * (knockBack / 10)) : knockBack / 10;
									living.motionZ += direction.x() * (knockBack + (timesPunchedInt / 2F));
									living.isAirBorne = true;
									// this line is needed to prevent a bug where players will not be pushed in multiplayer
									AvatarUtils.afterVelocityAdded(living);

								}
							}
						}
					}

				}
			}
			boolean isDone = timesPunchedInt > 2;
			if (isDone) timesPunched.replace(uuid, 0);
			return isDone;

		}
		return false;

	}

	private boolean canDamageEntity(Entity entity) {
		if (entity instanceof AvatarEntity && ((AvatarEntity) entity).getOwner() != entity) {
			return false;
		}
		if (entity instanceof EntityHanging || entity instanceof EntityXPOrb || entity instanceof EntityItem ||
				entity instanceof EntityArmorStand || entity instanceof EntityAreaEffectCloud) {
			return false;
		} else return entity.canBeCollidedWith() && entity.canBePushed() || entity instanceof EntityLivingBase;
	}
}
