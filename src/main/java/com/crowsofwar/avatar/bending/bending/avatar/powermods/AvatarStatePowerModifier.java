package com.crowsofwar.avatar.bending.bending.avatar.powermods;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.avatar.AbilityAvatarState;
import com.crowsofwar.avatar.bending.bending.avatar.Avatarbending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.PowerRatingModifier;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import java.util.Objects;

public class AvatarStatePowerModifier extends PowerRatingModifier {


    @Override
    public double get(BendingContext ctx) {

        BendingData data = ctx.getData();
        AbilityData abilityData = data.getAbilityData(Objects.requireNonNull(Abilities.get("avatar_state")));
        AbilityAvatarState ability = (AbilityAvatarState) Abilities.get("avatar_state");

        double modifier = 0;

        if (abilityData != null && ability != null) {
            modifier = ability.getProperty(Ability.POWERRATING, abilityData).doubleValue();
        }

        return modifier;

    }


    @Override
    public boolean onUpdate(BendingContext ctx) {

        AbilityData data = ctx.getData().getAbilityData("avatar_state");
        EntityLivingBase entity = ctx.getBenderEntity();
        AbilityAvatarState avatarState = (AbilityAvatarState) Abilities.get("avatar_state");

        if (avatarState != null && data != null) {
            float size = (1F + Math.min(data.getLevel(), 0) / 2F) * 2;
            World world = entity.world;
            if (world.isRemote) {
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
                                world.rand.nextGaussian() / 60).time(12).clr(AvatarUtils.getRandomNumberInRange(0, 255) / 255F,
                                AvatarUtils.getRandomNumberInRange(0, 255) / 255F, AvatarUtils.getRandomNumberInRange(0, 255) / 255F, 0.005F + size / 200)
                        .scale(size).element(BendingStyles.get(Avatarbending.ID)).glow(true)
                        .swirl((int) (size), (int) (size * Math.PI), size, 1F, (int) size * 40,
                                1F, entity, world, false, AvatarEntityUtils.getMiddleOfEntity(entity),
                                ParticleBuilder.SwirlMotionType.OUT, false, true);
            }
        }
        return super.onUpdate(ctx);
    }

    @Override
    public void onRemoval(BendingContext ctx) {
        super.onRemoval(ctx);
    }
}
