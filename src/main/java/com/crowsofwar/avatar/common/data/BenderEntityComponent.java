package com.crowsofwar.avatar.common.data;

import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import net.minecraft.entity.EntityLivingBase;

/**
 * An instance of {@link Bender} to serve Entities. BenderEntities can't derive directly from
 * Bender itself since then they couldn't extend Entity. Therefore, they use the
 * BenderEntityComponent which extends Bender.
 *
 * @author CrowsOfWar
 */
public class BenderEntityComponent extends Bender {

	private final EntityBender entity;
	private final BendingData data;

	public BenderEntityComponent(EntityBender entity) {
		this.entity = entity;
		this.data = new BendingData(category -> {
		}, () -> {
		}); // mc automatically saves entity
	}

	@Override
	public EntityLivingBase getEntity() {
		return entity;
	}

	@Override
	public BendingData getData() {
		return data;
	}

	@Override
	public boolean isCreativeMode() {
		return false;
	}

	@Override
	public boolean isFlying() {
		return false;
	}

	@Override
	public boolean consumeWaterLevel(int amount) {
		return false;
	}

	@Override
	public BenderInfo getInfo() {
		return new BenderInfoEntity(entity.getUniqueID());
	}
}
