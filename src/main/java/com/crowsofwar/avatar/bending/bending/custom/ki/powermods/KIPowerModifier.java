package com.crowsofwar.avatar.bending.bending.custom.ki.powermods;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BuffPowerModifier;
import com.crowsofwar.avatar.bending.bending.fire.AbilityImmolate;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.Vision;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;
import java.util.Objects;

import static com.crowsofwar.avatar.bending.bending.fire.AbilityImmolate.FIRE_CHANCE;
import static com.crowsofwar.avatar.bending.bending.fire.AbilityImmolate.INCINERATE_PROJECTILES;

public class KIPowerModifier extends BuffPowerModifier {

    @Override
    public double get(BendingContext ctx) {

        BendingData data = ctx.getData();
        AbilityData abilityData = data.getAbilityData(new AbilityImmolate().getName());

        //Powerrating should be an integer but I'll leave it as a double toa count for user error
        return Objects.requireNonNull(Abilities.get("immolate")).getProperty(Ability.POWERRATING, abilityData).doubleValue();

    }

    @Override
    public boolean onUpdate(BendingContext ctx) {

        EntityLivingBase entity = ctx.getBenderEntity();
        AbilityData abilityData = AbilityData.get(entity, "immolate");

        // Intermittently light on fire
        if (entity.ticksExisted % 15 == 0) {

            double chance = Objects.requireNonNull(Abilities.get("immolate")).getProperty(FIRE_CHANCE, abilityData).floatValue() / 10;
            if (Math.random() < chance) {
                entity.setFire(2);
            }

        }

        if (Objects.requireNonNull(Abilities.get("immolate")).getBooleanProperty(INCINERATE_PROJECTILES, abilityData)) {
            AxisAlignedBB box = new AxisAlignedBB(entity.posX - 2, entity.posY, entity.posZ - 2, entity.posX + 2, entity.posY + 3, entity.posZ + 2);
            List<Entity> targets = entity.world.getEntitiesWithinAABB(Entity.class, box);
            if (!entity.world.isRemote) {
                if (!targets.isEmpty()) {
                    for (Entity e : targets) {
                        if ((e instanceof AvatarEntity && ((AvatarEntity) e).canCollideWith(entity)) || e != entity) {
                            e.setFire(5);
                            if (e instanceof EntityThrowable || e instanceof EntityItem) {
                                e.setFire(1);
                                e.setDead();
                            }
                            if (e instanceof EntityArrow) {
                                e.setFire(1);
                                e.setDead();
                            }
                        }
                    }
                }
            }
        }

        return super.onUpdate(ctx);
    }

    @Override
    protected Vision[] getVisions() {
        return new Vision[]{Vision.IMMOLATE_WEAK, Vision.IMMOLATE_MEDIUM, Vision.IMMOLATE_POWERFUL};
    }

    @Override
    protected String getAbilityName() {
        return new AbilityImmolate().getName();
    }

}

