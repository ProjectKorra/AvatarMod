package com.crowsofwar.avatar.bending.bending.earth.statctrls;

import akka.japi.Pair;
import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.earth.AbilityWall;
import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.entity.EntityWall;
import com.crowsofwar.avatar.entity.EntityWallSegment;
import com.crowsofwar.avatar.entity.data.FloatingBlockBehavior;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.avatar.util.Raytrace.Result;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Objects;

import static com.crowsofwar.avatar.bending.bending.Ability.DAMAGE;
import static com.crowsofwar.avatar.bending.bending.earth.AbilityWall.SIZE_MAX;
import static com.crowsofwar.avatar.bending.bending.earth.AbilityWall.WALL_REACH;
import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;

/**
 * @author Aang23
 */
public class StatCtrlShootWall extends StatusControl {

    public StatCtrlShootWall() {
        super(22, AvatarControl.CONTROL_LEFT_CLICK_DOWN, CrosshairPosition.LEFT_OF_CROSSHAIR);
    }

    @Override
    public boolean execute(BendingContext ctx) {
        World world = ctx.getWorld();
        EntityLivingBase entity = ctx.getBenderEntity();

        Vector start = new Vector(entity.getPositionVector());
        Vector direction = new Vector(entity.getLookVec());
        AbilityWall ability = (AbilityWall) Abilities.get("wall");
        AbilityData abilityData = AbilityData.get(entity, "wall");

        if (ability != null && abilityData != null) {
            double range = ability.getProperty(WALL_REACH, abilityData).doubleValue() * abilityData.getDamageMult() * abilityData.getXpModifier();
            Result raytrace = Raytrace.raytrace(world, start, direction, range, true);
            if (raytrace.hitSomething()) {
                Vector stopAt = raytrace.getPosPrecise();
                if (stopAt != null)
                    range = start.minus(stopAt).magnitude();
            }

            Vector end = start.plus(direction.times(range));

            // Exclude the bender itself
            HashSet<Entity> toExlude = new HashSet<>();
            toExlude.add(entity);

            // Do the actual raytracing
            RayTraceResult result = AvatarUtils.tracePath(world, (float) start.x(), (float) start.y(), (float) start.z(),
                    (float) end.x(), (float) end.y(), (float) end.z(), 1, toExlude, true, false);

            EntityWallSegment segment = null;

            // Process the result. The used segment is chosen randomly. Exit if that's not
            // an entity
            if (result != null && result.typeOfHit.equals(RayTraceResult.Type.ENTITY)) {
                EntityWall wall;
                int width = ability.getProperty(SIZE_MAX, abilityData).intValue();
                width *= abilityData.getDamageMult() * abilityData.getXpModifier();

                int n = AvatarUtils.getRandomNumberInRange(0, width - 1);
                if (result.entityHit instanceof EntityWallSegment) {
                    wall = ((EntityWallSegment) result.entityHit).getWall();
                    segment = wall.getSegment(n);
                } else if (result.entityHit instanceof EntityWall) {
                    wall = (EntityWall) result.entityHit;
                    segment = wall.getSegment(n);
                }
            } else return false;

            // Safety check
            if (segment == null) return false;

            float yaw = (float) Math.toRadians(entity.rotationYaw);
            float pitch = (float) Math.toRadians(entity.rotationPitch);
            Vector lookDir = Vector.toRectangular(yaw, pitch);
            EnumFacing cardinal = entity.getHorizontalFacing();

            // Get which contained block should be used from the segment
            Pair<Block, Integer> toUseBlockData = getBlockToUseFromSegment(segment);
            Block block = toUseBlockData.first();
            int usedNum = toUseBlockData.second();

            // Safety check
            if (block == Blocks.AIR) return false;

            EntityFloatingBlock floating = new EntityFloatingBlock(world);

            floating.setBlock(block);
            floating.setVelocity(lookDir.times(30));
            floating.setBehavior(new FloatingBlockBehavior.Thrown());
            floating.setAbility(ability);
            floating.setHitsLeft(1);
            floating.setTurnSolid(true);
            floating.setDamageSource("avatar_Earth_floatingBlock");
            floating.setBoomerang(false);
            floating.setTier(ability.getCurrentTier(abilityData));
            floating.setDamage((float) (Objects.requireNonNull(Abilities.get("earth_control")).
                    getProperty(DAMAGE, 1).floatValue() * abilityData.getXpModifier() * abilityData.getDamageMult()));
            floating.setOwner(entity);
            floating.setEntityInvulnerable(true);
            floating.setPosition(segment.getPositionVector().add(0, -usedNum, 0).add(entity.getLookVec()));

            if (!world.isRemote)
                world.spawnEntity(floating);

            // Consume some chi, but not too much
            ctx.getData().chi().consumeChi(ability.getChiCost(abilityData) / 5);
            if (entity instanceof EntityPlayer)
                ((EntityPlayer) entity).addExhaustion(ability.getExhaustion(abilityData) / 5);
            abilityData.addBurnout(ability.getBurnOut(abilityData) / 10);
        }

        return false;
    }

    private Pair<Block, Integer> getBlockToUseFromSegment(EntityWallSegment seg) {
        for (int i = seg.getSegmentHeight() - 1; i >= 0; i--) {
            Block temp = seg.getBlock(i).getBlock();
            if (temp != Blocks.AIR) {
                seg.setBlock(i, Blocks.AIR.getDefaultState());
                return new Pair<>(temp, i);
            }
        }
        return new Pair<>(Blocks.AIR, 0);
    }

}
