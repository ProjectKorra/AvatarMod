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
package com.crowsofwar.avatar.common.bending.air.tickhandlers;

import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
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

		Vector pos = Vector.getEntityPos(entity).minusY(0.05);

		if (world.isRemote)
			for (int i = 0; i < 2 + AvatarUtils.getRandomNumberInRange(0, 4); i++) {
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).collide(true).clr(1, 1, 1, 0.15F).pos(pos.toMinecraft()).vel(world.rand.nextGaussian() / 40, world.rand.nextGaussian() / 40, world.rand.nextGaussian() / 40).scale(1F +
						Math.max(data.getLevel(), 0) / 2F).time(6 + AvatarUtils.getRandomNumberInRange(0, 6)).element(new Airbending()).collide(true).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).collide(true).clr(1, 1, 1, 0.15F).pos(pos.toMinecraft()).vel(world.rand.nextGaussian() / 40, world.rand.nextGaussian() / 40, world.rand.nextGaussian() / 40)
						.scale(1F + Math.max(data.getLevel(), 0) / 2F).time(6 + AvatarUtils.getRandomNumberInRange(0, 6)).element(new Airbending()).collide(true).spawn(world);
			}
		//particles.spawnParticles(entity.world, EnumParticleTypes.EXPLOSION_NORMAL, 1, 4, pos,
		//		new Vector(0.7, 0.2, 0.7), true);

		return entity.isInWater() || entity.onGround || bender.isFlying();

	}

}
