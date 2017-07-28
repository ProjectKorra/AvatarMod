package com.crowsofwar.avatar.common.bending.lightning;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityLightningArc;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * @author CrowsOfWar
 */
public class AbilityLightningArc extends Ability {

	public static final UUID ID = UUID.fromString("9ebd7ff4-daad-42d6-90c6-f58a5f388597");

	public AbilityLightningArc() {
		super(Lightningbending.ID, "lightning_arc");
	}

	@Override
	public void execute(AbilityContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = entity.world;

		Vector position = Vector.getEntityPos(entity);
		Vector look = Vector.getLookRectangular(entity);

		EntityLightningArc lightning = new EntityLightningArc(world);
		lightning.copyLocationAndAnglesFrom(entity);
		lightning.setOwner(entity);
		lightning.setVelocity(look.times(20));

		world.spawnEntity(lightning);

	}

	@Override
	public UUID getId() {
		return ID;
	}

}
