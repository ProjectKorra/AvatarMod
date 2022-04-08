package com.crowsofwar.avatar.bending.bending.custom.hyper.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.custom.dark.Darkbending;
import com.crowsofwar.avatar.bending.bending.custom.hyper.AbilityHyperAdvent;
import com.crowsofwar.avatar.bending.bending.custom.hyper.Hyperbending;
import com.crowsofwar.avatar.bending.bending.custom.hyper.statctrls.StatCtrlHyperImplosion;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.EntityHyperBall;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.data.Behavior;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.*;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.bending.bending.Ability.*;

public class AdventRainHandler extends TickHandler {

    public AdventRainHandler(int id) {
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


        }

        if (entity.ticksExisted % (5 + (AvatarUtils.getRandomNumberInRange(1, 5))) == 0) {
            //Radius gets smaller as time goes on
            Vec3d look = entity.getLookVec().scale((radius - (chargeDuration / (float) maxCharge) * radius));
            EntityHyperBall ball = new EntityHyperBall(world);
            ball.setAbility(advent);
            ball.setOwner(entity);
            ball.setElement(Hyperbending.ID);
            ball.setPosition(Vector.getEyePos(entity));
            ball.setBehaviour(new AdventBehaviour());
            ball.setVelocity(look.x * world.rand.nextGaussian(),  world.rand.nextDouble(),
                    look.z * world.rand.nextGaussian());
            ball.setLifeTime(120);
            ball.setDamage(advent.getProperty(DAMAGE, abilityData).floatValue() / 2);
            ball.setTier(7);
            ball.setEntitySize(advent.getProperty(RADIUS, abilityData).floatValue() / 8);
            //Other attributes set later
            if (!world.isRemote)
                world.spawnEntity(ball);
        }
        return !data.hasStatusControl(StatusControlController.RELEASE_HYPER_ADVENT) ||
                data.getTickHandlerDuration(this) > maxCharge;
    }

    private int getClrRand() {
        return AvatarUtils.getRandomNumberInRange(1, 255);
    }

    @Override
    public void onRemoved(BendingContext ctx) {
        super.onRemoved(ctx);
        BendingData data = ctx.getData();
        if (data.hasStatusControl(StatusControlController.RELEASE_HYPER_ADVENT)) {
            data.addTickHandler(TickHandlerController.HYPER_ADVENT_EXPLOSION, ctx);
        }
    }

    public static class AdventBehaviour extends OffensiveBehaviour {

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            entity.addVelocity(0, -9.82 / 400, 0);
            if (entity.onCollideWithSolid())
                entity.Explode();

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
