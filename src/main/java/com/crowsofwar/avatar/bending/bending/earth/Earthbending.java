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

package com.crowsofwar.avatar.bending.bending.earth;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.client.gui.BendingMenuInfo;
import com.crowsofwar.avatar.client.gui.MenuTheme;
import com.crowsofwar.avatar.client.gui.MenuTheme.ThemeColor;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.awt.*;
import java.util.UUID;
import java.util.function.BiPredicate;

import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;
import static java.lang.Math.toRadians;

public class Earthbending extends BendingStyle {

    public static final UUID ID = UUID.fromString("82ad13b5-4bbe-4eaf-8aa0-00b36b33aed0");

    private final BendingMenuInfo menu;

    public Earthbending() {
        registerAbilities();
        Color light = new Color(225, 225, 225);
        Color brown = new Color(79, 57, 45);
        Color gray = new Color(90, 90, 90);
        Color lightBrown = new Color(255, 235, 224);
        ThemeColor background = new ThemeColor(lightBrown, brown);
        ThemeColor edge = new ThemeColor(brown, brown);
        ThemeColor icon = new ThemeColor(gray, light);
        menu = new BendingMenuInfo(new MenuTheme(background, edge, icon, 0xB09B7F), this);

    }

    public static boolean isBendable(IBlockState state) {
        Block block = state.getBlock();
        if (STATS_CONFIG.bendableBlocks.contains(block))
            return true;
        else return STATS_CONFIG.enableAutoModCompat && block.getRegistryName() != null && !block.getRegistryName().getNamespace().equals("minecraft") &&
                (OreDictionary.doesOreNameExist(block.getTranslationKey()) || block instanceof BlockOre ||
                        OreDictionary.doesOreNameExist(block.getLocalizedName()));
    }

    public static Vector getClosestEarthbendableBlock(EntityLivingBase entity, AbilityContext ctx, Ability ability) {
        World world = entity.world;
        Vector eye = Vector.getEyePos(entity);

        float range = ability.getProperty(Ability.RADIUS, ctx).floatValue();
        range *= ctx.getAbilityData().getDamageMult() * ctx.getAbilityData().getXpModifier();

        int angle = 12;
        for (int i = 0; i < angle; i++) {
            for (int j = 0; j < angle; j++) {

                double yaw = entity.rotationYaw + i * 360.0 / angle;
                double pitch = entity.rotationPitch + j * 360.0 / angle;

                BiPredicate<BlockPos, IBlockState> isWater = (pos, state) -> isBendable(state)
                        && state.getBlock() != Blocks.AIR;

                Vector angleVec = Vector.toRectangular(toRadians(yaw), toRadians(pitch));
                Raytrace.Result result = Raytrace.predicateRaytrace(world, eye, angleVec, range, isWater);
                if (result.hitSomething()) {
                    return result.getPosPrecise();
                }

            }

        }

        ctx.getBender().sendMessage("avatar.earthSourceFail");
        return null;

    }

    @Override
    public int getTextColour() {
        return 0x663300;
    }

    @Override
    public BendingMenuInfo getRadialMenu() {
        return menu;
    }

    @Override
    public String getName() {
        return "earthbending";
    }

    @Override
    public UUID getId() {
        return ID;
    }

    @Override
    public TextFormatting getTextFormattingColour() {
        return TextFormatting.DARK_GREEN;
    }

    @Override
    public SoundEvent getRadialMenuSound() {
        return SoundEvents.BLOCK_GRASS_BREAK;
    }
}
