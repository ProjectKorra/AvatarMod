package com.crowsofwar.avatar.common.data;

import net.minecraft.world.World;

import com.crowsofwar.gorecore.util.AccountUUIDs;

import javax.annotation.*;
import java.util.UUID;

/**
 * @author CrowsOfWar
 */
public class BenderInfoPlayer extends BenderInfo {

	private UUID playerId;

	public BenderInfoPlayer(@Nonnull String playerName) {
		this(AccountUUIDs.getId(playerName));
	}

	public BenderInfoPlayer(@Nonnull UUID playerId) {
		this.playerId = playerId;
	}

	@Override
	public boolean isPlayer() {
		return true;
	}

	@Nullable
	@Override
	public UUID getId() {
		return playerId;
	}

	@Nullable
	@Override
	public Bender find(World world) {
		return Bender.get(AccountUUIDs.findEntityFromUUID(world, playerId));
	}
}
