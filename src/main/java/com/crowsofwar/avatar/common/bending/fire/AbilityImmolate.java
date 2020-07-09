package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityLightOrb;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.LightOrbBehavior;
import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.TickHandlerController.PURIFY_PARTICLE_SPAWNER;
import static net.minecraft.init.MobEffects.HEALTH_BOOST;
import static net.minecraft.init.MobEffects.STRENGTH;

public class AbilityImmolate extends Ability {

	public AbilityImmolate() {
		super(Firebending.ID, "immolate");
	}

	@Override
	public boolean isBuff() {
		return true;
	}

	@Override
	public void execute(AbilityContext ctx) {

		BendingData data = ctx.getData();
		EntityLivingBase entity = ctx.getBenderEntity();
		Bender bender = ctx.getBender();
		World world = ctx.getWorld();
		AbilityData abilityData = data.getAbilityData(this);

		float chi = STATS_CONFIG.chiBuff;
		if (abilityData.getLevel() == 1) {
			chi = STATS_CONFIG.chiBuffLvl2;
		} else if (abilityData.getLevel() == 2) {
			chi = STATS_CONFIG.chiBuffLvl3;
		} else if (abilityData.getLevel() == 3) {
			chi = STATS_CONFIG.chiBuffLvl4;
		}

		if (bender.consumeChi(chi)) {

			// 3s base + 2s per level
			int duration = abilityData.getLevel() > 0 ? 60 + 40 * abilityData.getLevel() : 60;
			int lightRadius = 5;
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				duration = 400;
				lightRadius = 9;
			}

			int effectLevel = abilityData.getLevel() >= 2 ? 1 : 0;
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				effectLevel = 2;
				lightRadius = 12;
			}

			entity.addPotionEffect(new PotionEffect(STRENGTH, duration, effectLevel + 1));

			if (abilityData.getLevel() < 2) {
				entity.setFire(1);
			}

			if (abilityData.getLevel() >= 1) {
				entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, duration, effectLevel));
				lightRadius = 7;
			}

			if (abilityData.getLevel() >= 2) {
				lightRadius = 10;
			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				entity.addPotionEffect(new PotionEffect(HEALTH_BOOST, duration, effectLevel));
			}

			if (data.hasBendingId(getBendingId())) {

				ImmolatePowerModifier modifier = new ImmolatePowerModifier();
				modifier.setTicks(duration);

				// Ignore warning; we know manager != null if they have the bending style
				//noinspection ConstantConditions
				data.getPowerRatingManager(getBendingId()).addModifier(modifier, ctx);

			}

			EntityLightOrb orb = new EntityLightOrb(world);
			orb.setOwner(entity);
			orb.setAbility(this);
			orb.setPosition(new Vec3d(entity.posX, entity.getEntityBoundingBox().minY + entity.height / 2, entity.posZ));
			orb.setOrbSize(0.005F);
			orb.setLifeTime(duration);
			orb.setColor(1F, 0.5F, 0F, 3F);
			orb.setLightRadius(lightRadius);
			orb.setEmittingEntity(entity);
			orb.setBehavior(new ImmolateLightOrbBehaviour());
			orb.setType(EntityLightOrb.EnumType.COLOR_CUBE);
			world.spawnEntity(orb);
			abilityData.addXp(SKILLS_CONFIG.buffUsed);
			data.addTickHandler(PURIFY_PARTICLE_SPAWNER);

		}

	}

	@Override
	public int getCooldown(AbilityContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		int coolDown = 140;

		if (ctx.getLevel() == 1) {
			coolDown = 130;
		}
		if (ctx.getLevel() == 2) {
			coolDown = 120;
		}
		if (ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
			coolDown = 130;
		}
		if (ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
			coolDown = 110;
		}

		if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
			coolDown = 0;
		}
		return coolDown;
	}

	@Override
	public int getBaseTier() {
		return 5;
	}

	public static class ImmolateLightOrbBehaviour extends LightOrbBehavior.FollowPlayer {
		@Override
		public Behavior<EntityLightOrb> onUpdate(EntityLightOrb entity) {
			super.onUpdate(entity);
			EntityLivingBase emitter = entity.getOwner();
			assert emitter instanceof EntityPlayer || emitter instanceof EntityBender;
			Bender b = Bender.get(emitter);
			if (b != null && BendingData.getFromEntity(emitter) != null && entity.ticksExisted > 1) {
				if (!Objects.requireNonNull(b.getData().getPowerRatingManager(Firebending.ID)).hasModifier(ImmolatePowerModifier.class)) {
					entity.setDead();
				}
			}
			int lightRadius = 5;
			//Stops constant spam and calculations
			if (entity.ticksExisted == 1) {
				AbilityData aD = AbilityData.get(emitter, "immolate");
				int level = aD.getLevel();
				if (level >= 1) {
					lightRadius = 7;
				}
				if (level >= 2) {
					lightRadius = 10;
				}
				if (aD.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
					lightRadius = 9;
				}
				if (aD.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
					lightRadius = 12;
				}
			}
			if (entity.getEntityWorld().isRemote) entity.setLightRadius(lightRadius + (int) (Math.random() * 5));
			return this;
		}

		@Override
		public void fromBytes(PacketBuffer buf) {
			super.fromBytes(buf);
		}

		@Override
		public void toBytes(PacketBuffer buf) {
			super.toBytes(buf);
		}

		@Override
		public void load(NBTTagCompound nbt) {
			super.load(nbt);
		}

		@Override
		public void save(NBTTagCompound nbt) {
			super.save(nbt);
		}
	}

}
