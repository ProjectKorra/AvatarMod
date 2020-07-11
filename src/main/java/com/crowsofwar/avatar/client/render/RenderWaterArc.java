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

package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.entity.ControlPoint;
import com.crowsofwar.avatar.entity.EntityArc;
import com.crowsofwar.avatar.entity.EntityWaterArc;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderWaterArc extends RenderArc {

    private static final ResourceLocation water = new ResourceLocation("minecraft",
            "textures/blocks/water_overlay.png");

    /**
     * @param renderManager
     */
    public RenderWaterArc(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    protected ResourceLocation getTexture() {
        return water;
    }

    @Override
    protected void onDrawSegment(EntityArc arc, ControlPoint first, ControlPoint second) {
        // Parametric equation

        Vector from = new Vector(0, 0, 0);
        Vector to = second.position().minus(first.position());
        Vector diff = to.minus(from);
        Vector offset = first.position();
        Vector direction = diff.normalize();
        Vector spawnAt = offset.plus(direction.times(Math.random()));
        Vector velocity = first.velocity();
        //	arc.world.spawnParticle(EnumParticleTypes.WATER_SPLASH, spawnAt.x(), spawnAt.y(), spawnAt.z(),
        //			velocity.x(), velocity.y(), velocity.z());
    }

    @Override
    public void doRender(Entity entity, double xx, double yy, double zz, float p_76986_8_,
                         float partialTicks) {

        EntityWaterArc arc = (EntityWaterArc) entity;
        renderArc(arc, partialTicks, 0.1f, 1.5F * arc.getAvgSize());
    }


}
