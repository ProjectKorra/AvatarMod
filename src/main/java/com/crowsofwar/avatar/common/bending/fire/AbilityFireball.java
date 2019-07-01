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
package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityFireball;
import com.crowsofwar.avatar.common.entity.EntityLightOrb;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.FireballBehavior;
import com.crowsofwar.avatar.common.entity.data.LightOrbBehavior;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigClient.CLIENT_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.gorecore.util.Vector.getEyePos;
import static com.crowsofwar.gorecore.util.Vector.getLookRectangular;

import java.util.Random;

/**
 * @author CrowsOfWar
 */
public class AbilityFireball extends Ability {

	public AbilityFireball() {
		super(Firebending.ID, "fireball");
		requireRaytrace(2.5, false);
	}

	@Override
	public void execute(AbilityContext ctx) {

		EntityLivingBase entity = ctx.getBenderEntity();
		Bender bender = ctx.getBender();
		World world = ctx.getWorld();
		BendingData data = ctx.getData();


		if (bender.consumeChi(STATS_CONFIG.chiFireball) && !data.hasStatusControl(StatusControl.THROW_FIREBALL)) {

			Vector target;
			if (ctx.isLookingAtBlock()) {
				target = ctx.getLookPos();
			} else {
				Vector playerPos = getEyePos(entity);
				target = playerPos.plus(getLookRectangular(entity).times(2.5));
			}

			float damage = STATS_CONFIG.fireballSettings.damage;
			float explosionStrength = 0.75f;
			int size = 16;
			damage *= ctx.getLevel() >= 2 ? 1.75f : 1f;
			damage *= ctx.getPowerRatingDamageMod();

			if (ctx.getLevel() == 1) {
				explosionStrength = 1;
				size = 18;
			}

			if (ctx.getLevel() == 2) {
				explosionStrength = 1.25F;
				size = 20;
			}

			if (ctx.isMasterLevel(AbilityTreePath.FIRST)) {
				explosionStrength = 2;
				size = 30;
			}



			assert target != null;

			EntityFireball fireball = new EntityFireball(world);
			fireball.setPosition(target);
			fireball.setOwner(entity);
			fireball.setBehavior(new FireballBehavior.PlayerControlled());
			fireball.setDamage(damage);
			fireball.setPowerRating(bender.calcPowerRating(Firebending.ID));
			fireball.setSize(size);
			fireball.setAbility(this);
			if (ctx.isMasterLevel(AbilityTreePath.SECOND)) fireball.setSize(20);


			EntityLightOrb orb = new EntityLightOrb(world);
			orb.setOwner(entity);
			orb.setAbility(this);
			orb.setPosition(target);
			orb.setOrbSize(size / 10);
			orb.setColor(1F, 0.5F, 0F, 1F);
			orb.setLightRadius(15);
			orb.setEmittingEntity(fireball.getUniqueID().toString());
			orb.setBehavior(new FireballLightOrbBehavior());
			orb.setType(CLIENT_CONFIG.fireballRenderSettings.isSphere ? EntityLightOrb.EnumType.TEXTURE_SPHERE : EntityLightOrb.EnumType.TEXTURE_CUBE);
			orb.setTexture("avatarmod:textures/entity/fireball/frame_%number%.png");
			orb.setTextureFrameCount(30);
			world.spawnEntity(orb);

			if (ctx.isMasterLevel(AbilityTreePath.SECOND)) {
				explosionStrength = fireball.getSize() / 20F + 0.75F;
			}

			fireball.setExplosionStrength(explosionStrength);
			data.addStatusControl(StatusControl.THROW_FIREBALL);
			world.spawnEntity(fireball);

		}

	}

	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiFireball(this, entity, bender);
	}

	public static class FireballLightOrbBehavior extends LightOrbBehavior.FollowEntity {

		@Override
		public Behavior onUpdate(EntityLightOrb entity) {
			super.onUpdate(entity);
			entity.rotationPitch += 15;
			entity.rotationYaw += 15;
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
