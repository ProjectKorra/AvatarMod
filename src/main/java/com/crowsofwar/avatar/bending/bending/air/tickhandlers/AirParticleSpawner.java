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
package com.crowsofwar.avatar.bending.bending.air.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.air.AbilityAirJump;
import com.crowsofwar.avatar.bending.bending.air.Airbending;
import com.crowsofwar.avatar.client.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.client.particle.ParticleSpawner;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

/**
 * @author CrowsOfWar
 */
public class AirParticleSpawner extends TickHandler {
    private static final ParticleSpawner particles = new NetworkParticleSpawner();

    public AirParticleSpawner(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();
        AbilityData data = AbilityData.get(entity, "air_jump");
        AbilityAirJump jump = (AbilityAirJump) Abilities.get("air_jump");

        if (jump != null && data != null) {
            Vector pos = Vector.getEntityPos(entity).minusY(0.05);
            float size = jump.getProperty(Ability.EFFECT_RADIUS, data).floatValue() * 1.5F;
            size *= data.getDamageMult() * data.getXpModifier();

            if (world.isRemote)
                for (int i = 0; i < 4 + AvatarUtils.getRandomNumberInRange(0, 2); i++) {
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(0.95F, 0.95F, 0.95F, 0.075F).pos(pos.toMinecraft()).vel(world.rand.nextGaussian() / 40, world.rand.nextGaussian() / 40, world.rand.nextGaussian() / 40)
                            .scale(size).time(8 + AvatarUtils.getRandomNumberInRange(0, 6))
                            .element(new Airbending()).collide(true).spawn(world);
                }

            data.setRegenBurnout(false);
        }
        return entity.isInWater() || entity.onGround || bender.isFlying();

    }

}
