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
package com.crowsofwar.avatar.common.entity.mob;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BenderEntityComponent;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import javax.annotation.Nullable;

import static com.crowsofwar.avatar.common.config.ConfigMobs.MOBS_CONFIG;

/**
 * @author CrowsOfWar
 */
public abstract class EntityBender extends EntityCreature implements IEntityAdditionalSpawnData {

	private Bender bender;
	private static final DataParameter<Integer> SYNC_LEVEL = EntityDataManager
			.createKey(EntityBender.class, DataSerializers.VARINT);

	/**
	 * @param world
	 */
	public EntityBender(World world) {
		super(world);
	}

	protected Bender initBender() {
		return new BenderEntityComponent(this);
	}

	public int getLevel() {
		return dataManager.get(SYNC_LEVEL);
	}

	public void setLevel(int level) {
		dataManager.set(SYNC_LEVEL, level);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		// Initialize the bender here (instead of constructor) so the bender will be ready for
		// initEntityAI - Constructor is called AFTER initEntityAI
		bender = initBender();
		dataManager.register(SYNC_LEVEL, 1);
		applyAbilityLevels(getLevel());
		getData().addBending(getElement());
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		applyAbilityLevels(getLevel());
		getData().addBending(getElement());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setLevel(nbt.getInteger("Level"));
		bender.getData().readFromNbt(nbt);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("Level", getLevel());
		bender.getData().writeToNbt(nbt);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		bender.onUpdate();
	}

	public abstract void applyAbilityLevels(int level);

	public Bender getBender() {
		return bender;
	}

	public BendingData getData() {
		return bender.getData();
	}

	//Used for changing stuff like the size of an air bubble or something. Usually called right
	//before an entity is spawned
	public void modifyAbilities(Ability ability) {

	}


	public BendingStyle getElement() {
		return new Airbending();
	}

	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		setLevel(AvatarUtils.getRandomNumberInRange(1, MOBS_CONFIG.benderSettings.maxLevel));
		applyAbilityLevels(getLevel());
		getData().addBending(getElement());
		return super.onInitialSpawn(difficulty, livingdata);
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(getLevel());
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		setLevel(additionalData.readInt());
	}
}
