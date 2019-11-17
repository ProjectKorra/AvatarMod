package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.damageutils.DamageUtils;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityShockwave;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.ShockwaveBehaviour;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.avatar.common.util.AvatarEntityUtils;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_LEFT_CLICK;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)

public class StatCtrlInfernoPunchFirst extends StatusControl {
	private ParticleSpawner particleSpawner;

	public StatCtrlInfernoPunchFirst() {
		super(18, CONTROL_LEFT_CLICK, CrosshairPosition.LEFT_OF_CROSSHAIR);
		particleSpawner = new NetworkParticleSpawner();
	}

	//TODO: Spawn a schockwave.
	@Override
	public boolean execute(BendingContext ctx) {
		//TODO: Raytrace instead of event
		EntityLivingBase entity = ctx.getBenderEntity();
		HashSet<Entity> excluded = new HashSet<>();
		AbilityData abilityData = ctx.getData().getAbilityData("inferno_punch");
		BendingData data = ctx.getData();
		World world = ctx.getWorld();

		double reach = Raytrace.getReachDistance(entity);
		float powerModifier = (float) (ctx.getBender().getDamageMult(Firebending.ID));
		float damage = STATS_CONFIG.infernoPunchSettings.damage * 7 / 3 * powerModifier;
		int performance = (int) (2.5 * STATS_CONFIG.infernoPunchSettings.performanceAmount);
		float knockBack = 2 * STATS_CONFIG.infernoPunchSettings.knockbackMult * powerModifier;
		int fireTime = 4 * STATS_CONFIG.infernoPunchSettings.fireTime + (int) (powerModifier * 10);
		float shockwaveDamage = STATS_CONFIG.infernoPunchSettings.shockwaveDamage * powerModifier;
		float shockwaveRadius = STATS_CONFIG.infernoPunchSettings.shockwaveRadius * powerModifier;
		float shockwaveSpeed = STATS_CONFIG.infernoPunchSettings.shockwaveSpeed * 0.6F * powerModifier;

		if (entity.isPotionActive(MobEffects.STRENGTH)) {
			damage += (Objects.requireNonNull(entity.getActivePotionEffect(MobEffects.STRENGTH)).getAmplifier() + 1) / 2F;
		}

		excluded.add(entity);
		Vec3d startPos = entity.getPositionVector().add(0, entity.getEyeHeight(), 0);

		if (entity.getHeldItemMainhand() == ItemStack.EMPTY) {
			//Bounding Box to determine excluded entities
			AxisAlignedBB detectionBox = new AxisAlignedBB(entity.posX + reach, entity.posY + reach, entity.posZ + reach, entity.posX - reach,
					entity.posY - reach, entity.posZ - reach);
			List<Entity> exclude = entity.world.getEntitiesWithinAABB(Entity.class, detectionBox);
			if (!exclude.isEmpty()) {
				for (Entity detected : exclude) {
					if (detected instanceof AvatarEntity) {
						if (((AvatarEntity) detected).getOwner() == entity) {
							excluded.add(detected);
						}
					}
					if (detected.getTeam() != null && detected.getTeam() == entity.getTeam()) {
						excluded.add(detected);
					}
					if (detected.getControllingPassenger() == entity) {
						excluded.add(detected);
					}
				}
				RayTraceResult result = Raytrace.standardEntityRayTrace(entity.world, entity,
						null, startPos, startPos.add(entity.getLookVec().scale(5)), 0.2F, false, Raytrace.ignoreBenderFilter(entity));
				if (result != null) {
					if (result.entityHit != null) {
						Entity hit = result.entityHit;
						if (canCollideWith(entity)) {
							if (!world.isRemote) {
								DamageUtils.attackEntity(entity, hit, AvatarDamageSource.causeInfernoPunchDamage(entity), damage,
										performance, new AbilityInfernoPunch(), 0);
								Vec3d direction = entity.getLookVec();
								double x = 0.5 * direction.x * knockBack;
								double y = 0.5 * direction.y * knockBack + 0.15;
								double z = 0.5 * direction.z * knockBack;
								hit.setFire(fireTime);
								hit.addVelocity(x, y, z);
								AvatarUtils.afterVelocityAdded(hit);
							}

							Vec3d particlePos = hit.getPositionVector().add(0, hit.height / 2, 0);
							if (world instanceof WorldServer) {
								WorldServer World = (WorldServer) entity.world;
								World.spawnParticle(AvatarParticles.getParticleFlames(), true, particlePos.x, particlePos.y, particlePos.z, 60,
										0, 0, 0, 0.02);
							}
							EntityShockwave wave = new EntityShockwave(world);
							wave.setPosition(AvatarEntityUtils.getBottomMiddleOfEntity(hit));
							wave.setOwner(entity);
							wave.setBehaviour(new InfernoPunchShockwave());
							wave.setDamage(shockwaveDamage);
							wave.setRange(shockwaveRadius);
							wave.setSpeed(shockwaveSpeed);
							wave.setFireTime(fireTime);
							wave.setDamageSource(AvatarDamageSource.FIRE);
							wave.setKnockbackMult(new Vec3d(knockBack, knockBack, knockBack));
							wave.setKnockbackHeight(0.05);
							wave.setAbility(new AbilityInfernoPunch());
							wave.setElement(new Firebending());
							wave.setVelocity(Vector.ZERO);
							if (!world.isRemote)
								world.spawnEntity(wave);

							return true;
						}

					}
				}

			}

		}
		return false;
	}

	public boolean canCollideWith(Entity entity) {
		if (entity instanceof EntityEnderCrystal) {
			return true;
		} else
			return (entity.canBePushed() && entity.canBeCollidedWith()) || entity instanceof EntityLivingBase;
	}

	public static class InfernoPunchShockwave extends ShockwaveBehaviour {

		@Override
		public Behavior onUpdate(EntityShockwave entity) {
			World world = entity.world;
			if (world.isRemote) {

			}
			return this;
		}

		@Override
		public void fromBytes(PacketBuffer buf) {

		}

		@Override
		public void toBytes(PacketBuffer buf) {

		}

		@Override
		public void load(NBTTagCompound nbt) {

		}

		@Override
		public void save(NBTTagCompound nbt) {

		}
	}
}
