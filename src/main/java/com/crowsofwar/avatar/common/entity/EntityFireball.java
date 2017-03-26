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

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.FireballBehavior;
import com.crowsofwar.avatar.common.entity.data.OwnerAttribute;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityFireball extends AvatarEntity {
	
	public static final DataParameter<String> SYNC_OWNER = EntityDataManager.createKey(EntityFireball.class,
			DataSerializers.STRING);
	public static final DataParameter<FireballBehavior> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityFireball.class, FireballBehavior.DATA_SERIALIZER);
	
	private final OwnerAttribute ownerAttr;
	private AxisAlignedBB expandedHitbox;
	
	private float damage;
	
	/**
	 * @param world
	 */
	public EntityFireball(World world) {
		super(world);
		this.ownerAttr = new OwnerAttribute(this, SYNC_OWNER);
		setSize(.8f, .8f);
	}
	
	@Override
	public void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_BEHAVIOR, new FireballBehavior.Idle());
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		setBehavior((FireballBehavior) getBehavior().onUpdate(this));
		
		Vector v = velocity().dividedBy(20);
		moveEntity(MoverType.SELF, v.x(), v.y(), v.z());
		
		if (inWater) {
			removeStatCtrl();
			int particles = rand.nextInt(4) + 5;
			for (int i = 0; i < particles; i++) {
				worldObj.spawnParticle(EnumParticleTypes.CLOUD, posX, posY, posZ, rand.nextGaussian() * .05,
						rand.nextDouble() * .2, rand.nextGaussian() * .05);
			}
			worldObj.playSound(posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE,
					SoundCategory.PLAYERS, 1, rand.nextFloat() * 0.3f + 1.1f, false);
			setDead();
		}
		
	}
	
	@Override
	public EntityPlayer getOwner() {
		return ownerAttr.getOwner();
	}
	
	public void setOwner(EntityPlayer owner) {
		ownerAttr.setOwner(owner);
	}
	
	public FireballBehavior getBehavior() {
		return dataManager.get(SYNC_BEHAVIOR);
	}
	
	public void setBehavior(FireballBehavior behavior) {
		dataManager.set(SYNC_BEHAVIOR, behavior);
	}
	
	public float getDamage() {
		return damage;
	}
	
	public void setDamage(float damage) {
		this.damage = damage;
	}
	
	@Override
	public void onCollideWithSolid() {
		Explosion explosion = new Explosion(worldObj, this, posX, posY, posZ,
				STATS_CONFIG.fireballSettings.explosionSize, !worldObj.isRemote,
				STATS_CONFIG.fireballSettings.damageBlocks);
		if (!ForgeEventFactory.onExplosionStart(worldObj, explosion)) {
			explosion.doExplosionA();
			explosion.doExplosionB(true);
		}
		
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		ownerAttr.load(nbt);
		setDamage(nbt.getFloat("Damage"));
		setBehavior((FireballBehavior) Behavior.lookup(nbt.getInteger("Behavior"), this));
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		ownerAttr.save(nbt);
		nbt.setFloat("Damage", getDamage());
		nbt.setInteger("Behavior", getBehavior().getId());
	}
	
	public AxisAlignedBB getExpandedHitbox() {
		return this.expandedHitbox;
	}
	
	@Override
	public void setEntityBoundingBox(AxisAlignedBB bb) {
		super.setEntityBoundingBox(bb);
		expandedHitbox = bb.expand(0.35, 0.35, 0.35);
	}
	
	@Override
	public boolean shouldRenderInPass(int pass) {
		return (pass == 0 || pass == 1) && !isHidden();
	}
	
	private void removeStatCtrl() {
		if (getOwner() != null) {
			AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(getOwner());
			data.removeStatusControl(StatusControl.THROW_FIREBALL);
			if (!worldObj.isRemote) data.sync();
		}
	}
	
}
