package com.crowsofwar.avatar.bending.bending.custom.hyper.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.custom.ki.AbilitySpiritBomb;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityKiBall;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

import static com.crowsofwar.avatar.bending.bending.Ability.*;
import static com.crowsofwar.avatar.bending.bending.custom.demonic.AbilityHellBastion.SLOW_MULT;
import static com.crowsofwar.avatar.util.data.StatusControlController.RELEASE_SPIRIT_BOMB;

public class HyperImplosionHandler extends TickHandler {
    public static final UUID SPIRIT_BOMB_MOVEMENT_MOD_ID = UUID.randomUUID();

    public HyperImplosionHandler(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {
        World world = ctx.getWorld();
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();
        Bender bender = ctx.getBender();
        AbilityData abilityData = ctx.getData().getAbilityData("spirit_bomb");
        AbilitySpiritBomb spiritBomb = (AbilitySpiritBomb) Abilities.get("spirit_bomb");

        float charge;
        //4 stages, max charge of 4.

        if (abilityData != null && spiritBomb != null) {


            float powerMod = (float) abilityData.getDamageMult();
            float xpMod = abilityData.getXpModifier();

            int duration = data.getTickHandlerDuration(this);
            float damage = spiritBomb.getProperty(EFFECT_DAMAGE, abilityData).floatValue();
            float slowMult = spiritBomb.getProperty(SLOW_MULT, abilityData).floatValue();


            float knockBack = spiritBomb.getProperty(KNOCKBACK, abilityData).floatValue() / 4;
            float radius = spiritBomb.getProperty(EFFECT_RADIUS, abilityData).floatValue();
            float speed = spiritBomb.getProperty(SPEED, abilityData).floatValue() / 5;
            float maxEntitySize = spiritBomb.getProperty(SIZE, abilityData).floatValue();
            int performanceAmount = spiritBomb.getProperty(PERFORMANCE, abilityData).intValue();
            float shockwaveSpeed;

            float exhaustion, burnout;
            int cooldown;
            exhaustion = spiritBomb.getExhaustion(abilityData);
            burnout = spiritBomb.getBurnOut(abilityData);
            cooldown = spiritBomb.getCooldown(abilityData);

            if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
                exhaustion = burnout = cooldown = 0;
            }

            //Makes sure the charge is never 0.
//            charge = Math.max((int) (3 * (duration / durationToFire)) + 1, 1);
//            charge = Math.min(charge, 4);
            //We don't want the charge going over 4.

            charge = 4;
            maxEntitySize *= powerMod * xpMod;
            damage *= powerMod * xpMod;
            radius *= powerMod * xpMod;
            knockBack *= powerMod * xpMod;
            slowMult *= powerMod * xpMod;
            speed *= powerMod * xpMod;

            float movementMultiplier = slowMult - 0.7f * MathHelper.sqrt(duration / 40F);

            //how fast the shockwave's particle speed is.
            shockwaveSpeed = knockBack;
            //Affect things by the charge. The charge, at stage 3, should set everything to its max.
            damage *= (0.20 + 0.20 * charge);
            //Results in a bigger radius so that it blocks projectiles.
            radius *= (0.60 + 0.10 * charge);
            knockBack *= (0.60 + 0.10 * charge);
            speed *= (1F + charge / 4F);
            performanceAmount *= (0.20 + 0.20 * charge);


            applyMovementModifier(entity, MathHelper.clamp(movementMultiplier, 0.1f, 1));


            //Some kind of sound effect
            if (entity.ticksExisted % 10 == 0)
                world.playSound(null, new BlockPos(entity), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.25F * charge, 0.4F + world.rand.nextFloat() / 10);

            //Charging the ball
            EntityKiBall ball = AvatarEntity.lookupEntity(world, EntityKiBall.class, entityKiBall -> entityKiBall.getOwner() == entity);
            if (ball != null) {
                ball.setDamage(ball.getDamage() + damage);
                ball.setEntitySize(ball.getAvgSize() < maxEntitySize ? ball.getAvgSize() + 0.1F : maxEntitySize);
                ball.setTier(spiritBomb.getCurrentTier(abilityData));
                ball.setChiHit(3);
                ball.setXp(3);
                ball.setPerformanceAmount(performanceAmount);
            }
            //Dropping the ball
            if (!data.hasStatusControl(RELEASE_SPIRIT_BOMB)) {

                //Behaviour to drop it
                if (ball != null) {
                    ball.setBehaviour(new SpiritBombBehaviour());
                    ball.setExplosionDamage(damage);
                    ball.setExplosionSize(radius * 3);
                }
                entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(SPIRIT_BOMB_MOVEMENT_MOD_ID);

                world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE,
                        SoundCategory.BLOCKS, 1, 0.5F);

                return true;
            }
            return !data.hasStatusControl(RELEASE_SPIRIT_BOMB);
        } else {
            return true;
        }
    }


    private void applyMovementModifier(EntityLivingBase entity, float multiplier) {

        IAttributeInstance moveSpeed = entity.getEntityAttribute(SharedMonsterAttributes
                .MOVEMENT_SPEED);

        moveSpeed.removeModifier(SPIRIT_BOMB_MOVEMENT_MOD_ID);

        moveSpeed.applyModifier(new AttributeModifier(SPIRIT_BOMB_MOVEMENT_MOD_ID,
                "Spirit Bomb charge modifier", multiplier - 1, 1));

    }

    @Override
    public void onRemoved(BendingContext ctx) {
        super.onRemoved(ctx);
        AbilityData abilityData = AbilityData.get(ctx.getBenderEntity(), "spirit_bomb");
        if (abilityData != null)
            abilityData.setRegenBurnout(true);
        if (ctx.getBenderEntity().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(SPIRIT_BOMB_MOVEMENT_MOD_ID) != null)
            ctx.getBenderEntity().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(SPIRIT_BOMB_MOVEMENT_MOD_ID);

    }

    public static class SpiritBombBehaviour extends OffensiveBehaviour {

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            World world = entity.world;
            if (entity.getOwner() != null) {
                Vec3d targetPos = entity.getOwner().getPositionVector();
                targetPos = targetPos.add(entity.getOwner().getLookVec().scale(25));
                //Sets the y to the player's y.
                targetPos = new Vec3d(targetPos.x, entity.getOwner().posY, targetPos.z);
                //Drags it to where you're looking
                entity.setVelocity(targetPos.subtract(entity.getPositionVector()).scale(0.025).subtract(0, 0.25, 0));
                if (world.isRemote) {
                    Vec3d centre = AvatarEntityUtils.getMiddleOfEntity(entity);
                    float size = 0.75F * entity.getAvgSize() * (1 / entity.getAvgSize());
                    int rings = (int) (entity.getAvgSize() * 4);
                    int particles = (int) (entity.getAvgSize() * Math.PI);

                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).scale(size).time(8 + AvatarUtils.getRandomNumberInRange(0, 4)).glow(true)
                            .element(BendingStyles.get(entity.getElement())).clr(40, 240, 255).spawnEntity(entity).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 30)
                            .swirl(rings, particles, entity.getAvgSize() * 1.1F, size * 15, entity.getAvgSize() * 10, (-1 / size),
                                    entity, world, false, centre, ParticleBuilder.SwirlMotionType.OUT, false, true);
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).scale(size).time(8 + AvatarUtils.getRandomNumberInRange(0, 4))
                            .element(BendingStyles.get(entity.getElement())).clr(255, 255, 255).spawnEntity(entity).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 60)
                            .swirl(rings, particles, entity.getAvgSize() * 1.1F, size * 15, entity.getAvgSize() * 10, (-1 / size),
                                    entity, world, false, centre, ParticleBuilder.SwirlMotionType.OUT, false, true);
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
