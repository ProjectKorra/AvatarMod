package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.bending.avatar.AbilityElementshard;
import com.crowsofwar.avatar.common.bending.avatar.StatCtrlThrowElementshard;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.ElementshardBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
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
import org.lwjgl.Sys;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class EntityElementshard extends AvatarEntity {
	public static final DataParameter<ElementshardBehavior> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityElementshard.class, ElementshardBehavior.DATA_SERIALIZER);

	public static final DataParameter<Integer> SYNC_SIZE = EntityDataManager.createKey(EntityElementshard.class,
			DataSerializers.VARINT);

	private AxisAlignedBB expandedHitbox;
	private static boolean noShards;
	public void havenoShards(boolean shards) {
		this.noShards = shards;
	}
	private float damage;

	/**
	 * @param world
	 */
	public EntityElementshard(World world) {
		super(world);
		setSize(.2f, .2f);
	}

	@Override
	public void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_BEHAVIOR, new ElementshardBehavior.Idle());
		dataManager.register(SYNC_SIZE, 30);
	}

	@Override
	public void onUpdate() {

		super.onUpdate();
		setBehavior((ElementshardBehavior) getBehavior().onUpdate(this));

		// Add hook or something
		if (getOwner() == null) {
			setDead();
			if (noShards){
				removeStatCtrl();
			}

		}

	}

	/*@Override
	public void setDead() {
		super.setDead();
		if (!world.isRemote && this.isDead) {
			Thread.dumpStack();
		}
	}**/



	public ElementshardBehavior getBehavior() {
		return dataManager.get(SYNC_BEHAVIOR);
	}

	public void setBehavior(ElementshardBehavior behavior) {
		dataManager.set(SYNC_BEHAVIOR, behavior);
	}

	@Override
	public EntityLivingBase getController() {
		return getBehavior() instanceof ElementshardBehavior.PlayerControlled ? getOwner() : null;
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

	@Override
	protected void onCollideWithEntity(Entity entity) {
		if (entity instanceof AvatarEntity && !(entity instanceof EntityElementshard)) {
			((AvatarEntity) entity).onCollideWithSolid();
			//shardsLeft --;
		}
	}

	@Override
	public boolean onCollideWithSolid() {



		/*float explosionSize = STATS_CONFIG.fireballSettings.explosionSize;
		explosionSize *= getSize() / 30f;
		explosionSize += getPowerRating() * 2.0 / 100;
		boolean destroyObsidian = false;

		if (getOwner() != null) {
			AbilityData abilityData = BendingData.get(getOwner())
					.getAbilityData("element_shard");
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				destroyObsidian = true;
			}
		}
**/
		/*Explosion explosion = new Explosion(world, this, posX, posY, posZ, explosionSize,
				!world.isRemote, STATS_CONFIG.fireballSettings.damageBlocks);
		if (!ForgeEventFactory.onExplosionStart(world, explosion)) {

			explosion.doExplosionA();
			explosion.doExplosionB(true);

		}**/


		setDead();
		return true;

	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setDamage(nbt.getFloat("Damage"));
		setBehavior((ElementshardBehavior) Behavior.lookup(nbt.getInteger("Behavior"), this));
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
		return pass == 0 || pass == 1;
	}

	private void removeStatCtrl() {
		if (getOwner() != null && noShards) {
			System.out.println(noShards);
			BendingData data = Bender.get(getOwner()).getData();
			data.removeStatusControl(StatusControl.THROW_ELEMENTSHARD);
			noShards = false;
		}
	}

	@Override
	public boolean isProjectile() {
		return true;
	}

}
