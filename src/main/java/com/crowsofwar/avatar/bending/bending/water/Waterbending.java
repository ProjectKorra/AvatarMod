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

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.client.gui.BendingMenuInfo;
import com.crowsofwar.avatar.client.gui.MenuTheme;
import com.crowsofwar.avatar.client.gui.MenuTheme.ThemeColor;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.awt.*;
import java.util.UUID;
import java.util.function.BiPredicate;

import static com.crowsofwar.avatar.bending.bending.Ability.*;
import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;
import static java.lang.Math.toRadians;

public class Waterbending extends BendingStyle {

    public static final UUID ID = UUID.fromString("33486f81-29cc-4f7e-84ee-972a73b03b95");

    private final BendingMenuInfo menu;

    public Waterbending() {
        registerAbilities();
        Color base = new Color(228, 255, 225);
        Color edge = new Color(60, 188, 145);
        Color icon = new Color(129, 149, 148);
        ThemeColor background = new ThemeColor(base, edge);
        menu = new BendingMenuInfo(new MenuTheme(background, new ThemeColor(edge, edge),
                new ThemeColor(icon, base), 0x57E8F2), this);
    }

    public static Vector getClosestWaterbendableBlock(EntityLivingBase entity, Ability ability, AbilityContext ctx) {
        World world = entity.world;

        Vector eye = Vector.getEyePos(entity);

        double range = ability.getProperty(SOURCE_RANGE, ctx).doubleValue();
        int angles = ability.getProperty(SOURCE_ANGLES, ctx).intValue();
        boolean plantbend = ability.getBooleanProperty(PLANT_BEND, ctx);

        range = ability.powerModify((float) range, ctx.getAbilityData());

        for (int i = 0; i < angles; i++) {
            for (int j = 0; j < angles; j++) {

                double yaw = entity.rotationYaw + i * 360.0 / angles;
                double pitch = entity.rotationPitch + j * 360.0 / angles;

                BiPredicate<BlockPos, IBlockState> isWater = (pos, state) ->
                        (STATS_CONFIG.waterBendableBlocks.contains(state.getBlock()) || STATS_CONFIG.plantBendableBlocks
                                .contains(state.getBlock()) && plantbend) && state.getBlock() != Blocks.AIR;

                Vector angle = Vector.toRectangular(toRadians(yaw), toRadians(pitch));
                Raytrace.Result result = Raytrace.predicateRaytrace(world, eye, angle, range, isWater);
                if (result.hitSomething()) {
                    return result.getPosPrecise();
                }

            }

        }
        return null;
    }

    /**
     * Assumes that the ability has PLANT_BEND as a property; should only be used for waterbending abilities.
     * @param ability
     * @param state
     * @param entity
     * @return
     */
    public static boolean isBendable(Ability ability, IBlockState state,
                                     EntityLivingBase entity) {
        boolean bendable = STATS_CONFIG.waterBendableBlocks.contains(state.getBlock());
        if (ability.getBooleanProperty(PLANT_BEND, AbilityData.get(entity, ability.getName())))
            bendable |= STATS_CONFIG.plantBendableBlocks.contains(state.getBlock());

        return bendable;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {

    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {

    }

    @Override
    public int getTextColour() {
        return 0x0066CC;
    }

    @Override
    public BendingMenuInfo getRadialMenu() {
        return menu;
    }

    @Override
    public String getName() {
        return "waterbending";
    }

    @Override
    public UUID getId() {
        return ID;
    }

    @Override
    public SoundEvent getRadialMenuSound() {
        return SoundEvents.ENTITY_GENERIC_SWIM;
    }

    @Override
    public TextFormatting getTextFormattingColour() {
        return TextFormatting.BLUE;
    }
}
