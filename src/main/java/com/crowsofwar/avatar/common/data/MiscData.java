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

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.ctx.NoBenderInfo;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import javax.annotation.Nonnull;
import java.util.HashMap;

/**
 * @author CrowsOfWar
 */
public class MiscData {

	private final Runnable save;
	private final HashMap<String, Integer> abilityCooldowns;
	private float fallAbsorption;
	private int timeInAir;
	private boolean wallJumping;
	private int petSummonCooldown;
	private boolean bisonFollowMode;
	private boolean canUseAbilities;
	private BenderInfo redirectionSource;

	public MiscData(Runnable save) {
		this.save = save;
		this.bisonFollowMode = true;
		this.canUseAbilities = true;
		this.redirectionSource = new NoBenderInfo();
		this.abilityCooldowns = new HashMap<>();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeFloat(fallAbsorption);
		buf.writeInt(timeInAir);
		buf.writeInt(abilityCooldowns.size());
		abilityCooldowns.keySet().iterator().forEachRemaining(ability -> {
			buf.writeInt(ability.length());
			new PacketBuffer(buf).writeString(ability);
			buf.writeInt(abilityCooldowns.get(ability));
		});
		buf.writeBoolean(wallJumping);
		buf.writeInt(petSummonCooldown);
		buf.writeBoolean(bisonFollowMode);
		buf.writeBoolean(canUseAbilities);
	}

	public void fromBytes(ByteBuf buf) {
		fallAbsorption = buf.readFloat();
		timeInAir = buf.readInt();
		for (int i = 0; i < buf.readInt(); i++) {
			int length = buf.readInt();
			abilityCooldowns.replace(new PacketBuffer(buf).readString(length), buf.readInt());
		}
		wallJumping = buf.readBoolean();
		petSummonCooldown = buf.readInt();
		bisonFollowMode = buf.readBoolean();
		canUseAbilities = buf.readBoolean();
	}

	public void readFromNbt(NBTTagCompound nbt) {
		fallAbsorption = nbt.getFloat("FallAbsorption");
		timeInAir = nbt.getInteger("TimeInAir");
		for (int i = 0; i < nbt.getInteger("AbilityCooldownSize"); i++) {
			abilityCooldowns.replace(nbt.getString("Ability " + abilityCooldowns.keySet().toArray()[i]),
					nbt.getInteger("Cooldown " + abilityCooldowns.keySet().toArray()[i]));
		}
		wallJumping = nbt.getBoolean("WallJumping");
		petSummonCooldown = nbt.getInteger("PetSummonCooldown");
		bisonFollowMode = nbt.getBoolean("BisonFollowMode");
		// Use key ending with "a4.6" because there was a bug in a4.5 where canUseAbilities
		// wasn't correctly flagged to false.
		// This new key forces glitched players from a4.5 to reload their CanUseAbilities flag
		canUseAbilities = nbt.getBoolean("CanUseAbilitiesA4.6");
		if (!nbt.hasKey("CanUseAbilitiesA4.6")) {
			canUseAbilities = true;
		}
		redirectionSource = BenderInfo.readFromNbt(nbt);
	}

	public void writeToNbt(NBTTagCompound nbt) {
		nbt.setFloat("FallAbsorption", fallAbsorption);
		nbt.setInteger("TimeInAir", timeInAir);
		nbt.setInteger("AbilityCooldownSize", abilityCooldowns.size());
		abilityCooldowns.keySet().iterator().forEachRemaining(ability -> {
			nbt.setString("Ability " + ability, ability);
			nbt.setInteger("Cooldown " + ability, abilityCooldowns.get(ability));
		});
		nbt.setBoolean("WallJumping", wallJumping);
		nbt.setInteger("PetSummonCooldown", petSummonCooldown);
		nbt.setBoolean("BisonFollowMode", bisonFollowMode);
		nbt.setBoolean("CanUseAbilitiesA4.6", canUseAbilities);
		redirectionSource.writeToNbt(nbt);
	}

	public float getFallAbsorption() {
		return fallAbsorption;
	}

	public void setFallAbsorption(float fallAbsorption) {
		if (fallAbsorption == 0 || fallAbsorption > this.fallAbsorption)
			this.fallAbsorption = fallAbsorption;
	}

	public void addFallAbsorption(float fallAbsorption) {
		if (fallAbsorption < 0) fallAbsorption = 0;
		this.fallAbsorption += fallAbsorption;
	}

	public int getTimeInAir() {
		return timeInAir;
	}

	public void setTimeInAir(int time) {
		this.timeInAir = time;
	}

	public int getAbilityCooldown(String ability) {
		return abilityCooldowns.getOrDefault(ability, 0);
	}

	public void setAbilityCooldown(String ability, int cooldown) {
		if (cooldown < 0) cooldown = 0;
		if (abilityCooldowns.containsKey(ability))
			abilityCooldowns.replace(ability, cooldown);
		else abilityCooldowns.put(ability, cooldown);
		save.run();
	}

	public void decrementCooldown() {
		for (String ability : abilityCooldowns.keySet()) {
			setAbilityCooldown(ability, getAbilityCooldown(ability) - 1);
		}
	}

	public boolean isWallJumping() {
		return wallJumping;
	}

	public void setWallJumping(boolean wallJumping) {
		this.wallJumping = wallJumping;
	}

	public int getPetSummonCooldown() {
		return petSummonCooldown;
	}

	public void setPetSummonCooldown(int petSummonCooldown) {
		this.petSummonCooldown = petSummonCooldown;
	}

	public boolean getBisonFollowMode() {
		return bisonFollowMode;
	}

	public void setBisonFollowMode(boolean followMode) {
		bisonFollowMode = followMode;
	}

	public boolean getCanUseAbilities() {
		return canUseAbilities;
	}

	public void setCanUseAbilities(boolean canUseAbilities) {
		this.canUseAbilities = canUseAbilities;
	}

	@Nonnull
	public BenderInfo getRedirectionSource() {
		return redirectionSource;
	}

	public void setRedirectionSource(@Nonnull BenderInfo redirectionSource) {
		if (redirectionSource == null) {
			redirectionSource = new NoBenderInfo();
		}
		this.redirectionSource = redirectionSource;
	}

}