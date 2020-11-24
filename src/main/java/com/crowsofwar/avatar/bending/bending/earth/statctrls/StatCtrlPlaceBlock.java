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

package com.crowsofwar.avatar.bending.bending.earth.statctrls;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.earth.AbilityEarthControl;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.entity.data.FloatingBlockBehavior;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

import static com.crowsofwar.avatar.client.controls.AvatarControl.CONTROL_RIGHT_CLICK_DOWN;
import static com.crowsofwar.avatar.util.data.StatusControl.CrosshairPosition.RIGHT_OF_CROSSHAIR;
import static com.crowsofwar.avatar.util.data.StatusControlController.THROW_BLOCK;

/**
 * @author CrowsOfWar
 */
public class StatCtrlPlaceBlock extends StatusControl {

    public StatCtrlPlaceBlock() {
        super(1, CONTROL_RIGHT_CLICK_DOWN, RIGHT_OF_CROSSHAIR);

        requireRaytrace(-1, true);

    }

    @Override
    public boolean execute(BendingContext ctx) {

        World world = ctx.getWorld();
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();
        Bender bender = ctx.getBender();
        AbilityData abilityData = AbilityData.get(entity, "earth_control");
        AbilityEarthControl control = (AbilityEarthControl) Abilities.get("earth_control");

        EntityFloatingBlock floating = AvatarEntity.lookupEntity(ctx.getWorld(), EntityFloatingBlock.class,
                fb -> fb.getBehavior() instanceof FloatingBlockBehavior.PlayerControlled
                        && fb.getOwner() == ctx.getBenderEntity());
        List<EntityFloatingBlock> blocks = world.getEntitiesWithinAABB(EntityFloatingBlock.class,
                entity.getEntityBoundingBox().grow(3.5, 3, 3.5));


        if (abilityData != null && control != null) {
            if (abilityData.getAbilityCooldown(entity) <= 0) {
				
                float chiCost = control.getChiCost(abilityData);
                float exhaustion = control.getExhaustion(abilityData);
                float burnout = control.getBurnOut(abilityData);
                int cooldown = control.getCooldown(abilityData);

                chiCost *= abilityData.getDamageMult() * abilityData.getXpModifier();
                exhaustion *= abilityData.getDamageMult() * abilityData.getXpModifier();
                burnout *= abilityData.getDamageMult() * abilityData.getXpModifier();
                cooldown *= abilityData.getDamageMult() * abilityData.getXpModifier();

                if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative())
                    chiCost = exhaustion = burnout = cooldown = 0;

                if (floating != null && !world.isRemote) {
                    Vec3d start = entity.getPositionEyes(1.0F);
                    Vec3d end = start.add(entity.getLookVec().scale(5));

                    RayTraceResult result = entity.rayTrace(5, 1.0F);
                    if (result != null) {
                        BlockPos pos = result.getBlockPos();
                        IBlockState state = world.getBlockState(pos);
                        IBlockState upState = world.getBlockState(pos.up());
                        Block block = state.getBlock();
                        Block upBlock = upState.getBlock();

                        if (!(upBlock instanceof BlockBush || upBlock instanceof BlockSnow || block instanceof BlockAir))
                            pos = pos.up();

                        if (block instanceof BlockSnow || block instanceof BlockBush)
                            pos = pos.down();

                        Vector force = new Vector(pos).minus(floating.velocity()).normalize();

                        if (bender.consumeChi(chiCost)) {
                            abilityData.addXp(control.getProperty(Ability.XP_USE).floatValue());
                            abilityData.addBurnout(burnout);
                            abilityData.setAbilityCooldown(cooldown);
                            if (entity instanceof EntityPlayer)
                                ((EntityPlayer) entity).addExhaustion(exhaustion);

                            blocks = blocks.stream().filter(floatingBlock -> floating.getBehavior() instanceof FloatingBlockBehavior.PlayerControlled).collect(Collectors.toList());
                            if (blocks.isEmpty())
                                abilityData.setRegenBurnout(true);

                            floating.setBehavior(new FloatingBlockBehavior.Place(pos));
                            floating.addVelocity(force);

                            SoundType sound = floating.getBlock().getSoundType();
                            floating.world.playSound(null, floating.getPosition(), sound.getPlaceSound(),
                                    SoundCategory.PLAYERS, sound.getVolume(), sound.getPitch());

                            data.removeStatusControl(THROW_BLOCK);
                        }

                        return true;
                    }
                    return false;
                }
            }
        }

        return true;

    }

}
