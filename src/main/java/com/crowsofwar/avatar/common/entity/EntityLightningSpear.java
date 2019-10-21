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

import com.crowsofwar.avatar.common.bending.lightning.AbilityLightningArc;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.damageutils.DamageUtils;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.LightningFloodFill;
import com.crowsofwar.avatar.common.entity.data.LightningSpearBehavior;
import com.crowsofwar.gorecore.util.Vector;
import elucent.albedo.event.GatherLightsEvent;
import elucent.albedo.lighting.ILightProvider;
import elucent.albedo.lighting.Light;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

import static com.crowsofwar.avatar.common.bending.lightning.StatCtrlThrowLightningSpear.THROW_LIGHTNINGSPEAR;
import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;

/**
 * @author CrowsOfWar
 */
@Optional.Interface(iface = "elucent.albedo.lighting.ILightProvider", modid = "albedo")
public class EntityLightningSpear extends EntityOffensive implements ILightProvider {

	//TODO: Clean up this class. Dear lord.

	private static final DataParameter<LightningSpearBehavior> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityLightningSpear.class, LightningSpearBehavior.DATA_SERIALIZER);

	private static final DataParameter<Float> SYNC_SIZE = EntityDataManager.createKey(EntityLightningSpear.class,
			DataSerializers.FLOAT);

	private static final DataParameter<Float> SYNC_DEGREES_PER_SECOND = EntityDataManager.createKey(EntityLightningSpear.class,
			DataSerializers.FLOAT);


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

	private float Size;



	/**
	 * @param world The world it spawns in
	 */
	public EntityLightningSpear(World world) {
		super(world);
		this.Size = 0.8F;
		setSize(Size, Size);
		this.damage = 3F;
		this.piercing = false;
		this.setInvisible(false);

	}

	@Override
	public int getFireTime() {
		return 0;
	}

	@Override
	public void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_BEHAVIOR, new LightningSpearBehavior.Idle());
		dataManager.register(SYNC_SIZE, Size);
		dataManager.register(SYNC_DEGREES_PER_SECOND, 400F);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		setBehavior((LightningSpearBehavior) getBehavior().onUpdate(this));

		// Add hook or something
		if (getOwner() != null) {
			if (getBehavior() != null && getBehavior() instanceof LightningSpearBehavior.PlayerControlled) {
				this.rotationYaw = this.getOwner().rotationYaw;
				this.rotationPitch = this.getOwner().rotationPitch;
				noClip = true;
			}
			else noClip = false;
		}



		LightningSpearBehavior.PlayerControlled controlled = new LightningSpearBehavior.PlayerControlled();
		if (getOwner() != null) {
			EntityLightningSpear spear = AvatarEntity.lookupControlledEntity(world, EntityLightningSpear.class, getOwner());
			BendingData bD = BendingData.get(getOwner());
			if (spear == null && bD.hasStatusControl(THROW_LIGHTNINGSPEAR)) {
				bD.removeStatusControl(THROW_LIGHTNINGSPEAR);
			}
			if (spear != null && spear.getBehavior().equals(controlled) && !(bD.hasStatusControl(THROW_LIGHTNINGSPEAR))) {
				bD.addStatusControl(THROW_LIGHTNINGSPEAR);
			}

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
		this.setEntitySize(getSize() / 3, getSize() / 3);

	}

	@Override
	public SoundEvent[] getSounds() {
		SoundEvent[] events = new SoundEvent[2];
		events[0] = SoundEvents.ENTITY_LIGHTNING_IMPACT;
		events[1] = SoundEvents.ENTITY_LIGHTNING_THUNDER;
		return events;
	}

	@Override
	public float getAoeDamage() {
		return getDamage() / 10;
	}

	@Override
	public double getExpandedHitboxHeight() {
		return getSize() / 4;
	}

	@Override
	public double getExpandedHitboxWidth() {
		return getSize() / 4;
	}

	@Override
	public DamageSource getDamageSource(Entity target) {
		return AvatarDamageSource.causeLightningSpearDamage(target, getOwner());
	}

	/**
	 * When a lightning spear hits water, electricity spreads through the water and nearby
	 * entities are electrocuted. This method is called when an entity gets electrocuted.
	 */
	private void handleWaterElectrocution(Entity entity) {

		// Uses same DamageSource as lightning arc; this is intentional
		DamageSource damageSource = AvatarDamageSource.causeLightningDamage(entity, getOwner());
		DamageUtils.attackEntity(getOwner(), entity, damageSource, getDamage() / 5, getPerformanceAmount(), getAbility(), getXpPerHit());

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

	public float getSize() {
		return dataManager.get(SYNC_SIZE);
	}

	public void setSize(float size) {
		dataManager.set(SYNC_SIZE, size);
	}

	@Override
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

	public float getDegreesPerSecond() {
		return dataManager.get(SYNC_DEGREES_PER_SECOND);
	}

	public void setDegreesPerSecond(float degrees) {
		dataManager.set(SYNC_DEGREES_PER_SECOND, degrees);
	}

	@Override
	public boolean onCollideWithSolid() {
		if (getBehavior() instanceof LightningSpearBehavior.Thrown)
			return super.onCollideWithSolid();
		else return false;
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

	@Override
	public boolean canBePushed() {
		return piercing;
	}

	@Override
	public void setDead() {
		super.setDead();
		removeStatCtrl();
	}

	@Override
	public boolean onAirContact() {
		if (getAbility() instanceof AbilityLightningArc && !world.isRemote) {
			AbilityData aD = AbilityData.get(getOwner(), "lightning_spear");
			if (!aD.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				this.setDead();
			}
		}
		return true;
	}

	@Override
	public void onCollideWithEntity(Entity entity) {
		if (getBehavior() instanceof LightningSpearBehavior.Thrown && getBehavior() != null)
			super.onCollideWithEntity(entity);
	}

	public void removeStatCtrl() {
		if (getOwner() != null) {
			BendingData data = Objects.requireNonNull(Bender.get(getOwner())).getData();
			data.removeStatusControl(THROW_LIGHTNINGSPEAR);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return true;
	}

	@Override
	public int getBrightnessForRender() {
		return 15728880;
	}

	@Override
	@Optional.Method(modid = "albedo")
	public Light provideLight() {
		return Light.builder().pos(this).color(1F, 2F, 3F).radius(8 + getSize()).build();
	}

	@Override
	@Optional.Method(modid = "albedo")
	public void gatherLights(GatherLightsEvent event, Entity entity) {

	}

	@Override
	public boolean canBeCollidedWith() {
		return !(getBehavior() instanceof LightningSpearBehavior.PlayerControlled);
	}

	@Override
	public boolean canBeAttackedWithItem() {
		return !(getBehavior() instanceof LightningSpearBehavior.PlayerControlled);
	}

	@Override
	public void applyElementalContact(AvatarEntity entity) {
		super.applyElementalContact(entity);
		entity.onLightningContact();
	}

	@Override
	public float getXpPerHit() {
		return SKILLS_CONFIG.lightningspearHit;
	}

	@Override
	public int getPerformanceAmount() {
		return 15;
	}
}
