package com.crowsofwar.avatar.bending.bending.water.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.water.AbilityWaterBlast;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityLightCylinder;
import com.crowsofwar.avatar.entity.EntityWaterCannon;
import com.crowsofwar.avatar.entity.data.Behavior;
import com.crowsofwar.avatar.entity.data.LightCylinderBehaviour;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.data.*;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

import static com.crowsofwar.avatar.bending.bending.water.AbilityWaterBlast.BURST;
import static com.crowsofwar.avatar.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.util.data.StatusControlController.BURST_WATER;

public class WaterChargeHandler extends TickHandler {
    public static final UUID WATER_CHARGE_MOVEMENT_ID = UUID.fromString("87a0458a-38ea-4d7a-be3b-0fee10217aa6");

    public WaterChargeHandler(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {

        AbilityData abilityData = ctx.getData().getAbilityData("water_blast");
        World world = ctx.getWorld();
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();
        Bender bender = ctx.getBender();
        AbilityWaterBlast blast = (AbilityWaterBlast) Abilities.get("water_blast");

        if (blast != null) {
            //TODO: Adjust this for new property system
            float powerMod = (float) abilityData.getDamageMult();
            float xpMod = abilityData.getXpModifier();
            float speed = blast.getProperty(Ability.SPEED, abilityData).floatValue() * 2;
            int maxDuration = blast.getProperty(Ability.CHARGE_TIME, abilityData).intValue();
            int duration = data.getTickHandlerDuration(this);
            double radius = ((float) maxDuration - duration) / 10F;
            float damage = blast.getProperty(Ability.DAMAGE, abilityData).floatValue();
            float movementMultiplier = 0.6f - 0.7f * MathHelper.sqrt(duration / 40f);
            float size = blast.getProperty(Ability.SIZE, abilityData).floatValue();
            float lifetime = blast.getProperty(Ability.LIFETIME, abilityData).floatValue();
            int charge;


            maxDuration *= (2 - powerMod);
            maxDuration -= xpMod * 10;
            damage = blast.powerModify(damage, abilityData);
            lifetime = blast.powerModify(lifetime, abilityData);
            speed = blast.powerModify(speed, abilityData);
            size = blast.powerModify(size, abilityData);

            /* Makes sure the charge is never 0. */
            charge = Math.max((3 * (duration / maxDuration)) + 1, 1);
            charge = Math.min(charge, 4);
            //We don't want the charge going over 4.

            //Things are maxed at level 3
            damage *= (0.25 + 0.25 * charge);
            speed *= (0.50 + 0.16667 * charge);
            size *= (0.50 + 0.16667 * charge);
            lifetime *= (0.70 + 0.10 * charge);

            if (blast.getBooleanProperty(BURST, abilityData))
                data.addStatusControl(BURST_WATER);


            applyMovementModifier(entity, MathHelper.clamp(movementMultiplier, 0.1f, 1));
            //2nd tick handler check ensures it doesn't execute in parallel
            if (!data.hasStatusControl(StatusControlController.RELEASE_WATER) && !data.hasTickHandler(TickHandlerController.WATER_BURST)) {
                fireCannon(world, entity, damage, speed, size, lifetime, blast);
                world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.PLAYERS, 1, 2);
                entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(WATER_CHARGE_MOVEMENT_ID);
                ctx.getData().removeStatusControl(BURST_WATER);
                return true;
            }
            return false;
        }
        ctx.getData().removeStatusControl(BURST_WATER);
        return true;

    }

    private void fireCannon(World world, EntityLivingBase entity, float damage, double speed, float size, float ticks,
                            AbilityWaterBlast blast) {

        EntityWaterCannon cannon = new EntityWaterCannon(world);

        cannon.setOwner(entity);
        cannon.setDamage(damage);
        cannon.setEntitySize(size);
        cannon.setPosition(Vector.getEyePos(entity).minusY(0.8));
        cannon.setLifeTime((int) ticks);
        cannon.setXp(SKILLS_CONFIG.waterHit / 2);
        cannon.rotationPitch = entity.rotationPitch;
        cannon.rotationYaw = entity.rotationYaw;
        cannon.setTier(blast.getCurrentTier(AbilityData.get(entity, "water_blast")));
        cannon.setAbility(blast);

        Vector velocity = Vector.getLookRectangular(entity);
        velocity = velocity.normalize().times(speed);
        cannon.setSpeed((float) speed);
        cannon.setVelocity(velocity);
        if (!world.isRemote)
            world.spawnEntity(cannon);

    }

    private void applyMovementModifier(EntityLivingBase entity, float multiplier) {

        IAttributeInstance moveSpeed = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

        moveSpeed.removeModifier(WATER_CHARGE_MOVEMENT_ID);

        moveSpeed.applyModifier(new AttributeModifier(WATER_CHARGE_MOVEMENT_ID, "Water charge modifier", multiplier - 1, 1));

    }

    public static class WaterCylinderBehaviour extends LightCylinderBehaviour {

        @Override
        public Behavior onUpdate(EntityLightCylinder entity) {
            if (entity.getOwner() != null) {
                EntityWaterCannon cannon = AvatarEntity.lookupControlledEntity(entity.world, EntityWaterCannon.class, entity.getOwner());
                if (cannon != null) {
                    entity.setCylinderLength(cannon.getDistance(entity.getOwner()));
                    Vec3d height = entity.getOwner().getPositionVector().add(0, entity.getOwner().getEyeHeight() - 0.15, 0);
                    Vec3d dist = cannon.getPositionVector().subtract(height).normalize();
                    entity.setPosition(height.add(dist.scale(0.075)));
                    AvatarEntityUtils.setRotationFromPosition(entity, cannon);
                } else {
                    if (entity.ticksExisted > 1)
                        entity.setDead();
                }
            } else entity.setDead();
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


