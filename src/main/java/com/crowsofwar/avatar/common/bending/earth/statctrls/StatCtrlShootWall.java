package com.crowsofwar.avatar.common.bending.earth.statctrls;

import akka.japi.Pair;
import com.crowsofwar.avatar.common.bending.earth.AbilityWall;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.StatusControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.entity.EntityWall;
import com.crowsofwar.avatar.common.entity.EntityWallSegment;
import com.crowsofwar.avatar.common.entity.data.FloatingBlockBehavior;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.avatar.common.util.Raytrace.Result;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Random;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

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

		double range = 5;
		Result raytrace = Raytrace.raytrace(world, start, direction, range, true);
		if (raytrace.hitSomething()) {
			Vector stopAt = raytrace.getPosPrecise();
			range = start.minus(stopAt).magnitude();
		}

		Vector end = start.plus(direction.times(range));

		// Exclude the bender itself
		HashSet<Entity> toExlude = new HashSet<Entity>();
		toExlude.add(entity);

		// Do the actual raytracing
		RayTraceResult result = AvatarUtils.tracePath(world, (float) start.x(), (float) start.y(), (float) start.z(),
				(float) end.x(), (float) end.y(), (float) end.z(), 1, toExlude, true, false);

		EntityWallSegment segment = null;

		// Process the result. The used segment is choosed randomly. Exit if that's not
		// an entity
		if (result != null && result.typeOfHit.equals(RayTraceResult.Type.ENTITY)) {
			EntityWall wall;
			int n = new Random().nextInt(7);
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
		floating.addVelocity(floating.velocity().times(-1));
		floating.addVelocity(lookDir.times(15));
		floating.setBehavior(new FloatingBlockBehavior.Thrown());
		floating.setAbility(new AbilityWall());
		floating.setOwner(entity);
		floating.setEntityInvulnerable(true);
		floating.setPosition(new Vector(segment.getPosition().offset(cardinal, 2).add(0, usedNum - 1, 0)));

		world.spawnEntity(floating);

		// Consume some chi, but not too much
		ctx.getData().chi().consumeChi(STATS_CONFIG.chiPickUpBlock / 2);

		return false;
	}

	private Pair<Block, Integer> getBlockToUseFromSegment(EntityWallSegment seg) {
		for (int i = EntityWallSegment.SEGMENT_HEIGHT - 1; i >= 0; i--) {
			Block temp = seg.getBlock(i).getBlock();
			if (temp != Blocks.AIR) {
				seg.setBlock(i, Blocks.AIR.getDefaultState());
				return new Pair<Block, Integer>(temp, i);
			}
		}
		return new Pair<Block, Integer>(Blocks.AIR, 0);
	}

}
