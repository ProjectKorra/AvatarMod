/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/
package com.crowsofwar.avatar.common.data;

import com.crowsofwar.avatar.common.data.ctx.NoBenderInfo;
import com.crowsofwar.gorecore.util.GoreCoreByteBufUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * BenderInfo are immutable objects that store information about a {@link Bender} so that he/she can
 * be found later. A reference to BenderInfo should never be null, instead the subclass
 * {@link NoBenderInfo} should be used.
 * <p>
 * Since there are different ways to identify a Bender based on what type it is (players
 * use account Id, entities use entity Id), BenderInfo has subclasses which determine the
 * behavior. For players, uses {@link BenderInfoPlayer}; for entities, uses
 * {@link BenderInfoEntity}.
 *
 * @author CrowsOfWar
 */
public abstract class BenderInfo {

	public static BenderInfo readFromNbt(NBTTagCompound nbt) {
		String type = nbt.getString("Type");
		UUID id = nbt.getUniqueId("Id");
		if (type.equals("Player")) {
			return new BenderInfoPlayer(id);
		} else if (type.equals("Entity")) {
			return new BenderInfoEntity(id);
		} else {
			return new NoBenderInfo();
		}
	}

	public static BenderInfo readFromBytes(ByteBuf buf) {
		String type = ByteBufUtils.readUTF8String(buf);
		UUID id = GoreCoreByteBufUtil.readUUID(buf);
		if (type.equals("Player")) {
			return new BenderInfoPlayer(id);
		} else if (type.equals("Entity")) {
			return new BenderInfoEntity(id);
		} else {
			return new NoBenderInfo();
		}
	}

	public static BenderInfo get(boolean player, @Nullable UUID id) {
		if (id == null) {
			return new NoBenderInfo();
		}
		if (player) {
			return new BenderInfoPlayer(id);
		} else {
			return new BenderInfoEntity(id);
		}
	}

	public static BenderInfo get(@Nullable EntityLivingBase entity) {
		if (entity == null) {
			return new NoBenderInfo();
		}
		if (entity instanceof EntityPlayer) {
			return new BenderInfoPlayer(entity.getName());
		} else {
			return new BenderInfoEntity(entity.getUniqueID());
		}
	}

	public abstract boolean isPlayer();

	@Nullable
	public abstract UUID getId();

	@Nullable
	public abstract Bender find(World world);

	/**
	 * Gets the type of this BenderInfo (according to class hierarchy) to be used in NBT compounds
	 */
	private String getType() {
		// this isn't an instance method since it would be a bit overcomplicated for something like this
		// Only using getType() wouldn't work for static method readFromNbt, which means a registry would be needed
		// ... which isn't necessary if BenderInfo only has 2-3 subclasses and will not add more in the future
		if (this instanceof BenderInfoPlayer) {
			return "Player";
		}
		if (this instanceof BenderInfoEntity) {
			return "Entity";
		}
		return "None";
	}

	public void writeToNbt(NBTTagCompound nbt) {
		nbt.setString("Type", getType());
		if (getId() != null) {
			nbt.setUniqueId("Id", getId());
		}
	}

	public void writeToBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, getType());
		if (getId() != null) {
			GoreCoreByteBufUtil.writeUUID(buf, getId());
		}
	}

}
