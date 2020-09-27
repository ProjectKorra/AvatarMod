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
                pickupBlock(ability, context, targetPos.toBlockPos());
            } else {
                pos = Earthbending.getClosestEarthbendableBlock(entity, context, ability, 2);
                if (pos != null) {
                    pickupBlock(ability, context, pos.toBlockPos());
                }
            }
        }
        return false;
    }

    @Override
    public void onRemoved(BendingContext ctx) {
        super.onRemoved(ctx);
        World world = ctx.getWorld();
        EntityLivingBase entity = ctx.getBenderEntity();
        AbilityEarthspikes ability = (AbilityEarthspikes) Abilities.get("earth_spikes");
        AbilityData abilityData = AbilityData.get(entity, "earth_spikes");

        if (ability != null && abilityData != null) {
            EntityEarthspike earthspike = new EntityEarthspike(world);
            earthspike.setOwner(entity);
            if (!world.isRemote)
                world.spawnEntity(earthspike);
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

    private void pickupBlock(AbilityEarthspikes ability, AbilityContext ctx, BlockPos pos) {

        World world = ctx.getWorld();
        Bender bender = ctx.getBender();
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();

        IBlockState ibs = world.getBlockState(pos);
        if (!ibs.isFullBlock() && !Earthbending.isBendable(ibs))
            ibs = world.getBlockState(pos.down());

        Block block = ibs.getBlock();

        int maxBlocks = 1;
        int heldBlocks = 0;

        if (ctx.getLevel() == 2)
            maxBlocks = 2;
        else if (ctx.getDynamicPath().equals(AbilityData.AbilityTreePath.FIRST))
            maxBlocks = 3;

        List<EntityFloatingBlock> blocks = world.getEntitiesWithinAABB(EntityFloatingBlock.class,
                entity.getEntityBoundingBox().grow(3, 2, 3));
        for (EntityFloatingBlock b : blocks) {
            if (b.getController() == entity)
                heldBlocks++;
        }

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

        if (!world.isAirBlock(pos) && bendable && heldBlocks < maxBlocks) {


        } else {
            world.playSound(null, entity.getPosition(), SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS,
                    1, (float) (world.rand.nextGaussian() / 0.25 + 0.375));
        }

    }
}
