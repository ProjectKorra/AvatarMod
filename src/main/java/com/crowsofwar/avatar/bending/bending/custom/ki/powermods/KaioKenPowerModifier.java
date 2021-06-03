package com.crowsofwar.avatar.bending.bending.custom.ki.powermods;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BuffPowerModifier;
import com.crowsofwar.avatar.bending.bending.fire.AbilityImmolate;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.PowerRatingModifier;
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

public class KaioKenPowerModifier extends PowerRatingModifier {

    @Override
    public double get(BendingContext ctx) {

        BendingData data = ctx.getData();
        AbilityData abilityData = data.getAbilityData(new AbilityImmolate().getName());

        //Powerrating should be an integer but I'll leave it as a double toa count for user error
        return Objects.requireNonNull(Abilities.get("kaio_ken")).getProperty(Ability.POWERRATING, abilityData).doubleValue();

    }

    @Override
    public boolean onUpdate(BendingContext ctx) {
        return super.onUpdate(ctx);
    }

}

