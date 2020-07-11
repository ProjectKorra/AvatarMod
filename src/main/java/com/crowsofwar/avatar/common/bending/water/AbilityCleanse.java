package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.water.tickhandlers.CleansePowerModifier;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static java.lang.Math.toRadians;

public class AbilityCleanse extends Ability {

	public AbilityCleanse() {
		super(Waterbending.ID, "cleanse");
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
		AbilityData abilityData = data.getAbilityData(this);

		float chi = STATS_CONFIG.chiBuff;
		if (abilityData.getLevel() == 1) {
			chi = STATS_CONFIG.chiBuffLvl2;
		} else if (abilityData.getLevel() == 2) {
			chi = STATS_CONFIG.chiBuffLvl3;
		} else if (abilityData.getLevel() == 3) {
			chi = STATS_CONFIG.chiBuffLvl4;
		}

		Vector targetPos = getClosestWaterBlock(entity, ctx.getLevel() * 3);

		if ((bender.consumeChi(chi) && targetPos != null || (entity instanceof EntityPlayerMP && ((EntityPlayerMP) entity).isCreative())
				|| ctx.consumeWater(4))) {

			// Duration: 5-10s
			int duration = abilityData.getLevel() < 2 ? 100 : 200;
			int regenLevel = MathHelper.clamp(abilityData.getLevel(), 0, 2);

			abilityData.addXp(SKILLS_CONFIG.buffUsed);

			// Apply basic potion effects

			entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, duration, regenLevel));

			if (abilityData.getLevel() >= 2) {
				entity.addPotionEffect(new PotionEffect(MobEffects.SATURATION, duration));
			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				entity.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, duration, 1));
				entity.addPotionEffect(new PotionEffect(MobEffects.SATURATION, duration, 1));
				entity.addPotionEffect(new PotionEffect(MobEffects.INSTANT_HEALTH, 0, 0));
			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				entity.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, duration));
				entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, duration, 1));
				entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, duration));
			}

			// Perform group heal?

			if (abilityData.getLevel() >= 1) {

				int groupLevel = abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)
						? 1 : 0;
				int groupDuration = abilityData.getLevel() == 3 ? 100 : 60;
				int groupRadius = abilityData.getLevel() >= 2 ? 6 : 4;

				PotionEffect groupEffect = new PotionEffect(MobEffects.REGENERATION, groupDuration,
						groupLevel);
				applyGroupEffect(ctx, groupRadius, player -> player.addPotionEffect(groupEffect));
				applyGroupEffect(ctx, groupRadius, this::addChiBonus);

			}

			// Apply power modifier

			CleansePowerModifier modifier = new CleansePowerModifier();
			modifier.setTicks(duration);
			// Ignore warning; we know they have the bending, so manager for that bending != null
			//noinspection ConstantConditions
			data.getPowerRatingManager(getBendingId()).addModifier(modifier, ctx);

		} else {
			bender.sendMessage("avatar.cleanseFail");
		}

	}

	/**
	 * Applies the given effect to all nearby players in the given range, excluding the
	 * caster. Range is in blocks.
	 */
	private void applyGroupEffect(AbilityContext ctx, int radius, Consumer<EntityPlayer> effect) {

		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();
		AxisAlignedBB aabb = new AxisAlignedBB(
				entity.posX - radius, entity.posY - radius, entity.posZ - radius,
				entity.posX + radius, entity.posY + radius, entity.posZ + radius);

		List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, aabb);

		for (EntityPlayer player : players) {

			// Initial aabb check was rectangular, need to check distance for truly circular radius
			if (player.getDistanceSq(entity) > radius * radius) {
				continue;
			}

			// Ignore the caster
			if (player == entity) {
				continue;
			}

			effect.accept(player);

		}

	}

	/**
	 * Grants the player a chi bonus
	 */
	private void addChiBonus(EntityPlayer player) {

		BendingData data = BendingData.get(player);
		data.chi().changeTotalChi(STATS_CONFIG.cleanseChiGroupBonus);
		data.chi().changeAvailableChi(STATS_CONFIG.cleanseChiGroupBonus);

	}


	private Vector getClosestWaterBlock(EntityLivingBase entity, int level) {
		World world = entity.world;

		Vector eye = Vector.getEyePos(entity);

		double rangeMult = 0.6;
		if (level >= 1) {
			rangeMult = 1;
		}

		double range = STATS_CONFIG.cleanseSearchRadius * rangeMult;
		for (int i = 0; i < STATS_CONFIG.cleanseAngles; i++) {
			for (int j = 0; j < STATS_CONFIG.cleanseAngles; j++) {

				double yaw = entity.rotationYaw + i * 360.0 / STATS_CONFIG.cleanseAngles;
				double pitch = entity.rotationPitch + j * 360.0 / STATS_CONFIG.cleanseAngles;

				BiPredicate<BlockPos, IBlockState> isWater = (pos, state) -> state.getBlock() == Blocks.WATER
						|| state.getBlock() == Blocks.FLOWING_WATER || state.getBlock() == Blocks.ICE || state.getBlock() == Blocks.SNOW_LAYER
						|| state.getBlock() == Blocks.SNOW;

				Vector angle = Vector.toRectangular(toRadians(yaw), toRadians(pitch));
				Raytrace.Result result = Raytrace.predicateRaytrace(world, eye, angle, range, isWater);
				if (result.hitSomething()) {
					return result.getPosPrecise();
				}

			}

		}

		return null;

	}

	@Override
	public int getBaseTier() {
		return 5;
	}

	@Override
	public int getCooldown(AbilityContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		int coolDown = 160;

		if (ctx.getLevel() == 1) {
			coolDown = 140;
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


}

