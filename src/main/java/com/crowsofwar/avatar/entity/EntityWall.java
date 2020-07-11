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

package com.crowsofwar.avatar.entity;

import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.avatar.entity.data.SyncedEntity;
import com.crowsofwar.gorecore.util.Vector;
import com.google.common.base.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.UUID;

import static com.crowsofwar.gorecore.util.GoreCoreNBTUtil.nestedCompound;
import static java.lang.Math.abs;
import static net.minecraft.util.EnumFacing.NORTH;

/**
 * @author CrowsOfWar
 */
public class EntityWall extends AvatarEntity {

	private static final DataParameter<Integer> SYNC_DIRECTION = EntityDataManager.createKey(EntityWall.class,
			DataSerializers.VARINT);
	private static final DataParameter<Optional<UUID>>[] SYNC_SEGMENTS;

	static {
		SYNC_SEGMENTS = new DataParameter[7];
		for (int i = 0; i < SYNC_SEGMENTS.length; i++) {
			SYNC_SEGMENTS[i] = EntityDataManager.createKey(EntityWall.class, DataSerializers.OPTIONAL_UNIQUE_ID);
		}
	}

	/**
	 * All the segments in this wall. MUST be fixed-length, as the data parameters
	 * must be same for both sides.
	 */
	private final SyncedEntity<EntityWallSegment>[] segments;

	private int nextSegment = 0;
	private boolean dropTypePlace = false;

	/**
	 * @param world
	 */
	@SuppressWarnings("unchecked")
	public EntityWall(World world) {
		super(world);
		this.segments = new SyncedEntity[7];
		for (int i = 0; i < segments.length; i++) {
			segments[i] = new SyncedEntity(this, SYNC_SEGMENTS[i]);
			segments[i].preventNullSaving();
		}
		setSize(0, 0);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_DIRECTION, NORTH.ordinal());
		for (DataParameter<Optional<UUID>> syncSegment : SYNC_SEGMENTS) {
			dataManager.register(syncSegment, Optional.absent());
		}
	}

	@Override
	public void onUpdate() {
		// super.onUpdate();
		// Helps reduce glitchiness

		if (this.getOwner() == null) {
			this.setDead();
		}

		// Sync y-velocity with slowest moving wall segment
		// Also calculate lowest top pos of each segment
		double slowest = Integer.MAX_VALUE;
		double lowest = Integer.MAX_VALUE;
		for (SyncedEntity<EntityWallSegment> ref : segments) {
			EntityWallSegment seg = ref.getEntity();
			if (seg != null) {

				if (abs(seg.velocity().y()) < abs(slowest)) {
					slowest = seg.velocity().y();
				}

				double topPos = seg.position().y() + seg.height;
				if (topPos < lowest) {
					lowest = topPos;
				}

			}
		}

		// Now sync all wall segment speeds
		// But only if they aren't blocked
		for (SyncedEntity<EntityWallSegment> ref : segments) {
			EntityWallSegment seg = ref.getEntity();
			if (seg != null) {

				Vector vel = seg.velocity();
				Vector pos = seg.position();

				if (slowest > 0.1) {
					seg.setVelocity(vel.withY(slowest));
				}

			}
		}

		setVelocity(Vector.ZERO);
		this.noClip = true;
		move(MoverType.SELF, 0, slowest / 20, 0);
	}

	/**
	 * To be used ONLY by {@link EntityWallSegment}
	 */
	void addSegment(EntityWallSegment segment) {
		segments[nextSegment].setEntity(segment);
		nextSegment++;
	}

	public EntityWallSegment getSegment(int i) {
		return segments[i].getEntity();
	}

	public EnumFacing getDirection() {
		return EnumFacing.values()[dataManager.get(SYNC_DIRECTION)];
	}

	public void setDirection(EnumFacing direction) {
		if (direction.getAxis().isVertical())
			throw new IllegalArgumentException("Cannot face up/down: " + direction);
		this.dataManager.set(SYNC_DIRECTION, direction.ordinal());
	}

	@Override
	public boolean isShield() {
		return true;
	}

	/*
	 * Changes how the wall should drop its blocks. false -> dropBlocks(), true ->
	 * placeBlocks(). dropBlocks() is the default
	 */
	public void setDropTypePlace(boolean place) {
		dropTypePlace = place;
	}

	@Override
	public void setDead() {
		for (SyncedEntity<EntityWallSegment> ref : segments) {
			// don't use setDead() as that will trigger this being called again
			EntityWallSegment entity = ref.getEntity();
			if (entity != null) {
				// Avoid setDead() as that will call wall.setDead()
				entity.isDead = true;

				if (dropTypePlace) {
					entity.placeBlocks();
				} else {
					entity.dropBlocks();
				}
			}
		}
		if (getOwner() != null) {
			BendingData bD = BendingData.get(getOwner());
			bD.removeStatusControl(StatusControlController.PLACE_WALL);
			bD.removeStatusControl(StatusControlController.DROP_WALL);
		}
		super.setDead();
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		for (int i = 0; i < segments.length; i++)
			segments[i].readFromNbt(nestedCompound(nbt, "Wall" + i));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		for (int i = 0; i < segments.length; i++)
			segments[i].writeToNbt(nestedCompound(nbt, "Wall" + i));
	}

	@Override
	public boolean canCollideWith(Entity entity) {
		return super.canCollideWith(entity) && !(entity instanceof EntityWall)
				&& !(entity instanceof EntityWallSegment);
	}

}
