package com.crowsofwar.avatar.common.data;

import com.crowsofwar.avatar.common.util.AvatarEntityUtils;
import com.crowsofwar.gorecore.util.AccountUUIDs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author CrowsOfWar
 */
public class BenderInfoPlayer extends BenderInfo {

	private UUID playerId;

	public BenderInfoPlayer(@Nonnull String playerName) {
		this(AvatarEntityUtils.getPlayerFromUsername(playerName).getUniqueID());//AccountUUIDs.getId(playerName));
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
		Entity entity = AvatarEntityUtils.getEntityFromStringID(playerId.toString());
		if (entity instanceof EntityLivingBase) {
			return Bender.get((EntityLivingBase) entity);//AccountUUIDs.findEntityFromUUID(world, playerId));
		}
		return null;
	}
}
