package com.crowsofwar.avatar.bending.bending.custom.hyper.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.combustion.AbilityMegatonDive;
import com.crowsofwar.avatar.bending.bending.custom.hyper.AbilityHyperAdvent;
import com.crowsofwar.avatar.bending.bending.custom.hyper.Hyperbending;
import com.crowsofwar.avatar.bending.bending.fire.Firebending;
import com.crowsofwar.avatar.blocks.BlockTemp;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.util.damageutils.DamageUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.bending.bending.custom.hyper.tickhandlers.AdventChargeHandler.ADVENT_MOVE_MOD_ID;
import static com.crowsofwar.gorecore.util.Vector.toRectangular;
import static java.lang.Math.toRadians;

public class AdventExplosionHandler extends TickHandler {

    public AdventExplosionHandler(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {
        BendingData data = ctx.getData();
        EntityLivingBase entity = ctx.getBenderEntity();
        AbilityData abilityData = data.getAbilityData("hyper_advent");
        AbilityHyperAdvent advent = (AbilityHyperAdvent) Abilities.get("hyper_advent");
        World world = ctx.getWorld();

        int duration = data.getTickHandlerDuration(this);
        int lifetime;

        if (advent != null) {

            float radius = advent.getProperty(Ability.RADIUS, abilityData).floatValue() / 2;
            float size = Math.min(0.5F * radius, 4F);
            int rings = (int) (radius * 6);
            int particles = (int) (radius * Math.PI * 2);

            if (world.isRemote) {
                world.playSound(entity.posX, entity.posY, entity.posZ,
                        SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1.0F, 1.0F, false);
                //  explosion(entity, advent, world, size * 1.5F, rings, particles, -2);
                explosion(entity, advent, world, size * 2F, rings, particles, -4);
                explosion(entity, advent, world, size * 0.25F, rings, particles, -1);
            }
            //Explode
            if (!world.isRemote) {
                AxisAlignedBB targetBox = entity.getEntityBoundingBox().grow(advent.getProperty(Ability.RADIUS,
                        abilityData).floatValue() / 2);
                List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class,
                        targetBox);
                if (!targets.isEmpty()) {
                    for (EntityLivingBase hit : targets) {
                        if (DamageUtils.canDamage(entity, hit)) {
                            hit.attackEntityFrom(AvatarDamageSource.COMBUSTION,
                                    advent.getProperty(Ability.DAMAGE, abilityData).floatValue());
                            Vec3d vel = hit.getPositionVector().subtract(entity.getPositionVector()).scale(
                                    advent.getProperty(Ability.KNOCKBACK, abilityData).floatValue());
                            hit.addVelocity(vel.x, vel.y, vel.z);
                        }
                    }
                }
            }
            abilityData.setUseNumber(abilityData.getUseNumber() + 1);

        }
        return abilityData.getUseNumber() > 1;
    }

    private void explosion(EntityLivingBase entity, AbilityHyperAdvent advent, World world, float size, int rings, int particles, float velMult) {
        Vec3d centre = AvatarEntityUtils.getMiddleOfEntity(entity);

        ParticleBuilder.create(ParticleBuilder.Type.FLASH).scale(size).time(36 + AvatarUtils.getRandomNumberInRange(0, 4)).glow(true)
                .element(BendingStyles.get(Hyperbending.ID)).
                clr(getClrRand(), getClrRand(), getClrRand()).spawnEntity(entity).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 30)
                .swirl(rings, particles, size * 1.1F, size * 10, size * 10, -15, entity,
                        world, false, centre, ParticleBuilder.SwirlMotionType.OUT, false, true, true);
        ParticleBuilder.create(ParticleBuilder.Type.FLASH).scale(size).time(36 + AvatarUtils.getRandomNumberInRange(0, 4))
                .element(BendingStyles.get(Hyperbending.ID)).
                clr(getClrRand(), getClrRand(), getClrRand(), getClrRand()).spawnEntity(entity).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 60)
                .swirl(rings, particles, size * 1.1F, size * 10, size * 10, -15, entity,
                        world, false, centre, ParticleBuilder.SwirlMotionType.OUT, false, true, true);
    }

    private int getClrRand() {
        return AvatarUtils.getRandomNumberInRange(1, 255);
    }

    @Override
    public void onRemoved(BendingContext ctx) {
        super.onRemoved(ctx);
        EntityLivingBase entity = ctx.getBenderEntity();
        if (entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(ADVENT_MOVE_MOD_ID) != null)
            entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(ADVENT_MOVE_MOD_ID);
        ctx.getData().removeStatusControl(StatusControlController.RELEASE_HYPER_ADVENT);
        ctx.getData().getAbilityData("hyper_advent").setUseNumber(0);
    }
}
