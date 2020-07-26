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
package com.crowsofwar.avatar.bending.bending.air;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingAi;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.EntityAirGust;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.data.Behavior;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;

/**
 * @author CrowsOfWar
 */
public class AbilityAirblade extends Ability {

    public static final String
            NUMBER_OF_BLADES = "numberOfBlades",
            ANGLE = "angle",
            DESTROY_GRASS = "destroyGrass",
            BOOMERANG = "boomerAang",
            PIERCES = "pierces";

    public AbilityAirblade() {
        super(Airbending.ID, "airblade");
    }

    @Override
    public void init() {
        super.init();
        addProperties(NUMBER_OF_BLADES, ANGLE);
        addBooleanProperties(DESTROY_GRASS, BOOMERANG, PIERCES);
    }

    @Override
    public void execute(AbilityContext ctx) {

        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();

        if (!bender.consumeChi(getChiCost(ctx))) return;


        AbilityData abilityData = ctx.getData().getAbilityData(this);
        float sizeMult = getProperty(SIZE, ctx).floatValue();
        float damage = getProperty(DAMAGE, ctx).floatValue();
        float speed = getProperty(SPEED, ctx).floatValue() * 5;
        float knockback = getProperty(KNOCKBACK, ctx).floatValue();
        int lifetime = getProperty(LIFETIME, ctx).intValue();
        int performance = getProperty(PERFORMANCE, ctx).intValue();
        int blades = getProperty(NUMBER_OF_BLADES, ctx).intValue();
        //Degrees, not radians, for ease of player use.
        float angle = getProperty(ANGLE, ctx).floatValue();

        damage *= abilityData.getXpModifier() * ctx.getPowerRatingDamageMod();
        sizeMult *= abilityData.getXpModifier();
        //Ensures 0 powerrating doesn't make the size 0.
        sizeMult *= Math.max(0.25, bender.calcPowerRating(Airbending.ID) / 100 + 1);
        speed *= abilityData.getXpModifier() * ctx.getPowerRatingDamageMod();
        knockback *= abilityData.getXpModifier() * ctx.getPowerRatingDamageMod();
        lifetime *= abilityData.getXpModifier() * ctx.getPowerRatingDamageMod();

        Vector spawnAt = Vector.getEyePos(entity);

        for (int i = 0; i < getProperty(NUMBER_OF_BLADES, ctx).intValue(); i++) {
            @SuppressWarnings("IntegerDivisionInFloatingPointContext")
            float yaw = (entity.rotationYaw - (blades / 2) * angle) + (i * angle);
            Vector direction = Vector.toRectangular(Math.toRadians(yaw), Math.toRadians(entity.rotationPitch));
            EntityAirGust airblade = new EntityAirGust(world);
            airblade.setPosition(spawnAt.x(), spawnAt.y(), spawnAt.z());
            airblade.setAbility(Objects.requireNonNull(Abilities.get("airblade")));
            airblade.setVelocity(direction.times(speed));
            airblade.setDamage(damage);
            airblade.setDestroyGrass(getBooleanProperty(DESTROY_GRASS, ctx));
            airblade.setElement(new Airbending());
            airblade.setXp(getProperty(XP_HIT, ctx).floatValue());
            airblade.rotationPitch = entity.rotationPitch;
            airblade.rotationYaw = yaw;
            airblade.setPiercing(getBooleanProperty(PIERCES, ctx));
            airblade.setPush(knockback);
            airblade.setLifeTime(lifetime);
            airblade.setOwner(entity);
            airblade.setTier(getCurrentTier(ctx));
            airblade.setChiHit(getProperty(CHI_HIT, ctx).floatValue());
            airblade.setAbility(this);
            //All changed depending on boomeranging or not
            if (getBooleanProperty(BOOMERANG, ctx)) {
                airblade.setEntitySize(sizeMult, 0.25F * sizeMult);
                airblade.setExpandedWidth(sizeMult / 10);
                airblade.setExpandedHeight(sizeMult / 20);
            } else {
                airblade.setExpandedHeight(sizeMult / 10);
                airblade.setExpandedWidth(sizeMult / 20);
                airblade.setEntitySize(sizeMult, 0.25F * sizeMult);

            }
            airblade.setBehaviour(new AirBladeBehaviour());
            if (!world.isRemote)
                world.spawnEntity(airblade);
        }

        super.execute(ctx);

    }

    @Override
    public int getBaseTier() {
        return 2;
    }

    @Override
    public BendingAi getAi(EntityLiving entity, Bender bender) {
        return new AiAirblade(this, entity, bender);
    }

    @Override
    public boolean isProjectile() {
        return true;
    }

    @Override
    public boolean isOffensive() {
        return true;
    }

    public static class AirBladeBehaviour extends OffensiveBehaviour {

