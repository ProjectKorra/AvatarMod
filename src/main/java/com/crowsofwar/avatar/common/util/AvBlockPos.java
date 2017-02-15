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

package com.crowsofwar.avatar.common.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumFacing;

/**
 * Recommended to use minecraft Vec3i instead.
 * 
 * @author CrowsOfWar
 */
@Deprecated
public class AvBlockPos {
	
	public int x, y, z;
	
	public AvBlockPos(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}
	
	public static AvBlockPos fromBytes(ByteBuf buf) {
		return new AvBlockPos(buf.readInt(), buf.readInt(), buf.readInt());
	}
	
	public double dist(AvBlockPos pos) {
		return dist(this, pos);
	}
	
	public double distSq(AvBlockPos pos) {
		return distSq(this, pos);
	}
	
	public static double dist(AvBlockPos pos1, AvBlockPos pos2) {
		return Math.sqrt(distSq(pos1, pos2));
	}
	
	public static double distSq(AvBlockPos pos1, AvBlockPos pos2) {
		double dx = pos2.x - pos1.x;
		double dy = pos2.y - pos1.y;
		double dz = pos2.z - pos1.z;
		return dx * dx + dy * dy + dz * dz;
	}
	
	/**
	 * Move this BlockPos in the specified direction by 1 meter.
	 */
	public void offset(EnumFacing direction) {
		switch (direction.getAxis()) {
			case X:
				x += direction.getAxisDirection().getOffset();
				break;
			case Y:
				y += direction.getAxisDirection().getOffset();
				break;
			case Z:
				z += direction.getAxisDirection().getOffset();
				break;
		}
	}
	
	/**
	 * Returns true if the object is a BlockPos and it has the same coordinates.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof AvBlockPos)) return false;
		AvBlockPos pos = (AvBlockPos) obj;
		return x == pos.x && y == pos.y && z == pos.z;
	}
	
}
