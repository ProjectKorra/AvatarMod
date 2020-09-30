package com.crowsofwar.avatar.bending.bending.earth.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.SourceInfo;
import com.crowsofwar.avatar.bending.bending.earth.AbilityEarthspikes;
import com.crowsofwar.avatar.bending.bending.earth.Earthbending;
import com.crowsofwar.avatar.entity.EntityEarthspike;
import com.crowsofwar.avatar.entity.EntityEarthspikeSpawner;
import com.crowsofwar.avatar.entity.mob.EntityBender;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.avatar.util.data.*;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import com.crowsofwar.gorecore.util.VectorI;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.UUID;

public class EarthSpikeHandler extends TickHandler {

    private static final String EARTHSPIKE_MOVEMENT_MOD = "d241bdc5-1c7f-4b7a-b634-91a3710c5e2f";

    public EarthSpikeHandler(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        World world = ctx.getWorld();
        BendingData data = ctx.getData();
        AbilityData abilityData = data.getAbilityData("earth_spikes");
        AbilityEarthspikes ability = (AbilityEarthspikes) Abilities.get("earth_spikes");

        if (ability != null) {
            VectorI targetPos = ctx.getLookPosI();
            Vec3d lookPos = ctx.getBenderEntity().getPositionEyes(1.0F);
            VectorI entityPos = new VectorI((int) lookPos.x, (int) lookPos.y, (int) lookPos.z);
            Vector pos;
            int range = ability.getProperty(Ability.RANGE, abilityData).intValue();

            range = (int) ability.powerModify(range, abilityData);

            Raytrace.Result raytrace = Raytrace.raytrace(world, Vector.getEyePos(entity).toMinecraft(), entity.getLookVec(),
                    range, false);
            AbilityContext context = new AbilityContext(data, entity, ctx.getBender(), raytrace,
                    ability, abilityData.getPowerRating(), false);

            if (raytrace.hitSomething())
                targetPos = raytrace.getPos();
            if (targetPos != null && targetPos.dist(entityPos) <= range) {
                abilityData.getSourceInfo().setBlockPos(targetPos.toBlockPos());
                abilityData.setSourceInfo(findBlock(ability, context, targetPos.toBlockPos(), abilityData.getSourceTime()));
            } else {
                pos = Earthbending.getClosestEarthbendableBlock(entity, context, ability, 2);
                if (pos != null) {
                    abilityData.getSourceInfo().setBlockPos(pos.toBlockPos());
                    abilityData.setSourceInfo(findBlock(ability, context, pos.toBlockPos(), abilityData.getSourceTime()));
                }
            }
            if (Earthbending.isBendable(abilityData.getSourceBlock()))
                abilityData.incrementSourceTime();
            else abilityData.setSourceTime(0);
        }
        return !data.hasStatusControl(StatusControlController.RELEASE_EARTH_SPIKE);
    }

