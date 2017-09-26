package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.gorecore.util.Vector.getEntityPos;

/*public class EntityWaterCannon extends AvatarEntity {
    /**
     * @param world
     */
    /*public EntityWaterCannon(World world) {
            super(world);
            setSize(0.5f, 0.5f);
            damage = 8;
        }
    @Override
    protected void collideWithNearbyEntities() {

        List<Entity> collisions = Raytrace.entityRaytrace(world, position(), velocity(), velocity
                ().magnitude() / 20, entity -> entity != getOwner() && entity != this);

        for (Entity collided : collisions) {
            onCollideWithEntity(collided);
        }

    }
    private void damageEntity(EntityLivingBase entity, float damageModifier) {

        if (world.isRemote) {
            return;
        }

        // Handle lightning redirection

        DamageSource damageSource = createDamageSource(entity);

            Vector velocity = getEntityPos(entity).minus(this.position()).normalize();
            velocity = velocity.times(2);
            entity.addVelocity(velocity.x(), 0.4, velocity.z());
            AvatarUtils.afterVelocityAdded(entity);

            // Add Experience
            // Although 2 lightning entities are fired in each lightning ability, this won't
            // cause 2x XP rewards as this only happens when the entity is successfully attacked
            // (hurtResistantTime prevents the 2 lightning entities from both damaging at once)
            if (getOwner() != null) {
                BendingData data = BendingData.get(getOwner());
                AbilityData abilityData = data.getAbilityData("water_cannon");
                abilityData.addXp(SKILLS_CONFIG.struckWithLightning);
            }
        }

    }
}

**/