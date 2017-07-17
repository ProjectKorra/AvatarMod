package com.crowsofwar.avatar.common.data;

import com.crowsofwar.avatar.common.data.ctx.Bender;
import com.crowsofwar.avatar.common.data.ctx.BenderInfo;
import com.crowsofwar.gorecore.util.AccountUUIDs;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
		return Bender.get(AccountUUIDs.findEntityFromUUID(world, entityId));
	}
}