    @Override
    public void onRemoved(BendingContext ctx) {
        super.onRemoved(ctx);

        World world = ctx.getWorld();
        Bender bender = ctx.getBender();
        EntityLivingBase entity = ctx.getBenderEntity();
        AbilityEarthspikes ability = (AbilityEarthspikes) Abilities.get("earth_spikes");
        AbilityData abilityData = AbilityData.get(entity, "earth_spikes");

        if (ability != null && abilityData != null) {
            int chargeTime, chargedTime, cooldown, tier;
            float damage, knockback, chiHit, chiCost, exhaustion, burnout, size, chargeMult,
                    maxSize, maxDamage, xp;


            chargeTime = ability.getProperty(Ability.CHARGE_TIME, abilityData).intValue();
            cooldown = ability.getCooldown(abilityData);
            chiCost = ability.getChiCost(abilityData);
            exhaustion = ability.getExhaustion(abilityData);
            burnout = ability.getBurnOut(abilityData);
            chiHit = ability.getProperty(Ability.CHI_HIT, abilityData).floatValue();
            size = ability.getProperty(Ability.SIZE, abilityData).floatValue() * 1.5F;
            damage = ability.getProperty(Ability.DAMAGE, abilityData).floatValue();
            maxDamage = ability.getProperty(Ability.MAX_DAMAGE, abilityData).floatValue();
            maxSize = ability.getProperty(Ability.MAX_SIZE, abilityData).floatValue() * 1.5F;
            knockback = ability.getProperty(Ability.KNOCKBACK, abilityData).floatValue() / 10;
            tier = ability.getCurrentTier(abilityData);
            xp = ability.getProperty(Ability.XP_HIT, abilityData).floatValue();

            chiHit = ability.powerModify(chiHit, abilityData);
            size = ability.powerModify(size, abilityData);
            damage = ability.powerModify(damage, abilityData);
            maxDamage = ability.powerModify(maxDamage, abilityData);
            maxSize = ability.powerModify(maxSize, abilityData);
            chargeTime = (int) ability.powerModify(chargeTime, abilityData);
            knockback = ability.powerModify(knockback, abilityData);

            chargedTime = Math.min(abilityData.getSourceTime(), chargeTime);

            chargeMult = chargedTime / (float) chargeTime;

            if (bender.isCreativeMode())
                burnout = chiCost = exhaustion = cooldown = 0;
            else if (entity instanceof EntityBender)
                chiCost = 0;

            if (abilityData.getAbilityCooldown(entity) <= 0 && chargeMult > 0 && bender.consumeChi(chiCost)) {
                if (Earthbending.isBendable(abilityData.getSourceBlock())) {
                    damage *= (1.0 + chargeMult / 2F);
                    size *= (1.0 + chargeMult / 2F);
                    chiHit *= (1.0 + chargeMult / 2F);

                    damage = Math.min(damage, maxDamage);
                    size = Math.min(size, maxSize);

                    BlockPos pos = abilityData.getSourceInfo().getBlockPos();
                    Vector realPos;
                    IBlockState state = abilityData.getSourceBlock();
                    int range = ability.getProperty(Ability.RANGE, abilityData).intValue();

                    range = (int) ability.powerModify(range, abilityData);

                    Raytrace.Result raytrace = Raytrace.raytrace(world, Vector.getEyePos(entity).toMinecraft(), entity.getLookVec(),
                            range, false);

                    realPos = Vector.ZERO;
                    if (raytrace.hitSomething() && raytrace.getPosPrecise() != null)
                        realPos = raytrace.getPosPrecise();
                    else realPos = realPos.plus(pos.getX(), pos.getY() + 0.5, pos.getZ());

                    if (Earthbending.isBendable(world, realPos.toBlockPos().down(), state, 2)) {
                        EntityEarthspike earthspike = new EntityEarthspike(world);
                        earthspike.setOwner(entity);
                        earthspike.setTier(tier);
                        earthspike.setMaxEntitySize(size, size / 1.5F);
                        earthspike.setEntitySize(0.05F, 0.05F);
                        earthspike.setChiHit(chiHit);
                        earthspike.setDamage(damage);
                        earthspike.setPosition(realPos);
                        earthspike.setTier(tier);
                        earthspike.setVelocity(Vec3d.ZERO);
                        earthspike.setLifeTime((int) (size * 30));
                        earthspike.setAbility(ability);
                        earthspike.setPush(knockback);
                        earthspike.setDamageSource("avatar_Earth_earthSpike");
                        earthspike.setXp(xp);
                        if (!world.isRemote)
                            world.spawnEntity(earthspike);
                        else {
                            for (int i = 0; i < (int) (size * 30); i++)
                                world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, realPos.x(), realPos.y() + i / (size * 30),
                                        realPos.z(), 0, 0, 0, Block.getStateId(state));
                        }

                        if (ability.getBooleanProperty(AbilityEarthspikes.TRACE_SPIKES, abilityData)) {
                           // EntityEarthspikeSpawner spawner = new EntityEarthspikeSpawner(world);
                        }
                    }
                    else bender.sendMessage("avatar.earthSourceFail");

                    //Inhibitors
                    abilityData.setAbilityCooldown(cooldown);
                    if (entity instanceof EntityPlayer)
                        ((EntityPlayer) entity).addExhaustion(exhaustion);
                    abilityData.addBurnout(burnout);
                }
                abilityData.clearSourceTime();
                abilityData.clearSourceBlock();

            }
        }
    }

    public void addModifier(EntityLivingBase entity, float mult) {
        AttributeModifier speedMod = new AttributeModifier(EARTHSPIKE_MOVEMENT_MOD, mult - 1, 1);
    }

    public void removeModifier(EntityLivingBase entity) {
        AttributeModifier speedMod = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(UUID.fromString(EARTHSPIKE_MOVEMENT_MOD));
        if (speedMod != null && entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(speedMod))
            entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(speedMod);
    }

    private SourceInfo findBlock(AbilityEarthspikes ability, AbilityContext ctx, BlockPos pos, int time) {
        SourceInfo sourceInfo = new SourceInfo();
        World world = ctx.getWorld();
        EntityLivingBase entity = ctx.getBenderEntity();

        IBlockState ibs = world.getBlockState(pos);
        if (!ibs.isFullBlock() && !Earthbending.isBendable(ibs))
            ibs = world.getBlockState(pos.down());

        Block block = ibs.getBlock();

        boolean bendable = Earthbending.isBendable(ibs);
        bendable |= !bendable && !Earthbending.isBendable(world, pos.down(), world.getBlockState(pos.down()), 2)
                && !(block instanceof BlockSnow || block instanceof BlockTallGrass) && world.getBlockState(pos).isNormalCube();

        if (!bendable)
            if (Earthbending.getClosestEarthbendableBlock(entity, ctx, ability, 2) != null)
                pos = Objects.requireNonNull(Earthbending.getClosestEarthbendableBlock(entity, ctx, ability, 2)).toBlockPos();

        ibs = world.getBlockState(pos);
        if (!ibs.isFullBlock() && !Earthbending.isBendable(world, pos, ibs, 2))
            ibs = world.getBlockState(pos.down());

        block = ibs.getBlock();

        bendable = Earthbending.isBendable(world, pos, ibs, 2);
        bendable |= !bendable && !Earthbending.isBendable(world, pos.down(), world.getBlockState(pos.down()), 2)
                && !(block instanceof BlockSnow || block instanceof BlockTallGrass) && world.getBlockState(pos).isNormalCube();

        if (!world.isAirBlock(pos) && bendable) {
            sourceInfo.setState(ibs);
            sourceInfo.setBlockPos(pos);
            sourceInfo.setWorld(world);
            sourceInfo.setTime(time);
        } else {
            world.playSound(null, entity.getPosition(), SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS,
                    1, (float) (world.rand.nextGaussian() / 0.25 + 0.375));
        }
        return sourceInfo;
    }
}
