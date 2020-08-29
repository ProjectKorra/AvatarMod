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
import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.bending.bending.air.Airbending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.EntityShockwave;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * @author CrowsOfWar
 */
public class SmashGroundHandler extends TickHandler {
    public SmashGroundHandler(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {

        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();

        if (entity.isInWater() || entity.onGround || bender.isFlying()) {

            if (entity.onGround) {
                smashEntity(entity);
                world.playSound(null, entity.posX, entity.posY, entity.posZ, getSound(), getSoundCategory(), 4F, 0.5F);

            }

            return true;
        }

        return false;
    }

    protected void smashEntity(EntityLivingBase entity) {
        World world = entity.world;
        EntityShockwave shockwave = new EntityShockwave(world);
        shockwave.setDamage(getDamage());
        shockwave.setOwner(entity);
        shockwave.setPosition(entity.posX, entity.getEntityBoundingBox().minY + 0.4, entity.posZ);
        shockwave.setKnockbackHeight(getKnockbackHeight());
        shockwave.setSpeed((float) getSpeed() / 4);
        shockwave.setKnockbackMult(new Vec3d(2, 4, 2));
        shockwave.setRange((float) getRange());
        shockwave.setPush((float) (getSpeed() / 15));
        shockwave.setRenderNormal(false);
        shockwave.setElement(getElement());
        shockwave.setSphere(false);
        shockwave.setTier(getAbility().getCurrentTier(AbilityData.get(entity, getAbility().getName())));
        shockwave.setParticle(getParticle());
        shockwave.setParticleAmount(getParticleAmount());
        shockwave.setParticleSpeed((float) getParticleSpeed() / 4);
        shockwave.setFireTime(fireTime());
        shockwave.setDamageSource(getDamageSource().getDamageType() + "_shockwave");
        shockwave.setAbility(getAbility());
        shockwave.setParticleWaves(getParticleWaves());
        shockwave.setPerformanceAmount(getPerformanceAmount());
        shockwave.setRenderNormal(spawnNormalShockwave());
        shockwave.setBehaviour(getBehaviour());
        if (!world.isRemote)
            world.spawnEntity(shockwave);
    }

    protected int fireTime() {
        return 0;
    }

    protected double getRange() {
        return 8;
    }

    protected EnumParticleTypes getParticle() {
        return EnumParticleTypes.EXPLOSION_NORMAL;
    }

    protected int getParticleWaves() {
        return (int) (getSpeed() * 3);
    }

    protected int getParticleAmount() {
        return 1;
    }

    protected Ability getAbility() {
        return Abilities.get("air_jump");
    }

    protected double getParticleSpeed() {
        return 0.1F;
    }

    protected double getSpeed() {
        return 6;
    }

    protected float getKnockbackHeight() {
        return 0.1F;
    }

    protected SoundEvent getSound() {
        return SoundEvents.BLOCK_FIRE_EXTINGUISH;
    }

    protected SoundCategory getSoundCategory() {
        return SoundCategory.BLOCKS;
    }

    protected int getPerformanceAmount() {
        return 10;
    }

    protected float getDamage() {
        return 6F;
    }

    protected DamageSource getDamageSource() {
        return AvatarDamageSource.AIR;
    }

    protected BendingStyle getElement() {
        return new Airbending();
    }

    protected boolean spawnNormalShockwave() {
        return false;
    }

    protected OffensiveBehaviour getBehaviour() {
        return new AirGroundPoundShockwave();
    }

    public static class AirGroundPoundShockwave extends OffensiveBehaviour {

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            if (entity.world.isRemote) {
                if (entity instanceof EntityShockwave) {
                    if (entity.ticksExisted <= ((EntityShockwave) entity).getParticleWaves()) {
                        for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / (((EntityShockwave) entity).getRange() * ((EntityShockwave) entity).getParticleAmount() * entity.ticksExisted * 0.25)) {
                            //Even though the maths is technically wrong, you use sin if you want a shockwave, and cos if you want a sphere (for x).
                            double x2 = entity.posX + (entity.ticksExisted * ((EntityShockwave) entity).getSpeed()) * Math.sin(angle);
                            double y2 = entity.getEntityBoundingBox().minY + 0.3;
                            double z2 = entity.posZ + (entity.ticksExisted * ((EntityShockwave) entity).getSpeed()) * Math.cos(angle);
                            Vector speed = new Vector((entity.ticksExisted * ((EntityShockwave) entity).getSpeed()) * Math.sin(angle) * (entity.getParticleSpeed()),
                                    entity.getParticleSpeed() / 2 * entity.world.rand.nextGaussian(), (entity.ticksExisted * ((EntityShockwave) entity).getSpeed()) * Math.cos(angle) * (entity.getParticleSpeed()));
                            ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(0.95F, 0.95F, 0.95F, 0.15F).pos(x2, y2, z2).vel(speed.toMinecraft())
                                    .collide(true).collideParticles(true).scale((float) ((EntityShockwave) entity).getSpeed() * 4F).time(8 + AvatarUtils.getRandomNumberInRange(0, 2)).spawn(entity.world);
                        }
                    }

                }
            }
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

