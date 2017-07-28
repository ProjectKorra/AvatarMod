package com.crowsofwar.avatar.common.bending.lightning;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityLightningArc;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import java.util.List;
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

		Vector hitPos = performRaytrace(ctx);

		if (!hitPos.equals(Vector.ZERO)) {

			EntityLightningArc lightning = new EntityLightningArc(world);
			lightning.copyLocationAndAnglesFrom(entity);
			lightning.setOwner(entity);
			lightning.setEndPos(hitPos);

			world.spawnEntity(lightning);

		}

	}

	@Override
	public UUID getId() {
		return ID;
	}

	/**
	 * Performs a raytrace to find the closest hit block or entity. Returns Vector.ZERO if
	 * nothing is hit
	 */
	private Vector performRaytrace(AbilityContext ctx) {

		final double maxRange = 20;

		Vector pos = Vector.getEntityPos(ctx.getBenderEntity());
		Vector look = Vector.getLookRectangular(ctx.getBenderEntity());

		List<Entity> hitEntity = Raytrace.entityRaytrace(ctx.getWorld(), pos, look, maxRange, ent
				-> ent != ctx
				.getBenderEntity());

		if (!hitEntity.isEmpty()) {
			return Vector.getEntityPos(hitEntity.get(0));
		}

		Raytrace.Result raytrace = Raytrace.raytrace(ctx.getWorld(), pos, look, maxRange, false);

		if (raytrace.hitSomething()) {
			return raytrace.getPosPrecise();
		} else {
			return Vector.ZERO;
		}

	}

}
