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

import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.fire.AbilityFireball;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.StatusControlController;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.FireballBehavior;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import com.zeitheron.hammercore.api.lighting.ColoredLight;
import com.zeitheron.hammercore.api.lighting.impl.IGlowingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Objects;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 * @author CrowsOfWar
 */
@Optional.Interface(iface = "com.zeitheron.hammercore.api.lighting.impl.IGlowingEntity", modid = "hammercore")
public class EntityFireball extends EntityOffensive implements IGlowingEntity {

	public static final DataParameter<Integer> SYNC_SIZE = EntityDataManager.createKey(EntityFireball.class,
			DataSerializers.VARINT);
	private static final DataParameter<FireballBehavior> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityFireball.class, FireballBehavior.DATA_SERIALIZER);
	private AxisAlignedBB expandedHitbox;

	private float damage;
	private BlockPos position;

	/**
	 * @param world
	 */
	public EntityFireball(World world) {
		super(world);
		setSize(.8f, .8f);
		this.position = this.getPosition();
		this.lightTnt = true;
	}

	@Override
	public BendingStyle getElement() {
		return new Firebending();
	}

	@Override
	public void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_BEHAVIOR, new FireballBehavior.Idle());
		dataManager.register(SYNC_SIZE, 30);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();


		if (getBehavior() == null) {
			this.setBehavior(new FireballBehavior.Thrown());
		}
		setBehavior((FireballBehavior) getBehavior().onUpdate(this));
		if (ticksExisted % 30 == 0) {
			world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 6, 0.8F);
		}

		// Add hook or something
		if (getOwner() == null) {
			setDead();
			removeStatCtrl();
		}

		if (getOwner() != null) {
			EntityFireball ball = AvatarEntity.lookupControlledEntity(world, EntityFireball.class, getOwner());
			BendingData bD = BendingData.get(getOwner());
			if (ball == null && bD.hasStatusControl(StatusControlController.THROW_FIREBALL)) {
				bD.removeStatusControl(StatusControlController.THROW_FIREBALL);
			}
			if (ball != null && ball.getBehavior() instanceof FireballBehavior.PlayerControlled
					&& !(bD.hasStatusControl(StatusControlController.THROW_FIREBALL))) {
				bD.addStatusControl(StatusControlController.THROW_FIREBALL);
			}
			if (getBehavior() != null && getBehavior() instanceof FireballBehavior.PlayerControlled) {
				this.position = this.getPosition();
			}
		}
		//I'm using 0.03125, because that results in a size of 0.5F when rendering, as the default size for the fireball is actually 16.
		//This is due to weird rendering shenanigans
		setSize(getSize() * 0.03125F, getSize() * 0.03125F);
	}

	@Override
	public boolean onMajorWaterContact() {
		spawnExtinguishIndicators();
		if (getBehavior() instanceof FireballBehavior.PlayerControlled) {
			removeStatCtrl();
		}
		setDead();
		removeStatCtrl();
		return true;
	}

	@Override
	public boolean onMinorWaterContact() {
		spawnExtinguishIndicators();
		return false;
	}

	public FireballBehavior getBehavior() {
		return dataManager.get(SYNC_BEHAVIOR);
	}

	public void setBehavior(FireballBehavior behavior) {
		dataManager.set(SYNC_BEHAVIOR, behavior);
	}

	@Override
	public EntityLivingBase getController() {
		return getBehavior() instanceof FireballBehavior.PlayerControlled ? getOwner() : null;
	}


	public int getSize() {
		return dataManager.get(SYNC_SIZE);
	}

	public void setSize(int size) {
		dataManager.set(SYNC_SIZE, size);
	}

	@Override
	public Vec3d getExplosionKnockbackMult() {
		return super.getExplosionKnockbackMult().scale(STATS_CONFIG.fireballSettings.explosionSize * getSize() / 15F + getPowerRating() * 0.02);
	}

	@Override
	public double getExplosionHitboxGrowth() {
		return STATS_CONFIG.fireballSettings.explosionSize * getSize() / 15F + getPowerRating() * 0.02;
	}

	@Override
	public void applyElementalContact(AvatarEntity entity) {
		super.applyElementalContact(entity);
		entity.onFireContact();
	}

	@Override
	public boolean shouldExplode() {
		return getBehavior() instanceof FireballBehavior.Thrown;
	}

	@Override
	public boolean shouldDissipate() {
		return false;
	}

	@Override
	public void setDead() {
		super.setDead();
		removeStatCtrl();
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setDamage(nbt.getFloat("Damage"));
		setBehavior((FireballBehavior) Behavior.lookup(nbt.getInteger("Behavior"), this));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setFloat("Damage", getDamage());
		nbt.setInteger("Behavior", getBehavior().getId());
	}

	@Override
	public int getBrightnessForRender() {
		return 150;
	}

	public AxisAlignedBB getExpandedHitbox() {
		return this.expandedHitbox;
	}

	@Override
	public void setEntityBoundingBox(AxisAlignedBB bb) {
		super.setEntityBoundingBox(bb);
		expandedHitbox = bb.grow(0.35, 0.35, 0.35);
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return true;
	}
	// Mostly fixes a glitch where the entity turns invisible

	private void removeStatCtrl() {
		if (getOwner() != null) {
			BendingData data = Objects.requireNonNull(Bender.get(getOwner())).getData();
			if (data != null) {
				data.removeStatusControl(StatusControlController.THROW_FIREBALL);
			}
		}
	}

	@Override
	public boolean isProjectile() {
		return true;
	}


	@SideOnly(Side.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return true;
	}


	@Override
	public ColoredLight produceColoredLight(float partialTicks) {
		return ColoredLight.builder().pos(this).color(1f, 0f, 0f, 1f).radius(10f).build();
	}

}
