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

package com.crowsofwar.avatar.bending.bending.water;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingAi;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.EntityWave;
import com.crowsofwar.avatar.entity.data.Behavior;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Are you proud of me, dad? I'm outlining how this should work in the documentation.
 * I'm actually following conventions. Praise be!
 * <p>
 * Wave -- Waterbending
 * This ability is designed for mobility and offense. It's a staple of waterbenders, and allows them
 * to dominate in water, snow, or rain. Charge up to create a bigger wave! As with most waterbending abilities,
 * it can be frozen and electrocuted.
 * Rideable.
 * <p>
 * Level 1 - Simple Wave.
 * Level 2 - Faster, Stronger, Bigger, Cooler. Ya know. Pierces. Works on land (with applicable water source nearby).
 * Level 3 - It's now rideable! Woo! Bigger source radius! Drawn from plants!
 * Level 4 Path 1 : Sundering Tsunami - Waves are now closer to geysers of water in a line. Think ravine but water and stronger.
 * Level 4 Path 2 : Voluminous Falls - Waves are now thicker, and can be controlled/charged into shapes (walls)!
 *
 * @author CrowsofWar, FavouriteDragon (mainly me)
 *
 * TODO: Finish property file
 */
public class AbilityCreateWave extends Ability {

    public static final String
            GEYSER = "geysers",
            CHARGEABLE = "chargeable",
            RIDEABLE = "rideable",
            LAND = "land";

    public AbilityCreateWave() {
        super(Waterbending.ID, "wave");
    }

    @Override
    public void init() {
        super.init();
        addProperties(SOURCE_ANGLES, SOURCE_RANGE, WATER_AMOUNT);
        addBooleanProperties(GEYSER, CHARGEABLE, RIDEABLE, PLANT_BEND, LAND);
    }

    @Override
    public void execute(AbilityContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();
        AbilityCreateWave abilityWave = (AbilityCreateWave) Abilities.get("wave");
        AbilityData abilityData = ctx.getAbilityData();

        Vector look = Vector.getLookRectangular(entity);
        Vector pos = Vector.getEntityPos(entity);
        if (bender.consumeChi(getChiCost(ctx)) && abilityWave != null) {
            if (ctx.consumeWater(getProperty(WATER_AMOUNT, ctx).intValue())) {
                //Entity damage values and such go here
                float damage = getProperty(DAMAGE, ctx).floatValue();
                float speed = getProperty(SPEED, ctx).floatValue() * 5;
                int lifetime = getProperty(LIFETIME, ctx).intValue();
                float push = getProperty(KNOCKBACK, ctx).floatValue() / 2;
                float size = getProperty(SIZE, ctx).floatValue() / 2;

                damage = powerModify(damage, abilityData);
                speed = powerModify(speed, abilityData);
                lifetime = (int) powerModify(lifetime, abilityData);
                push = powerModify(push, abilityData);
                size = powerModify(size, abilityData);

                //Logic for spawning the wave
                Vector firstPos = pos.plus(look).minusY(1);
                Vector secondPos = firstPos.minusY(1);
                BlockPos pos1 = firstPos.toBlockPos();
                BlockPos pos2 = secondPos.toBlockPos();
                boolean firstBendable;
                boolean secondBendable;

                //Either the wave can go on land or there's a compatible block to use
                firstBendable = Waterbending.isBendable(abilityWave, world.getBlockState(pos1),
                        entity);
                firstBendable |= getBooleanProperty(LAND, ctx) && world.getBlockState(pos1).isFullBlock();
                secondBendable = Waterbending.isBendable(abilityWave, world.getBlockState(pos2),
                        entity);
                secondBendable |= getBooleanProperty(LAND, ctx) && world.getBlockState(pos2).isFullBlock();

                EntityWave wave = new EntityWave(world);
                wave.setOwner(entity);
                wave.setRunOnLand(getBooleanProperty(LAND, ctx));
                wave.setRideable(getBooleanProperty(RIDEABLE, ctx));
                wave.setDamage(damage);
                wave.setDistance(lifetime * speed / 10);
                wave.setAbility(this);
                wave.setPush(push);
                wave.setLifeTime(lifetime);
                wave.setTier(getCurrentTier(ctx));
                wave.setEntitySize(size * 0.75F, size * 1.5F);
                wave.setXp(getProperty(XP_HIT).floatValue());
                wave.rotationPitch = entity.rotationPitch;
                wave.rotationYaw = entity.rotationYaw;
                if (getBooleanProperty(GEYSER, ctx))
                    wave.setBehaviour(new WaveGeyserBehaviour());


                if (getBooleanProperty(CHARGEABLE, ctx)) {
                    //Add a tick handler/stat ctrl for charging
                }
                if (getBooleanProperty(RIDEABLE, ctx)) {
                    //Add a status control here
                }

                //Block at feet is bendable
                if (firstBendable) {
                    //Pos is the source block, we want it to be above the source block
                    wave.setPosition(firstPos.plusY(1));
                }
                //Block below feet is bendable
                else if (secondBendable) {
                    //Same thing here. Above source block.
                    wave.setPosition(secondPos.plusY(1));
                }
                //If the blocks beneath the player's feet aren't bendable
                else {
                    firstPos = Waterbending.getClosestWaterbendableBlock(entity,
                            abilityWave, ctx);

                    if (firstPos != null) {
                        pos1 = firstPos.toBlockPos();

                        firstBendable = Waterbending.isBendable(abilityWave, world.getBlockState(pos1),
                                entity);
                        if (firstBendable) {
                            wave.setPosition(firstPos);
                        }
                    }
                }

                if (!world.isRemote && (firstBendable || secondBendable))
                    world.spawnEntity(wave);

            } else bender.sendMessage("avatar.waterSourceFail");
        }

        super.execute(ctx);
    }

    @Override
    public int getBaseTier() {
        return 2;
    }

    @Override
    public boolean isProjectile() {
        return true;
    }

    @Override
    public boolean isOffensive() {
        return true;
    }

    @Override
    public BendingAi getAi(EntityLiving entity, Bender bender) {
        return new AiWave(this, entity, bender);
    }

    //Makes it spawn a line of geysers!
    public static class WaveGeyserBehaviour extends OffensiveBehaviour {

        @Override
        public Behavior onUpdate(EntityOffensive entity) {
            return this;
        }

        @Override
        public void fromBytes(PacketBuffer buf) {

        }

        @Override
        public void toBytes(PacketBuffer buf) {

        }

        @Override
        public void load(NBTTagCompound nbt) {

        }

        @Override
        public void save(NBTTagCompound nbt) {

        }
    }

}
