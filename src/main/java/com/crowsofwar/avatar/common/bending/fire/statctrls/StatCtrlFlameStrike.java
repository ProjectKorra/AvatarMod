package com.crowsofwar.avatar.common.bending.fire.statctrls;

import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.StatusControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.avatar.common.util.Raytrace;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_LEFT_CLICK;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_RIGHT_CLICK;
import static com.crowsofwar.avatar.common.data.StatusControlController.FLAME_STRIKE_MAIN;
import static com.crowsofwar.avatar.common.data.StatusControlController.FLAME_STRIKE_OFF;
import static com.crowsofwar.avatar.common.data.TickHandlerController.FLAME_STRIKE_HANDLER;

public class StatCtrlFlameStrike extends StatusControl {

	private static HashMap<UUID, Integer> timesUsed = new HashMap<>();
	EnumHand hand;

	public StatCtrlFlameStrike(EnumHand hand) {
		super(18, hand == EnumHand.MAIN_HAND ? CONTROL_LEFT_CLICK : CONTROL_RIGHT_CLICK,
				hand == EnumHand.MAIN_HAND ? CrosshairPosition.LEFT_OF_CROSSHAIR : CrosshairPosition.RIGHT_OF_CROSSHAIR);
		this.hand = hand;
	}

	public static int getTimesUsed(UUID id) {
		return timesUsed.getOrDefault(id, 0);
	}

	public static void setTimesUsed(UUID id, int times) {
		if (timesUsed.containsKey(id))
			timesUsed.replace(id, times);
		else timesUsed.put(id, times);
	}

	@Override
	public boolean execute(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = ctx.getWorld();
		AbilityData abilityData = ctx.getData().getAbilityData("flame_strike");

		if (!ctx.getData().hasTickHandler(FLAME_STRIKE_HANDLER))
			return true;

		double reach = Raytrace.getReachDistance(entity);
		float powerModifier = (float) (ctx.getBender().getDamageMult(Firebending.ID));
		float xpMod = abilityData.getXpModifier();

		float damage = STATS_CONFIG.flameStrikeSettings.damage;
		int performance = STATS_CONFIG.flameStrikeSettings.performanceAmount;
		float knockBack = STATS_CONFIG.flameStrikeSettings.knockback;
		int fireTime = STATS_CONFIG.flameStrikeSettings.fireTime;
		float size= STATS_CONFIG.flameStrikeSettings.size;
		float xp = SKILLS_CONFIG.flameStrikeHit;
		int particleCount = 4;

		if (abilityData.getLevel() == 1) {
			damage *= 1.5F;
			knockBack *= 1.125F;
			fireTime += 2;
			performance += 2;
			xp -= 1;
			particleCount += 2;
		}
		if (abilityData.getLevel() == 2) {
			damage *= 2F;
			knockBack *= 1.25F;
			fireTime += 4;
			performance += 5;
			xp -= 2;
			particleCount += 4;
		}
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			damage *= 2.5F;
			performance += 10;
			fireTime += 3;
			size *= 0.5F;
			particleCount += 10;
		}
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
			damage *= 4;
			performance += 2;
			fireTime += 5;
			size *= 2;
			particleCount -= 1;
		}

		damage *= powerModifier * xpMod;
		knockBack *= powerModifier * xpMod;
		fireTime *= powerModifier * xpMod;
		performance *= powerModifier * xpMod;

		Vec3d lookPos = entity.getLookVec();

		if (world.isRemote) {
			Vec3d direction = lookPos.scale(0.25).add(world.rand.nextGaussian() / 40, world.rand.nextGaussian() / 40, world.rand.nextGaussian() / 40);
			for (int i = 0; i < particleCount * 20; i++) {
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(lookPos.add(entity.getPositionVector().add(0, entity.getEyeHeight(), 0)))
						.time(6 + AvatarUtils.getRandomNumberInRange(0, 4)).vel(direction).
						clr(255, 15, 5).collide(true).scale(size).element(new Firebending()).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(lookPos.add(entity.getPositionVector())).time(6 + AvatarUtils.getRandomNumberInRange(0, 4)).vel(direction)
						.clr(255, 60 + AvatarUtils.getRandomNumberInRange(0, 60), 10).collide(true).
						scale(size).element(new Firebending()).spawn(world);
			}
		}

		if (hand == EnumHand.OFF_HAND)
			entity.swingArm(hand);

		if (!world.isRemote)
			setTimesUsed(entity.getPersistentID(), getTimesUsed(entity.getPersistentID()) + 1);

		if (ctx.getData().hasTickHandler(FLAME_STRIKE_HANDLER))
			ctx.getData().addStatusControl(hand == EnumHand.MAIN_HAND ? FLAME_STRIKE_OFF : FLAME_STRIKE_MAIN);

		return true;
	}

	public boolean canCollideWith(Entity entity) {
		if (entity instanceof EntityEnderCrystal) {
			return true;
		} else
			return (entity.canBePushed() && entity.canBeCollidedWith()) || entity instanceof EntityLivingBase;
	}
}
