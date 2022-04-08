package com.crowsofwar.avatar.bending.bending.custom.hyper.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.custom.dark.Darkbending;
import com.crowsofwar.avatar.bending.bending.custom.hyper.AbilityHyperAdvent;
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
public class AdventChargeHandler extends TickHandler {
    public static final UUID ADVENT_MOVE_MOD_ID = UUID.fromString("f8b142db-54e1-4002-b6e0-24d71ca28cee");

    public AdventChargeHandler(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {
        BendingData data = ctx.getData();
        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();
        AbilityData abilityData = data.getAbilityData("hyper_advent");
        AbilityHyperAdvent advent = (AbilityHyperAdvent) Abilities.get("hyper_advent");
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
        if (entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(ADVENT_MOVE_MOD_ID) == null)
            applyMovementModifier(entity, movementModifier);

        //Makes sure the charge is never 0.
        charge = Math.max((3 * (chargeDuration / maxCharge)) + 1, 1);
        charge = Math.min(charge, 4);
        //We don't want the charge going over 4.

        float radius = advent.getProperty(RADIUS, abilityData).floatValue() / 6;
        radius *= (0.60 + 0.10 * charge);

        Vec3d pos = entity.getPositionVector().add(0, entity.getEyeHeight() / 2, 0).add(entity.getLookVec().scale(0.5));
        if (world.isRemote) {
            //Swirl:
            ParticleBuilder.create(ParticleBuilder.Type.FLASH).glow(true).element(BendingStyles.get(Darkbending.ID))
                    .clr(getClrRand(), getClrRand(), getClrRand(), getClrRand()).scale(0.5F).time(32).spawnEntity(entity).swirl((int) (radius * 2), (int) (radius),
                            radius, (float) (0.25 / radius),
                            60, 1 / radius, entity, world, false, pos, ParticleBuilder.SwirlMotionType.IN, false,
                            true);
            ParticleBuilder.create(ParticleBuilder.Type.FLASH).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 40)
                    .clr(getClrRand(), getClrRand(), getClrRand(), getClrRand()).scale(0.5F).time(32).spawnEntity(entity).swirl((int) (radius * 2), (int) (radius),
                            radius, (float) (0.25 / radius),
                            60, 1 / radius, entity, world, false, pos, ParticleBuilder.SwirlMotionType.IN, false,
                            true);
            ParticleBuilder.create(ParticleBuilder.Type.FLASH).glow(true).element(BendingStyles.get(Darkbending.ID))
                    .clr(getClrRand(), getClrRand(), getClrRand(), getClrRand()).scale(0.25F).time(8).spawnEntity(entity).swirl((int) (radius * 2), (int) (radius),
                            radius, (float) (0.25 / radius),
                            60, 1 / radius, entity, world, false, pos, ParticleBuilder.SwirlMotionType.IN, false,
                            true);
            ParticleBuilder.create(ParticleBuilder.Type.FLASH).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 40)
                    .clr(getClrRand(), getClrRand(), getClrRand(), getClrRand()).scale(0.25F).time(8).spawnEntity(entity).swirl((int) (radius * 2), (int) (radius),
                            radius, (float) (0.25 / radius),
                            60, 1 / radius, entity, world, false, pos, ParticleBuilder.SwirlMotionType.IN, false,
                            true);

            //Particles going up:
            int angle = entity.ticksExisted % 360;
            for (int i = 0; i < 6; i++) {
                double radians = Math.toRadians(angle + i * 60);
                double x = Math.cos(radians) * radius * (chargeDuration / (float) maxCharge);
                double y = chargeDuration / (float) maxCharge * radius * 12;
                double z = Math.sin(radians) * radius * (chargeDuration / (float) maxCharge);;
                ParticleBuilder.create(ParticleBuilder.Type.FLASH)
                        .spawnEntity(entity).pos(pos.add(x, y, z)).clr(getClrRand(), getClrRand(), getClrRand())
                        .glow(true).time(20).scale(0.5F).vel(world.rand.nextGaussian() / 60,
                                world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60).spawn(world);

            }

        }

        entity.world.playSound(null, new BlockPos(entity), SoundEvents.BLOCK_FIRE_EXTINGUISH, entity.getSoundCategory(),
                0.6F, 0.8F + world.rand.nextFloat() / 10);
        return chargeDuration >= maxCharge || !data.hasStatusControl(StatusControlController.RELEASE_HYPER_ADVENT);
    }

    private int getClrRand() {
        return AvatarUtils.getRandomNumberInRange(1, 255);
    }

    private void applyMovementModifier(EntityLivingBase entity, float multiplier) {

        IAttributeInstance moveSpeed = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

        moveSpeed.removeModifier(ADVENT_MOVE_MOD_ID);

        moveSpeed.applyModifier(new AttributeModifier(ADVENT_MOVE_MOD_ID, "Hyper Advent Movement Modifier", multiplier - 1, 1));

    }

    @Override
    public void onRemoved(BendingContext ctx) {
        super.onRemoved(ctx);
        BendingData data = ctx.getData();
        if (data.hasStatusControl(StatusControlController.RELEASE_HYPER_ADVENT)) {
            data.addTickHandler(TickHandlerController.HYPER_ADVENT_RAIN, ctx);
        }
    }
}
