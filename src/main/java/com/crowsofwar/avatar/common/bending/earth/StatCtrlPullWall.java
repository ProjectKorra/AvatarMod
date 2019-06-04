package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.bending.Abilities;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.entity.EntityWall;
import com.crowsofwar.avatar.common.entity.EntityWallSegment;
import com.crowsofwar.avatar.common.entity.data.FloatingBlockBehavior;
import com.crowsofwar.avatar.common.entity.data.WallBehavior;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.avatar.common.util.Raytrace.Result;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;

import akka.japi.Pair;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 * @author Aang23
 */
public class StatCtrlPullWall extends StatusControl {

	public StatCtrlPullWall() {
		super(12, AvatarControl.CONTROL_LEFT_CLICK_DOWN, CrosshairPosition.LEFT_OF_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {
		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();

		// TODO: When upgrade to a5.0 , call setOwner on the wall itself , then lookup
		// based on wall

		// Wall has no owner so we go for segments
		EntityWallSegment wallSegment = AvatarEntity.lookupOwnedEntity(world, EntityWallSegment.class, entity);

		if (wallSegment.getBehavior().getClass() == WallBehavior.Waiting.class) {

			List<EntityWallSegment> segments = world.getEntities(EntityWallSegment.class,
					segment -> segment.getOwner() == entity);

			for (EntityWallSegment segment : segments) {
				if (segment.getBehavior().getClass().equals(WallBehavior.Waiting.class))
					segment.setBehavior(new WallBehavior.Pull());
			}

			return true;
		} else {
			return false;
		}
	}
}
