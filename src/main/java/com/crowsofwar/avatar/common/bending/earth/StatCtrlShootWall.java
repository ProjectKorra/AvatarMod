package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.bending.Abilities;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.entity.EntityWall;
import com.crowsofwar.avatar.common.entity.EntityWallSegment;
import com.crowsofwar.avatar.common.entity.data.FloatingBlockBehavior;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.avatar.common.util.Raytrace.Result;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * @author Aang23
 */
public class StatCtrlShootWall extends StatusControl {

	public StatCtrlShootWall() {
		super(2, AvatarControl.CONTROL_LEFT_CLICK_DOWN, CrosshairPosition.LEFT_OF_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {
		// TODO clean that messy code and add comments

		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();

		Vector start = new Vector(entity.getPositionVector());
		Vector direction = new Vector(entity.getLookVec());

		double range = 100;
		Result raytrace = Raytrace.raytrace(world, start, direction, range, true);
		if (raytrace.hitSomething()) {
			Vector stopAt = raytrace.getPosPrecise();
			range = start.minus(stopAt).magnitude();
		}

		List<Entity> hit = new ArrayList<>();

		Vector end = start.plus(direction.times(range));

		if (entity.getDistanceSq(new BlockPos(end.x(), end.y(), end.z())) > 10) {
			return false;
		}

		HashSet<Entity> toExlude = new HashSet<Entity>();
		toExlude.add(entity);
		RayTraceResult result = AvatarUtils.tracePath(world, (float) start.x(), (float) start.y(), (float) start.z(),
				(float) end.x(), (float) end.y(), (float) end.z(), 1, toExlude, true, false);

		EntityWallSegment segment = null;

		System.out.println(result.entityHit.toString());

		if (result.typeOfHit.equals(RayTraceResult.Type.ENTITY)) {
			EntityWall wall;
			if (result.entityHit instanceof EntityWallSegment) {
				wall = ((EntityWallSegment) result.entityHit).getWall();
				int n = new Random().nextInt(7);
				System.out.println(n);
				segment = wall.getSegment(n);
			} else if (result.entityHit instanceof EntityWall) {
				wall = (EntityWall) result.entityHit;
				int n = new Random().nextInt(7);
				System.out.println(n);
				segment = wall.getSegment(n);
			}
		} else {
			return false;
		}

		if (segment == null) {
			return false;
		}

		EntityFloatingBlock floating = new EntityFloatingBlock(world);

		float yaw = (float) Math.toRadians(entity.rotationYaw);
		float pitch = (float) Math.toRadians(entity.rotationPitch);

		Vector lookDir = Vector.toRectangular(yaw, pitch);

		Block toUse = Blocks.AIR;

		int usedNum = 0;

		// TODO put in a method
		blockchoosing: for (int i = segment.SEGMENT_HEIGHT - 1; i >= 0; i--) {
			Block temp = segment.getBlock(i).getBlock();
			if (temp != Blocks.AIR) {
				toUse = temp;
				usedNum = i;
				segment.setBlock(i, Blocks.AIR.getDefaultState());
				break blockchoosing;
			}
		}

		if (toUse == Blocks.AIR) {
			return false;
		}

		floating.setBlock(toUse);
		floating.addVelocity(floating.velocity().times(-1));
		floating.addVelocity(lookDir.times(15));
		floating.setBehavior(new FloatingBlockBehavior.Thrown());
		floating.setAbility(Abilities.get("wall"));
		floating.setOwner(entity);
		floating.setEntityInvulnerable(true);

		floating.setPosition(segment.getPositionVector().add(0, usedNum - 1, 0));

		world.spawnEntity(floating);

		return false;
	}

	private boolean containsBlock(EntityWallSegment seg) {
		boolean res = false;
		for (int i = seg.SEGMENT_HEIGHT - 1; i >= 0; i--) {
			System.out.println(seg.getBlock(i).toString());
			if (seg.getBlock(i) != Blocks.AIR) {
				res = true;
			}
		}
		return res;
	}

}
