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

package com.crowsofwar.avatar.util.data;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.AbilityModifier;
import com.crowsofwar.avatar.bending.bending.SourceInfo;
import com.crowsofwar.gorecore.util.GoreCoreByteBufUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.crowsofwar.avatar.config.ConfigSkills.SKILLS_CONFIG;

/**
 * Represents saveable data about an ability. These are not singletons; there is
 * as many instances as required for each player data.
 *
 * @author CrowsOfWar
 */
public class AbilityData {

    //TODO: Infinite scaling??
    public static final int MAX_LEVEL = 3;

    private final BendingData data;
    private final String abilityName;
    private float lastXp;
    private float xp;
    //Infinite scaling bois
    private float abilityPowerLevel;
    //Whether to switch the path of the ability
    private boolean switchPath;
    private int abilityCooldown;
    private double powerRating;
    //How much exhaustion to add to the player (affects the hunger bar, like sprinting or fighting)
    private float exhaustion;
    private boolean shouldRegenBurnout;
    //This is the most small brain solution ever, but we're rewriting later so who really cares
    private int useNumber;
    private SourceInfo sourceInfo;
    //TODO: Move this to the ability class (like properties). It'll massively reduce lag.
    private List<AbilityModifier> modifiers;
    /**
     * Current burnout amount of the ability.
     * <p>
     * Goes from 0 to 100, where 100 is basically half damage and effects.
     * Discourages move spamming.
     * Affects other stats like so:
     * <p>
     * Chi: 0 is base cost, 100 is 1.5x cost.
     * Cooldown: 0 is base cooldown, 100 is 1.5x cooldown.
     * Exhaustion: 0 is base exhaustion, 100 ix 2x exhaustion.
     * </p>
     * </p>
     */
    private float burnOut;
    /**
     * The current level. -1 for locked
     * <p>
     * Note that it starts at 0, so 0 = Level I, 1 = Level II, etc.
     */
    private int level;
    private AbilityTreePath path;

    public AbilityData(BendingData data, Ability ability) {
        this(data, ability.getName());
    }

    public AbilityData(BendingData data, String abilityName) {
        this.data = data;
        this.abilityName = abilityName;
        this.xp = 0;
        this.level = -1;
        this.path = AbilityTreePath.MAIN;
        this.switchPath = false;
        this.abilityCooldown = 0;
        this.burnOut = 0;
        this.exhaustion = 0;
        this.powerRating = 0;
        this.shouldRegenBurnout = true;
        this.useNumber = 1;
        this.sourceInfo = new SourceInfo();
        this.modifiers = new ArrayList<>();
        this.abilityPowerLevel = 1;
    }

    public AbilityData(BendingData data, String abilityName, boolean switchPath) {
        this.data = data;
        this.abilityName = abilityName;
        this.xp = 0;
        this.level = -1;
        this.path = AbilityTreePath.MAIN;
        this.switchPath = switchPath;
        this.abilityCooldown = 0;
        this.burnOut = 0;
        this.exhaustion = 0;
        this.powerRating = 0;
        this.shouldRegenBurnout = true;
        this.useNumber = 1;
        this.sourceInfo = new SourceInfo();
        this.modifiers = new ArrayList<>();
        this.abilityPowerLevel = 1;
    }

    /**
     * Reads ability data from the network.
     */
    public static AbilityData createFromBytes(ByteBuf buf, BendingData data) {
        String abilityName = GoreCoreByteBufUtil.readString(buf);
        AbilityData abilityData = new AbilityData(data, abilityName);
        abilityData.fromBytes(buf);
        return abilityData;
    }

    @Nullable
    public static AbilityData get(EntityLivingBase entity, String abilityName) {
        return BendingData.getFromEntity(entity).getAbilityData(abilityName);
    }

    public static AbilityData get(World world, UUID playerId, String abilityName) {
        return BendingData.get(world, playerId).getAbilityData(abilityName);
    }

    public static AbilityData get(World world, String playerName, String abilityName) {
        return BendingData.get(world, playerName).getAbilityData(abilityName);
    }

    public int getUseNumber() {
        return this.useNumber;
    }

    public void setUseNumber(int number) {
        this.useNumber = number;
    }

    public void setSwitchPath(boolean switchPath) {
        this.switchPath = switchPath;
    }

    public boolean shouldSwitchPath() {
        return switchPath;
    }

    public void setRegenBurnout(boolean regen) {
        this.shouldRegenBurnout = regen;
    }

    public boolean shouldRegenBurnout() {
        return this.shouldRegenBurnout;
    }

    public void addBurnout(float burnout) {
        //Burnout isn't greater than 100 or less than 0
        this.burnOut += MathHelper.clamp(burnout, 0, 100);
    }

    public float getBurnOut() {
        return this.burnOut;
    }

