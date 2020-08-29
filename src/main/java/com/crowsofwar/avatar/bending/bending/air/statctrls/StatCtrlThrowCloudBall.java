package com.crowsofwar.avatar.bending.bending.air.statctrls;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.air.AbilityCloudBurst;
import com.crowsofwar.avatar.entity.mob.EntityBender;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityCloudBall;
import com.crowsofwar.avatar.entity.data.CloudburstBehavior;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.client.controls.AvatarControl.CONTROL_LEFT_CLICK;
import static com.crowsofwar.avatar.util.data.StatusControl.CrosshairPosition.LEFT_OF_CROSSHAIR;

public class StatCtrlThrowCloudBall extends StatusControl {
	public StatCtrlThrowCloudBall() {
		super(16, CONTROL_LEFT_CLICK, LEFT_OF_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = ctx.getWorld();
		AbilityData abilityData = ctx.getData().getAbilityData("cloudburst");
		AbilityCloudBurst burst = (AbilityCloudBurst) Abilities.get("cloudburst");
		Bender bender = ctx.getBender();

		if (abilityData != null && burst != null) {
			double speed = burst.getProperty(Ability.SPEED, abilityData).floatValue() * 3;
			float exhaustion, burnout, chiCost;
			int cooldown;
			exhaustion = burst.getExhaustion(abilityData);
			burnout = burst.getBurnOut(abilityData);
			cooldown = burst.getCooldown(abilityData);
			chiCost = burst.getChiCost(abilityData);

			if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative())
				burnout = exhaustion = chiCost = cooldown = 0;

			speed *= abilityData.getDamageMult() * abilityData.getXpModifier();

			if (bender.consumeChi(chiCost)) {

				EntityCloudBall cloudBall = AvatarEntity.lookupControlledEntity(world, EntityCloudBall.class, entity);

				if (cloudBall != null) {
					cloudBall.setBehavior(new CloudburstBehavior.Thrown());
					cloudBall.setVelocity(Vector.getLookRectangular(entity).times(speed * 1.5F));
					abilityData.setAbilityCooldown(cooldown);
					if (entity instanceof EntityPlayer)
						((EntityPlayer) entity).addExhaustion(exhaustion);
					abilityData.addBurnout(burnout);
				}

				abilityData.setRegenBurnout(true);
			}
		}

		return true;
	}

}
//REGISTER THIS TO SEE IF IT FIXES ITSELF
//Umm Idk what the line above is referring to- cloudburst is pretty much fixed except for the occasional invisibility
//weirdness.

