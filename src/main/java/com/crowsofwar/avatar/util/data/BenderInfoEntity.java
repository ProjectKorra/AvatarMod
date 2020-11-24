package com.crowsofwar.avatar.util.data;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * @author CrowsOfWar
 */
public class BenderInfoEntity extends BenderInfo {

	private UUID entityId;

	public BenderInfoEntity(@Nonnull UUID entityId) {
		this.entityId = entityId;
	}

	@Override
	public boolean isPlayer() {
		return true;
	}

	@Nonnull
	@Override
	public UUID getId() {
		return entityId;
	}

	@Nullable
	@Override
	public Bender find(World world) {
		List<EntityLivingBase> entities = world.getEntities(EntityLivingBase.class,
				ent -> ent.getUniqueID().equals(entityId));
		return !entities.isEmpty() ? Bender.get(entities.get(0)) : null;
	}
}