    public void setBurnOut(float burnOut) {
        this.burnOut = burnOut;
        //Burnout isn't greater than 100 or less than 0
        this.burnOut = MathHelper.clamp(this.burnOut, 0, 100);
    }

    public int getAbilityCooldown() {
        return abilityCooldown;
    }

    public void setAbilityCooldown(int cooldown) {
        abilityCooldown = cooldown;
    }

    public int getAbilityCooldown(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) return 0;
        return getAbilityCooldown();
    }

    public Number modify(String property, Number val) {
        //Returns the property's base value/1 by default
        float baseVal = val.floatValue();
//        for (AbilityModifier modifier : getModifiers())
//            baseVal *= modifier.getProperty(property).floatValue();
        return baseVal;
    }

    public void setModifier(List<AbilityModifier> modifiers) {
        this.modifiers = modifiers;
    }

    public List<AbilityModifier> getModifiers() {
        return this.modifiers;
    }

    public void addModifiers(AbilityModifier... modifiers) {
        if (!this.modifiers.contains(modifiers))
            this.modifiers.addAll(Arrays.asList(modifiers.clone()));
    }

    public void removeModifiers(AbilityModifier... modifiers) {
        //mutable ugh
        if (this.modifiers.contains(modifiers))
            this.modifiers.removeAll(Arrays.asList(modifiers.clone()));
    }

    public void clearModifier() {
        this.modifiers = new ArrayList<>();
    }

    public void decrementCooldown() {
        abilityCooldown--;
    }

    public SourceInfo getSourceInfo() {
        return this.sourceInfo;
    }

    public void setSourceInfo(SourceInfo info) {
        this.sourceInfo = info;
    }

    public IBlockState getSourceBlock() {
        return this.sourceInfo.getBlockState();
    }

    public void setSourceBlock(IBlockState state) {
        this.sourceInfo.setState(state);
    }

    public void clearSourceBlock() {
        this.sourceInfo.setState(Blocks.AIR.getDefaultState());
    }

    public int getSourceTime() {
        return sourceInfo.getTime();
    }

    public void setSourceTime(int time) {
        this.sourceInfo.setTime(time);
    }

    public void incrementSourceTime() {
        setSourceTime(getSourceTime() + 1);
    }

    public void clearSourceTime() {
        this.sourceInfo.setTime(-1);
    }

    public double getPowerRating() {
        return powerRating;
    }

    public void setPowerRating(double power) {
        this.powerRating = power;
    }

    /**
     * Gets the power rating, but in the range 0.5 to 1.5 for convenience in damage calculations.
     * <ul>
     * <li>-100 power rating gives 0.5; damage would be 1/2 of normal</li>
     * <li>0 power rating gives 1; damage would be the same as normal</li>
     * <li>100 power rating gives 1.5; damage would be 1.5 times as much as usual</li>
     * Powerrating goes from -1000 to 1000, to allow for insane buffs (avatar).
     */
    //NOTE: Unlike the other methods, this works server-side and client-side! Use this in abilities and such.
    public double getDamageMult() {
        double powerRating = getPowerRating();
        if (powerRating < 0) {
            return 0.005 * powerRating + 1 < 0 ? 1 / 50F : 0.005 * powerRating + 1;
        } else {
            return 0.005 * powerRating + 1;
        }
    }


    //NOTE: Unlike the other methods, this works server-side and client-side! Use this in abilities and such.
    public double getPowerRatingMult() {
        double powerRating = getPowerRating();
        if (powerRating < 0) {
            return 0.01 * powerRating + 1 < 0 ? 1 / 100F : 0.01 * powerRating + 1;
        } else {
            return 0.01 * powerRating + 1;
        }
    }

    @Nullable
    public Ability getAbility() {
        return Abilities.get(abilityName);
    }

    public String getAbilityName() {
        return abilityName;
    }

    /**
     * Get the current level of this ability. A value of -1 indicates this
     * ability is {@link #isLocked() locked}.
     * <p>
     * Starts at 0, so 0 is level I, 1 is level II, etc.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets the current level of the ability to the given amount. Clamps between
     * 0 and MAX_LEVEL. Marks dirty, but does not sync.
     */
    public void setLevel(int level) {
        if (level < -1) level = -1;
        if (level > MAX_LEVEL) level = MAX_LEVEL;
        this.level = level;
        checkPath();
        save();
    }

    /**
     * Increments the level by 1 and syncs the level. If the ability is locked,
     * unlocks the ability. Does not allow going past the maximum level.
     */
    public void addLevel() {
        setLevel(level + 1);
    }

    /**
     * Gets a string representing both the level, and the path (if this is upgraded to level IV).
     * For example, "lvl2" or "lvl4_1"; levels reported here start at 1.
     */
    public String getLevelDesc() {
        String str = "lvl" + (level + 1);
        if (level == 3) {
            str += "_" + path.ordinal();
        }
        return str;
    }

    /**
     * Get the current path we are on. Note that while path other than MAIN only
     * matters in level IV, it can be other than MAIN on levels I,II, or III,
     * but will be ignored. On level IV however, path cannot be MAIN and will
     * either be FIRST or SECOND.
     */
    public AbilityTreePath getPath() {
        return path;
    }

    /**
     * Set the current path. For details about valid values, see
     * {@link #getPath()}.
     */
    public void setPath(AbilityTreePath path) {
        this.path = path;
        checkPath();
        save();
    }

    /**
     * Checks whether is at level IV and has chosen the given path.
     */
    public boolean isMasterPath(AbilityTreePath path) {
        return isMaxLevel() && this.path == path;
    }

    /**
     * Same as isMasterLevel(), but accounts for a dynamic change
     */
    public boolean isDynamicMasterLevel(AbilityTreePath path) {
        return getLevel() == 3 && getDynamicPath() == path;
    }

    /**
     * Ensures ability path is correct - on level 4, if still on MAIN path, will
     * switch to FIRST automatically
     */
    private void checkPath() {
        if (level == 3 && path == AbilityTreePath.MAIN) {
            setPath(AbilityTreePath.FIRST);
        }
    }


    /*
     * Same as getPath(), but accounts for a dynamic change
     */
    public AbilityTreePath getDynamicPath() {
        AbilityTreePath currentPath = getPath();
        if (switchPath) {
            if (currentPath == AbilityTreePath.FIRST) {
                return AbilityTreePath.SECOND;
            } else if (currentPath == AbilityTreePath.SECOND) {
                return AbilityTreePath.FIRST;
            } else {
                return AbilityTreePath.MAIN;
            }
        } else {
            return currentPath;
        }
    }

    /**
     * Gets the total experience value from 0-100, taking into account the level
     * AND current xp. Maximum value is 136.
     */
    public float getTotalXp() {
        return level * 33 + xp * 33F / 100;
    }

    /**
     * @return Returns a modifier based on the current xp, from 1 to 1.36.
     */
    public float getXpModifier() {
        return (float) Math.min(getTotalXp() / 100 < 1 ? 1 : getTotalXp() / 100, 1.36);
    }

    public float getXp() {
        return xp;
    }

    /**
     * Sets the XP level to the given amount, clamping from 0-100. If more than
     * 100, won't to next level. Will also save the AvatarPlayerData.
     */
    public void setXp(float xp) {
        if (xp == this.xp) return;
        this.lastXp = this.xp;

        if (xp < 0) xp = 0;
        if (xp > 100) {
            xp = 100;
        }

        this.xp = xp;
        save();

    }

    /**
     * Add XP to this ability data. Will be {@link #getXpMultiplier()
     * multiplied} for exponential decay.
     */
    public void addXp(float xp) {

        xp *= getXpMultiplier();
        if (xp == 0) {
            if (SKILLS_CONFIG.abilitySettings.infiniteScaling ||
                    getAbilityPower() < SKILLS_CONFIG.abilitySettings.maxScaleLevel)
                addAbilityPower(xp);
            return;
        }
        setXp(this.xp + xp);

    }

    /**
     * Gets the previous XP value before the last time {@link #setXp(float)} or {@link #addXp(float)}
     * was called. Not synced to client.
     */
    public float getLastXp() {
        return lastXp;
    }

    /**
     * Resets the previous XP value so it is the same as the current one.
     */
    public void resetLastXp() {
        lastXp = xp;
    }

    public boolean isMaxLevel() {
        return level >= MAX_LEVEL;
    }

    /**
     * Gets the multiplier applied to any XP gains. Tends to lower faster near
     * ends of levels, and also is lower as the level increases. The minimum
     * value is .5 (unless on level 3).
     */
    public float getXpMultiplier() {
        float x = xp / 100;
        if (level == 0) {
            return 1 - .2f * x * x;
        }
        if (level == 1) {
            return .8f - .2f * (x - 1) * (x - 1);
        }
        if (level == 2) {
            return .6f - .1f * (x - 2) * (x - 2);
        }
        return 0;
    }

    /**
     *
     * @return The current power level of the ability. Independent from the configurable power level,
     * this infinitely increases if enabled in the config.
     */
    public float getAbilityPower() {
        return this.abilityPowerLevel;
    }

    /**
     * @param xp The xp used to increase the power level; called independently by the ability elsewhere.
     *           Abilities will keep gaining in power after you hit the level limit,
     *           if enabled in the config. Calculation copied from normally adding xp, but adjusted slightly.
     */
    public void addAbilityPower(float xp) {
        float x = xp / 100;
        this.abilityPowerLevel += 0.07f * Math.sqrt(x / 10);

        //May need to change later but eh
        if (abilityPowerLevel < 1)
            abilityPowerLevel = 1;
    }

    public void clearAbilityPower() {
        this.abilityPowerLevel = 1;
    }
    /**
     * Returns whether this ability is locked and the player cannot use it.
     */
    public boolean isLocked() {
        return level == -1;
    }

    /**
     * If this ability is {@link #isLocked() locked}, unlocks the ability.
     */
    public void unlockAbility() {
        if (isLocked()) {
            level = 0;
        }
    }

    public int getCurrentTier() {
        int tier = 0;
        if (getAbility() != null) {
            tier += getAbility().getBaseTier();
            switch (level) {
                default:
                    break;
                case 2:
                    tier++;
                    break;
                case 3:
                    tier += 2;
                    break;
            }
        }
        return tier;
    }

    public void readFromNbt(NBTTagCompound nbt) {
        xp = nbt.getFloat("Xp");
        lastXp = nbt.getFloat("lastXp");
        level = nbt.getInteger("Level");
        path = AbilityTreePath.get(nbt.getInteger("Path"));
        abilityCooldown = nbt.getInteger("AbilityCooldown");
        powerRating = nbt.getDouble("PowerRating");
        exhaustion = nbt.getFloat("Exhaustion");
        burnOut = nbt.getFloat("Burnout");
        shouldRegenBurnout = nbt.getBoolean("RegenBurnout");
        useNumber = nbt.getInteger("Jumps");
        sourceInfo = sourceInfo.readFromNBT(nbt);

        nbt.setInteger("Modifier Size", modifiers.size());
        for (AbilityModifier modifier : modifiers)
            modifier.toNBT(nbt);

        abilityPowerLevel = nbt.getFloat("AbilityPowerLevel");
    }

    public void writeToNbt(NBTTagCompound nbt) {
        nbt.setFloat("Xp", xp);
        nbt.setFloat("LastXp", lastXp);
        nbt.setInteger("Level", level);
        nbt.setInteger("Path", path.id());
        nbt.setInteger("AbilityCooldown", abilityCooldown);
        nbt.setDouble("PowerRating", powerRating);
        nbt.setFloat("Exhaustion", exhaustion);
        nbt.setFloat("Burnout", burnOut);
        nbt.setBoolean("RegenBurnout", shouldRegenBurnout);
        nbt.setInteger("Jumps", useNumber);
        sourceInfo.writeToNBT(nbt);

        int size = nbt.getInteger("Modifier Size");
        modifiers.clear();
        for (int i = 0; i < size; i++) {
            modifiers.add(i, AbilityModifier.staticFromNBT(nbt));
        }

        nbt.setFloat("AbilityPowerLevel", abilityPowerLevel);
    }

    public void toBytes(ByteBuf buf) {
        GoreCoreByteBufUtil.writeString(buf, abilityName);
        buf.writeFloat(xp);
        buf.writeInt(level);
        buf.writeInt(path.id());
        buf.writeInt(abilityCooldown);
        buf.writeDouble(powerRating);
        buf.writeFloat(exhaustion);
        buf.writeFloat(burnOut);
        buf.writeBoolean(shouldRegenBurnout);
        buf.writeInt(useNumber);
        sourceInfo.writeToBytes(buf);
        buf.writeInt(modifiers.size());
        if (!modifiers.isEmpty()) {
            for (AbilityModifier modifier : modifiers)
                modifier.toBytes(buf);
        }
        buf.writeFloat(abilityPowerLevel);
    }

    private void fromBytes(ByteBuf buf) {
        xp = buf.readFloat();
        level = buf.readInt();
        path = AbilityTreePath.get(buf.readInt());
        abilityCooldown = buf.readInt();
        powerRating = buf.readDouble();
        exhaustion = buf.readFloat();
        burnOut = buf.readFloat();
        shouldRegenBurnout = buf.readBoolean();
        useNumber = buf.readInt();
        sourceInfo = sourceInfo.readFromBytes(buf);
        int size = buf.readInt();
        if (size > 0) {
            modifiers.clear();
            for (int i = 0; i < size; i++) {
                modifiers.add(i, new AbilityModifier().fromBytes(buf));
            }
        }
        abilityPowerLevel = buf.readFloat();
    }

    /**
     * Saves but does not sync
     */
    private void save() {
        data.save(DataCategory.ABILITY_DATA);
    }

    /**
     * Describes which path the abilityData is currently taking. Main path is
     * for levels 1, 2, and 3. For level 4, either FIRST or SECOND path can be
     * chosen.
     */
    public enum AbilityTreePath {

        MAIN,
        FIRST,
        SECOND;

        @Nullable
        public static AbilityTreePath get(int id) {
            if (id < 0 || id >= values().length) {
                return null;
            }
            return values()[id];
        }

        public int id() {
            return ordinal();
        }

    }

}