        @Override
        public Behavior<EntityOffensive> onUpdate(EntityOffensive entity) {
            if (entity instanceof EntityAirGust && entity.getOwner() != null) {
                World world = entity.world;
                AbilityData data = AbilityData.get(entity.getOwner(), entity.getAbility().getName());

                if (data != null && entity.getAbility().getBooleanProperty(BOOMERANG, data)) {
                    if (entity.ticksExisted > 8 && entity.ticksExisted < 25) {
                        entity.motionX *= 0.75;
                        entity.motionY *= 0.75;
                        entity.motionZ *= 0.75;
                    }
                    if (entity.ticksExisted > 25) {
                        entity.setVelocity(AvatarEntityUtils.getMiddleOfEntity(entity.getOwner()).subtract(AvatarEntityUtils.getMiddleOfEntity(entity)).scale(0.25));
                    }
                    if (world.isRemote) {
                        for (double i = 0; i < 20; i += 1 / entity.getWidth()) {
                            double spawnX = AvatarEntityUtils.getMiddleOfEntity(entity).x;
                            double spawnY = AvatarEntityUtils.getMiddleOfEntity(entity).y;
                            double spawnZ = AvatarEntityUtils.getMiddleOfEntity(entity).z;
                            ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
                                    world.rand.nextGaussian() / 60).collide(true).time(2 + AvatarUtils.getRandomNumberInRange(0, 1)).clr(1F, 1F, 1F, 0.075F)
                                    .scale(entity.getAvgSize() / 4).element(entity.getElement()).spawnEntity(entity).spawn(world);
                            ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(AvatarEntityUtils.getMiddleOfEntity(entity)).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
                                    world.rand.nextGaussian() / 60).collide(true).time(2 + AvatarUtils.getRandomNumberInRange(0, 2)).clr(0.8F, 0.8F, 0.8F, 0.075F)
                                    .scale(entity.getAvgSize() / 2).element(entity.getElement()).spin(entity.getWidth() * 2, 0.1).spawnEntity(entity).spawn(world);
                            ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(AvatarEntityUtils.getMiddleOfEntity(entity)).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
                                    world.rand.nextGaussian() / 60).collide(true).time(4 + AvatarUtils.getRandomNumberInRange(0, 2)).clr(1F, 1F, 1F, 0.1F)
                                    .scale(entity.getAvgSize()).element(entity.getElement()).spin(entity.getWidth() * 2, 0.1).spawnEntity(entity).spawn(world);
                        }

                    }
                } else {
                    if (world.isRemote) {
                        for (double i = 0; i < 0.75; i += 1 / entity.getHeight()) {
                            AxisAlignedBB boundingBox = entity.getEntityBoundingBox();
                            double spawnX = boundingBox.minX + world.rand.nextDouble() * (boundingBox.maxX - boundingBox.minX);
                            double spawnY = boundingBox.minY + world.rand.nextDouble() * (boundingBox.maxY - boundingBox.minY);
                            double spawnZ = boundingBox.minZ + world.rand.nextDouble() * (boundingBox.maxZ - boundingBox.minZ);
                            ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
                                    world.rand.nextGaussian() / 60).collide(true).time(6 + AvatarUtils.getRandomNumberInRange(0, 2)).clr(0.96F, 0.96F, 0.96F, 0.075F)
                                    .scale(entity.getAvgSize() / 5).element(entity.getElement()).spawn(world);
                        }

                        for (double i = -90; i <= 90; i += 5) {
                            Vec3d pos = AvatarEntityUtils.getMiddleOfEntity(entity);
                            Vec3d newDir = entity.getLookVec().scale(entity.getHeight() / 1.75 * Math.cos(Math.toRadians(i)));
                            pos = pos.add(newDir);
                            pos = new Vec3d(pos.x, pos.y + (entity.getHeight() / 1.75 * Math.sin(Math.toRadians(i))), pos.z);
                            ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(pos).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
                                    world.rand.nextGaussian() / 60).collide(true).time(1 + AvatarUtils.getRandomNumberInRange(0, 1)).clr(0.95F, 095F, 0.95F, 0.05F)
                                    .scale(entity.getWidth()).element(entity.getElement()).spawnEntity(entity).spawn(world);
                            ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(pos).vel(entity.motionX * 0.98, entity.motionY * 0.98, entity.motionZ * 0.98).collide(true)
                                    .time(8 + AvatarUtils.getRandomNumberInRange(0, 6)).clr(0.95F, 0.95F, 0.95F, 0.075F)
                                    .scale(entity.getWidth() * 2).spawnEntity(entity).element(entity.getElement()).spawn(world);
                        }
                    }
                    entity.motionX *= 0.975;
                    entity.motionY *= 0.975;
                    entity.motionZ *= 0.975;
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
