package com.crowsofwar.avatar.common.bending.sand;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntitySandstorm;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class AbilitySandstorm extends Ability {

	public AbilitySandstorm() {
		super(Sandbending.ID, "sandstorm");
	}

	@Override
	public void execute(AbilityContext ctx) {

		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();

		Vector velocity = Vector.toRectangular(Math.toRadians(entity.rotationYaw), 0).times(8);

		EntitySandstorm sandstorm = new EntitySandstorm(world);
		sandstorm.setPosition(Vector.getEntityPos(entity));
		sandstorm.setOwner(entity);
		sandstorm.setVelocity(velocity);
		world.spawnEntity(sandstorm);

		ctx.getData().addStatusControl(StatusControl.SANDSTORM_REDIRECT);

	}

}
