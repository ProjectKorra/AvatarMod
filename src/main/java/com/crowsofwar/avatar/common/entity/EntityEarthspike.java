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

package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 * @author CrowsOfWar
 */
public class EntityEarthspike extends AvatarEntity {

	//private static final DataParameter<Float> SYNC_SIZE = EntityDataManager
	//		.createKey(EntityEarthspike.class, DataSerializers.FLOAT);

	private float damageMult;
	//private float Size = 1;

	public EntityEarthspike(World world) {
		super(world);
		setSize(1, 1);
	}

	public void setDamageMult(float mult) {
		this.damageMult = mult;
	}

//	public void setSize (float size) {
	//	dataManager.set(SYNC_SIZE, size);
	//}

	//public float getSize() {
	//	return dataManager.get(SYNC_SIZE);
	//}

	@Override
	protected void entityInit() {
		super.entityInit();
		//dataManager.register(SYNC_SIZE, Size);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		setDead();
	}

	@Override
	protected boolean canCollideWith(Entity entity) {
		if (entity instanceof EntityEarthspike || entity instanceof EntityEarthspikeSpawner) {
			return false;
		}
		return entity instanceof EntityLivingBase || super.canCollideWith(entity);

	}

	@Override
	public void onUpdate() {

		super.onUpdate();
		if (world.isRemote) {
			setWorld(getOwner().world);
		}


		//For some reason earthspike thinks it's clientside. I don't why/
		//HHAAAAALLPPP MEEEEE

		setVelocity(Vector.ZERO);
		if (ticksExisted >= 15) {
			this.setDead();
		}

		// amount of entities which were successfully attacked
		int attacked = 0;

		// Push collided entities back
		if (!world.isRemote) {
			List<Entity> collided = world.getEntitiesInAABBexcluding(this, getEntityBoundingBox(),
					entity -> entity != getOwner());
			if (!collided.isEmpty()) {
				for (Entity entity : collided) {
					if (attackEntity(entity)) {
						attacked++;
					}
				}
			}
		}

		if (!world.isRemote && getOwner() != null) {
			BendingData data = BendingData.get(getOwner());
			if (data != null) {
				data.getAbilityData("earthspike").addXp(SKILLS_CONFIG.earthspikeHit * attacked);
			}
		}
	}

	@Override
	protected void onCollideWithEntity(Entity entity) {
		if (!world.isRemote) {
			pushEntity(entity);
			if (attackEntity(entity)) {
				if (getOwner() != null) {
					BendingData data = BendingData.get(getOwner());
					data.getAbilityData("earthspike").addXp(SKILLS_CONFIG.earthspikeHit);
					BattlePerformanceScore.addMediumScore(getOwner());
				}

			}
			System.out.println("Success??");
		}
		System.out.println("confusion");
	}

	private boolean attackEntity(Entity entity) {
		if (!(entity instanceof EntityItem && entity.ticksExisted <= 10)) {
			DamageSource ds = AvatarDamageSource.causeEarthspikeDamage(entity, getOwner());
			float damage = STATS_CONFIG.earthspikeSettings.damage * damageMult;
			return entity.attackEntityFrom(ds, damage);
		}

		return false;
	}

	private void pushEntity(Entity entity) {
		Vector entityPos = Vector.getEntityPos(entity);
		Vector direction = entityPos.minus(this.position());
		Vector velocity = direction.times(STATS_CONFIG.earthspikeSettings.push);
		entity.addVelocity(velocity.x()/4, velocity.y(), velocity.z()/5);
	}

	@Override
	public boolean isProjectile() {
		return true;
	}
}
