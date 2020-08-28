/*
  This file is part of AvatarMod.

  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.bending.bending.earth;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.entity.data.FloatingBlockBehavior;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.gorecore.util.Vector;
import com.crowsofwar.gorecore.util.VectorI;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.util.data.StatusControlController.PLACE_BLOCK;
import static com.crowsofwar.avatar.util.data.StatusControlController.THROW_BLOCK;

/**
 * @author CrowsOfWar
 */
public class AbilityEarthControl extends Ability {

    private static final String
            BOOMERANG = "boomerang",
            BLOCK_HITS = "blockHits",
            TURN_SOLID = "turnSolid",
            EXPLOSION = "explosion";

    private final Random random;

    public AbilityEarthControl() {
        super(Earthbending.ID, "earth_control");
        this.random = new Random();
        requireRaytrace(-1, true);
    }

    @Override
    public void init() {
        super.init();
        addProperties(BLOCK_HITS, RANGE, RADIUS);
        addBooleanProperties(BOOMERANG, TURN_SOLID, EXPLOSION);
    }

    @Override
    public boolean isUtility() {
        return true;
    }

    @Override
    public void execute(AbilityContext ctx) {

        VectorI targetPos = ctx.getLookPosI();
        Vec3d lookPos = ctx.getBenderEntity().getPositionEyes(1.0F);
        VectorI entityPos = new VectorI((int) lookPos.x, (int) lookPos.y, (int) lookPos.z);
        Vector pos;
        EntityLivingBase entity = ctx.getBenderEntity();
        int range = getProperty(RANGE, ctx).intValue();

        if (targetPos != null && targetPos.dist(entityPos) <= range) {
            pickupBlock(ctx, targetPos.toBlockPos());
        } else {
            pos = Earthbending.getClosestEarthbendableBlock(entity, ctx, this);
            if (pos != null) {
                pickupBlock(ctx, pos.toBlockPos());
            }
        }
    }

    private void pickupBlock(AbilityContext ctx, BlockPos pos) {

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
        bendable |= !bendable && !Earthbending.isBendable(world.getBlockState(pos.down()))
                && !(block instanceof BlockSnow || block instanceof BlockTallGrass) && world.getBlockState(pos).isNormalCube();

        if (!bendable)
            if (Earthbending.getClosestEarthbendableBlock(entity, ctx, this) != null)
                pos = Earthbending.getClosestEarthbendableBlock(entity, ctx, this).toBlockPos();

        ibs = world.getBlockState(pos);
        if (!ibs.isFullBlock() && !Earthbending.isBendable(ibs))
            ibs = world.getBlockState(pos.down());

        block = ibs.getBlock();

        bendable = Earthbending.isBendable(ibs);
        bendable |= !bendable && !Earthbending.isBendable(world.getBlockState(pos.down()))
                && !(block instanceof BlockSnow || block instanceof BlockTallGrass) && world.getBlockState(pos).isNormalCube();

        if (!world.isAirBlock(pos) && bendable && heldBlocks < maxBlocks) {
            AbilityData abilityData = ctx.getData().getAbilityData(this);

            if (bender.consumeChi(getChiCost(abilityData) / 4)) {


                EntityFloatingBlock floating = new EntityFloatingBlock(world, ibs);
                floating.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                floating.setItemDropsEnabled(!bender.isCreativeMode());

                float damageMult = getProperty(DAMAGE, ctx).floatValue();
                float chiOnHit = getProperty(CHI_HIT, ctx).floatValue();
                float knockback = getProperty(KNOCKBACK, ctx).floatValue() / 8;

                damageMult *= abilityData.getDamageMult() * abilityData.getXpModifier();

                double dist = 2.5;
                Vector force = new Vector(0, Math.sqrt(20 * dist), 0);
                floating.setVelocity(force);
                floating.setBehavior(new FloatingBlockBehavior.PickUp());
                floating.setOwner(entity);
                floating.setAbility(this);
                floating.setDamageMult(damageMult);
                floating.setHitsLeft(getProperty(BLOCK_HITS, ctx).intValue());
                floating.setXp(getProperty(XP_HIT, ctx).floatValue());
                floating.setLifeTime((int) (getProperty(LIFETIME, ctx).intValue() * abilityData.getXpModifier() * abilityData.getDamageMult()));
                floating.setChiHit(chiOnHit);
                floating.setBoomerang(getBooleanProperty(BOOMERANG, ctx));
                floating.setPush(knockback);
                floating.setExplosionDamage(damageMult / 4);
                floating.setExplosionSize(damageMult / 6);
                floating.setExplosionStrength(damageMult / 6);
                floating.setTurnSolid(getBooleanProperty(TURN_SOLID, ctx));
                floating.setExplosion(getBooleanProperty(EXPLOSION, ctx));
                floating.setDamageSource("avatar_Earth_floatingBlock");
                floating.setTier(getCurrentTier(ctx));


                if (STATS_CONFIG.preventPickupBlockGriefing) {
                    floating.setItemDropsEnabled(false);
                } else {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                }

                if (!world.isRemote)
                    world.spawnEntity(floating);

                SoundType sound = block.getSoundType();
                if (sound != null) {
                    world.playSound(null, floating.getPosition(), sound.getBreakSound(),
                            SoundCategory.PLAYERS, sound.getVolume(), sound.getPitch());
                }

                if (!data.hasStatusControl(PLACE_BLOCK))
                    data.addStatusControl(PLACE_BLOCK);
                if (!data.hasStatusControl(THROW_BLOCK))
                    data.addStatusControl(THROW_BLOCK);

            }

        } else {
            world.playSound(null, entity.getPosition(), SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS,
                    1, (float) (random.nextGaussian() / 0.25 + 0.375));
        }

    }

    @Override
    public boolean isProjectile() {
        return true;
    }

    @Override
    public boolean isOffensive() {
        return true;
    }

    @Override
    public int getCooldown(AbilityContext ctx) {
        return 0;
    }

    @Override
    public float getBurnOut(AbilityContext ctx) {
        return 0;
    }

    @Override
    public float getExhaustion(AbilityContext ctx) {
        return 0;
    }
}
