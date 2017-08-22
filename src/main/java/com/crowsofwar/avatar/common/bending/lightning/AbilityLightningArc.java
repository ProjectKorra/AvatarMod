package com.crowsofwar.avatar.common.bending.lightning;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityLightningArc;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import java.util.UUID;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

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
		Bender bender = ctx.getBender();
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = entity.world;

		if (bender.consumeChi(STATS_CONFIG.chiLightning)) {
			
			float[] turbulenceValues = {0.6f, 1.2f};

			for (float turbulence : turbulenceValues) {

				EntityLightningArc lightning = new EntityLightningArc(world);
				lightning.setOwner(entity);
				lightning.setTurbulence(turbulence);

				lightning.setPosition(Vector.getEyePos(entity));
				lightning.setEndPos(Vector.getEyePos(entity));

				Vector velocity = Vector.getLookRectangular(entity);
				velocity = velocity.normalize().times(30);
				lightning.setVelocity(velocity);

				world.spawnEntity(lightning);

			}

		}

	}

	@Override
	public UUID getId() {
		return ID;
	}

}
