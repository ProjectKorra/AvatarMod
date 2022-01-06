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
import com.crowsofwar.avatar.bending.bending.BendingAi;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityWaterArc;
import com.crowsofwar.avatar.entity.data.WaterArcBehavior;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.BiPredicate;

import static com.crowsofwar.avatar.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.util.data.StatusControlController.THROW_WATER;
import static com.crowsofwar.gorecore.util.Vector.getLookRectangular;
import static java.lang.Math.toRadians;

/**
 * @author CrowsOfWar
 *
 * Creates a water arc. Wow. Amazing.
 * Press shift to 'bind' it to you, allowing you to use it as a whip.
 * Middle click to change from flow control to water arc.w
 */
public class AbilityWaterArc extends Ability {

    public AbilityWaterArc() {
        super(Waterbending.ID, "water_arc");
        requireRaytrace(-1, true);
    }

    @Override
    public void init() {
        super.init();
       addProperties(WATER_LEVEL, EXPLOSION_SIZE, EXPLOSION_DAMAGE, EFFECT_RADIUS, MAX_HEALTH, CHARGE_FREQUENCY,
                CHARGE_AMOUNT, EFFECT_RADIUS, RANGE);
    }

    @Override
    public void execute(AbilityContext ctx) {
        World world = ctx.getWorld();
        Bender bender = ctx.getBender();
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();

    }


    /**
     * Kills already existing water arc if there is one
     */
    private void removeExisting(AbilityContext ctx) {

        EntityWaterArc water = AvatarEntity.lookupControlledEntity(ctx.getWorld(), EntityWaterArc
                .class, ctx.getBenderEntity());

        if (water != null) {
            water.setBehavior(new WaterArcBehavior.Thrown());
        }

    }

    @Override
    public BendingAi getAi(EntityLiving entity, Bender bender) {
        return new AiWaterArc(this, entity, bender);
    }

    @Override
    public boolean isProjectile() {
        return true;
    }

    @Override
    public boolean isOffensive() {
        return true;
    }
}
