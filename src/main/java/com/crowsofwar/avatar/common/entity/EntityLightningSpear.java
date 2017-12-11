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
import com.crowsofwar.avatar.common.bending.BattlePerformance;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.LightningFloodFill;
import com.crowsofwar.avatar.common.entity.data.LightningSpearBehavior;
import com.crowsofwar.gorecore.util.Vector;
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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 *
 *
 * @author CrowsOfWar
 */
public class EntityLightningSpear extends AvatarEntity {

	public static final DataParameter<LightningSpearBehavior> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityLightningSpear.class, LightningSpearBehavior.DATA_SERIALIZER);

	public static final DataParameter<Integer> SYNC_SIZE = EntityDataManager.createKey(EntityLightningSpear.class,
			DataSerializers.VARINT);

	private AxisAlignedBB expandedHitbox;

	private float damage;

	/**
	 * Whether the lightning spear can continue through multiple enemies, instead of being destroyed
	 * upon hitting one.
	 */
	private boolean piercing;

    /**
     * Upon hitting an enemy, whether to damage any additional enemies next to the hit target.
     */
    private boolean groupAttack;

	/**
	 * Handles electrocution of nearby entities when the lightning spear touches water
	 */
	private LightningFloodFill floodFill;

	/**
	 * @param world
	 */
	public EntityLightningSpear(World world) {
		super(world);
		setSize(.8f, .8f);
	}

	@Override
	public void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_BEHAVIOR, new LightningSpearBehavior.Idle());
		dataManager.register(SYNC_SIZE, 30);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		LightningSpearBehavior.PlayerControlled controlled = new LightningSpearBehavior.PlayerControlled();
		setBehavior((LightningSpearBehavior) getBehavior().onUpdate(this));
		if (this.isDead){
			removeStatCtrl();
		}
		// TODO Temporary fix to avoid extra fireballs
		// Add hook or something
		if (getOwner() == null) {
			if (this.getBehavior() == controlled) {
			this.rotationYaw = this.getOwner().rotationYaw;
			this.rotationPitch = this.getOwner().rotationPitch;
			}
			setDead();
			removeStatCtrl();
		}

		// Electrocute enemies in water
		if (inWater) {

			// When in the water, lightning spear should disappear, but also keep
			// electrocuting entities. If the lightning spear was simply removed, flood fill
			// processing (i.e. electrocution) would end, so don't do that. Instead make it
			// invisible and remove once process is complete.
			// A hack but it works :\
			setInvisible(true);
			setVelocity(Vector.ZERO);

		}
		if (inWater && !world.isRemote) {
			if (floodFill == null) {
				floodFill = new LightningFloodFill(world, getPosition(), 12,
						this::handleWaterElectrocution);
			}
			if (floodFill.tick()) {
				// Remove lightning spear when it's finished electrocuting
				setDead();
			}
		}

	}

	/**
	 * When a lightning spear hits water, electricity spreads through the water and nearby
	 * entities are electrocuted. This method is called when an entity gets electrocuted.
	 */
	private void handleWaterElectrocution(Entity entity) {

		// Uses same DamageSource as lightning arc; this is intentional
		DamageSource damageSource = AvatarDamageSource.causeLightningDamage(entity, getOwner());

		entity.attackEntityFrom(damageSource, damage / 2);

		BattlePerformance.addLargeScore(getOwner());

	}

	public LightningSpearBehavior getBehavior() {
		return dataManager.get(SYNC_BEHAVIOR);
	}

	public void setBehavior(LightningSpearBehavior behavior) {
		dataManager.set(SYNC_BEHAVIOR, behavior);
	}

	@Override
	public EntityLivingBase getController() {
		return getBehavior() instanceof LightningSpearBehavior.PlayerControlled ? getOwner() : null;
	}

	public float getDamage() {
		return damage;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

	public int getSize() {
		return dataManager.get(SYNC_SIZE);
	}

	public void setSize(int size) {
		dataManager.set(SYNC_SIZE, size);
	}

    public boolean isPiercing() {
        return piercing;
    }

    public void setPiercing(boolean piercing) {
        this.piercing = piercing;
    }

    public boolean isGroupAttack() {
        return groupAttack;
    }

    public void setGroupAttack(boolean groupAttack) {
        this.groupAttack = groupAttack;
    }

    @Override
	public boolean onCollideWithSolid() {

		if (!(getBehavior() instanceof LightningSpearBehavior.Thrown)) {
			return false;
		}

		float explosionSize = STATS_CONFIG.fireballSettings.explosionSize;
		explosionSize *= getSize() / 30f;
		boolean destroyObsidian = false;

		if (getOwner() != null) {
			AbilityData abilityData = BendingData.get(getOwner())
					.getAbilityData("lightning_spear");
			if (abilityData.isMasterPath(AbilityTreePath.FIRST)) {
				destroyObsidian = false;
			}
		}

		Explosion explosion = new Explosion(world, this, posX, posY, posZ, explosionSize,
				!world.isRemote, STATS_CONFIG.fireballSettings.damageBlocks);
		if (!ForgeEventFactory.onExplosionStart(world, explosion)) {

			explosion.doExplosionA();
			explosion.doExplosionB(true);

		}

		if (destroyObsidian) {
			for (EnumFacing dir : EnumFacing.values()) {
				BlockPos pos = getPosition().offset(dir);
				if (world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN) {
					world.destroyBlock(pos, true);
				}
			}
		}

		world.playSound(posX, posY, posZ, SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory
				.PLAYERS, 1, 1, false);

		setDead();
		return true;

	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setDamage(nbt.getFloat("Damage"));
		setBehavior((LightningSpearBehavior) Behavior.lookup(nbt.getInteger("Behavior"), this));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setFloat("Damage", getDamage());
		nbt.setInteger("Behavior", getBehavior().getId());
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
		return pass == 1;
	}

	private void removeStatCtrl() {
		if (getOwner() != null) {
			BendingData data = Bender.get(getOwner()).getData();
			data.removeStatusControl(StatusControl.THROW_LIGHTNINSPEAR);
		}
	}

}
