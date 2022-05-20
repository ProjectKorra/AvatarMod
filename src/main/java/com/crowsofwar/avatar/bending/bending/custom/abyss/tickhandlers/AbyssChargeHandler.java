package com.crowsofwar.avatar.bending.bending.custom.abyss.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.custom.abyss.AbilityAbyssalEnd;
import com.crowsofwar.avatar.bending.bending.custom.dark.Darkbending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.*;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

import static com.crowsofwar.avatar.bending.bending.Ability.*;

//Swirls and sends something up into the air; uses behaviour
public class AbyssChargeHandler extends TickHandler {
    public static final UUID ABYSS_END_MOVE_MOD_ID = UUID.fromString("d8a0d7b1-fd78-4373-8ddc-7c8f61edd000");

    public AbyssChargeHandler(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {
        BendingData data = ctx.getData();
        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();
        AbilityData abilityData = data.getAbilityData("abyss_end");
        AbilityAbyssalEnd advent = (AbilityAbyssalEnd) Abilities.get("abyss_end");
        int chargeDuration = data.getTickHandlerDuration(this);
        if (advent == null)
            return false;

        int maxCharge = advent.getProperty(CHARGE_TIME, abilityData).intValue();
        float charge;
        float requiredChi = advent.getProperty(CHI_COST, abilityData).floatValue() / 20F;
        double powerFactor = 2 - abilityData.getDamageMult();
        //Inverts what happens as you want chi to decrease when you're more powerful
        requiredChi *= powerFactor;


        float movementModifier = 1F - Math.min(requiredChi * 12.5F, 0.7F);
        if (entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(ABYSS_END_MOVE_MOD_ID) == null)
            applyMovementModifier(entity, movementModifier);

        //Makes sure the charge is never 0.
        charge = Math.max((3 * (chargeDuration / (float) maxCharge)) + 1, 1);
        charge = Math.min(charge, 4);
        //We don't want the charge going over 4.

        float radius = advent.getProperty(RADIUS, abilityData).floatValue() / 6;
        radius *= (0.60 + 0.10 * charge);

        Vec3d pos = entity.getPositionVector().add(0, entity.getEyeHeight() / 2, 0).add(entity.getLookVec().scale(0.5));
        if (world.isRemote) {
            //Swirl:
            ParticleBuilder.create(ParticleBuilder.Type.FLASH).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 95).element(BendingStyles.get(Darkbending.ID))
                    .clr(getClrRand(), getClrRand(), getClrRand(), getClrRand() * 4).scale(0.5F).time(32).spawnEntity(entity).swirl((int) (radius * 2), (int) (radius),
                            radius, (float) (0.25 / radius),
                            60, 1 / radius, entity, world, false, pos, ParticleBuilder.SwirlMotionType.IN, false,
                            true);
            ParticleBuilder.create(ParticleBuilder.Type.FLASH).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 88)
                    .clr(getClrRand(), getClrRand(), getClrRand(), getClrRand() * 4).scale(0.5F).time(32).spawnEntity(entity).swirl((int) (radius * 2), (int) (radius),
                            radius, (float) (0.25 / radius),
                            60, 1 / radius, entity, world, false, pos, ParticleBuilder.SwirlMotionType.IN, false,
                            true);
            ParticleBuilder.create(ParticleBuilder.Type.FLASH).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 95).element(BendingStyles.get(Darkbending.ID))
                    .clr(getClrRand(), getClrRand(), getClrRand(), getClrRand() * 4).scale(0.25F).time(8).spawnEntity(entity).swirl((int) (radius * 2), (int) (radius),
                            radius, (float) (0.25 / radius),
                            60, 1 / radius, entity, world, false, pos, ParticleBuilder.SwirlMotionType.IN, false,
                            true);
            ParticleBuilder.create(ParticleBuilder.Type.FLASH).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 95)
                    .clr(getClrRand(), getClrRand(), getClrRand(), getClrRand() * 4).scale(0.25F).time(8).spawnEntity(entity).swirl((int) (radius * 2), (int) (radius),
                            radius, (float) (0.25 / radius),
                            60, 1 / radius, entity, world, false, pos, ParticleBuilder.SwirlMotionType.IN, false,
                            true);

            //Particles going up:
//            int angle = entity.ticksExisted % 360;
//            for (int h = 0; h < (radius * 6)+ 2; h++) {
//                for (int i = 0; i < 2 * charge; i++) {
//                    double radians = Math.toRadians(angle + i * 60);
//                    double x = Math.cos(radians) * radius * (chargeDuration / (float) maxCharge);
//                    double y = radius * 12 * (h + 1) / (radius * 6 + 2F);
//                    double z = Math.sin(radians) * radius * (chargeDuration / (float) maxCharge);
//                    ParticleBuilder.create(ParticleBuilder.Type.FLASH)
//                            .spawnEntity(entity).pos(pos.add(x, y, z)).clr(getClrRand(), getClrRand(), getClrRand(), 100)
//                            .time(20).scale(0.15F * charge).vel(world.rand.nextGaussian() / 60,
//                                    world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60).spawn(world);
//                }
//            }

        }

        entity.world.playSound(null, new BlockPos(entity), SoundEvents.BLOCK_FIRE_EXTINGUISH, entity.getSoundCategory(),
                0.6F, 0.8F + world.rand.nextFloat() / 10);
        return chargeDuration >= maxCharge || !data.hasStatusControl(StatusControlController.RELEASE_ABYSS_END);
    }

    private int getClrRand() {
        return AvatarUtils.getRandomNumberInRange(1, 25);
    }

    private void applyMovementModifier(EntityLivingBase entity, float multiplier) {

        IAttributeInstance moveSpeed = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

        moveSpeed.removeModifier(ABYSS_END_MOVE_MOD_ID);

        moveSpeed.applyModifier(new AttributeModifier(ABYSS_END_MOVE_MOD_ID, "Abyssal End Movement Modifier", multiplier - 1, 1));

    }

    @Override
    public void onRemoved(BendingContext ctx) {
        super.onRemoved(ctx);
        BendingData data = ctx.getData();
        if (data.hasStatusControl(StatusControlController.RELEASE_ABYSS_END)) {
            data.addTickHandler(TickHandlerController.ABYSS_END_RAIN, ctx);
        }
        else data.addTickHandler(TickHandlerController.ABYSS_END_EXPLOSION, ctx);
    }
}
