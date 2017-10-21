package com.crowsofwar.avatar.common.bending.sand;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntitySandstorm;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.data.WorldData;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import java.util.UUID;

public class AbilitySandstorm extends Ability {

	public AbilitySandstorm() {
		super(Sandbending.ID, "sandstorm");
	}

	@Override
	public void execute(AbilityContext ctx) {

		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();

		Raytrace.Result raytrace = Raytrace.getTargetBlock(entity, 20, false);
		if (raytrace.hitSomething()) {

			Vector hitPos = raytrace.getPosPrecise();

			EntitySandstorm sandstorm = new EntitySandstorm(world);
			sandstorm.setPosition(hitPos);
			sandstorm.setOwner(entity);
			world.spawnEntity(sandstorm);

			ctx.getData().addStatusControl(StatusControl.SANDSTORM_REDIRECT);

		}

	}

}
