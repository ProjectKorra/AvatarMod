package com.crowsofwar.avatar.bending.bending.earth.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.earth.AbilityEarthspikes;
import com.crowsofwar.avatar.bending.bending.earth.Earthbending;
import com.crowsofwar.avatar.entity.EntityEarthspike;
import com.crowsofwar.avatar.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandler;
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
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
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
            AbilityContext context = new AbilityContext(data, entity, ctx.getBender(), Raytrace.getTargetBlock(entity, range, false),
                    ability, abilityData.getPowerRating(), false);

            if (targetPos != null && targetPos.dist(entityPos) <= range) {
                abilityData.setSourceBlock(pickupBlock(ability, context, targetPos.toBlockPos()));
            } else {
                pos = Earthbending.getClosestEarthbendableBlock(entity, context, ability, 2);
                if (pos != null) {
                    abilityData.setSourceBlock(pickupBlock(ability, context, pos.toBlockPos()));
                }
            }
            if (Earthbending.isBendable(abilityData.getSourceBlock()))
                abilityData.incrementSourceTime();
            else abilityData.setSourceTime(0);
        }
        return false;
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
            maxSize, maxDamage;


            chargeTime = ability.getProperty(Ability.CHARGE_TIME, abilityData).intValue();
            cooldown = ability.getCooldown(abilityData);
            chiCost = ability.getChiCost(abilityData);
            exhaustion = ability.getExhaustion(abilityData);
            burnout = ability.getBurnOut(abilityData);
            chiHit = ability.getProperty(Ability.CHI_HIT, abilityData).floatValue();
            size = ability.getProperty(Ability.SIZE, abilityData).floatValue();
            damage = ability.getProperty(Ability.DAMAGE, abilityData).floatValue();
            maxDamage = ability.getProperty(Ability.MAX_DAMAGE, abilityData).floatValue();
            maxSize = ability.getProperty(Ability.MAX_SIZE, abilityData).floatValue();
            tier = ability.getCurrentTier(abilityData);

            chiHit = ability.powerModify(chiHit, abilityData);
            size = ability.powerModify(size, abilityData);
            damage = ability.powerModify(size, abilityData);
            maxDamage = ability.powerModify(maxDamage, abilityData);
            maxSize = ability.powerModify(maxSize, abilityData);
            chargeTime = (int) ability.powerModify(chargeTime, abilityData);

            chargedTime = Math.min(abilityData.getSourceTime(), chargeTime);

            chargeMult = chargedTime / (float) chargeTime;

            if (abilityData.getAbilityCooldown(entity) <= 0 && chargeMult > 0 && bender.consumeChi(chiCost)) {

                damage *= (0.5 + chargeMult / 2);
                size *= (0.5 + chargeMult / 2);
                chiHit *= (0.5 + chargeMult / 2);

                damage = Math.min(damage, maxDamage);
                size = Math.min(size, maxSize);

                EntityEarthspike earthspike = new EntityEarthspike(world);
                earthspike.setOwner(entity);
                earthspike.setTier(tier);
                earthspike.setEntitySize(size, size / 2);
                earthspike.setChiHit(chiHit);
                earthspike.setDamage(damage);
                earthspike.setPosition(abilityData.getSourceInfo().getBlockPos().add(0, 1, 0));
                earthspike.setTier(tier);
                earthspike.setAbility(ability);
                earthspike.setDamageSource("avatar_Earth_earthSpike");
                if (!world.isRemote)
                    world.spawnEntity(earthspike);

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

    private IBlockState pickupBlock(AbilityEarthspikes ability, AbilityContext ctx, BlockPos pos) {

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
            return ibs;
        } else {
            world.playSound(null, entity.getPosition(), SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS,
                    1, (float) (world.rand.nextGaussian() / 0.25 + 0.375));
        }
        return Blocks.AIR.getDefaultState();
    }
}
