package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.avatar.common.util.Raytrace.Info;
import com.crowsofwar.gorecore.util.Vector;
import com.crowsofwar.gorecore.util.VectorI;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityWaterArc extends BendingAbility<WaterbendingState> {
	
	private final Raytrace.Info raytrace;
	
	/**
	 * @param controller
	 */
	public AbilityWaterArc(BendingController<WaterbendingState> controller) {
		super(controller);
		this.raytrace = new Raytrace.Info(-1, false);
	}
	
	@Override
	public boolean requiresUpdateTick() {
		return true;
	}
	
	@Override
	public void update(AvatarPlayerData data) {
		EntityPlayer player = data.getPlayerEntity();
		World world = player.worldObj;
		WaterbendingState bendingState = data.getBendingState(controller);
		
		System.out.println("--update");
		if (bendingState.isBendingWater()) {
			
			EntityWaterArc water = bendingState.getWaterArc();
			if (water != null) {
				Vector look = Vector.fromYawPitch(Math.toRadians(player.rotationYaw),
						Math.toRadians(player.rotationPitch));
				Vector lookPos = Vector.getEyePos(player).plus(look.times(3));
				Vector motion = lookPos.minus(new Vector(water));
				motion.normalize();
				motion.mul(.15);
				water.moveEntity(motion.x(), motion.y(), motion.z());
				water.setOwner(player);
				
				if (water.worldObj.isRemote && water.canPlaySplash()) {
					if (motion.sqrMagnitude() >= 0.004) water.playSplash();
				}
			} else {
				if (!world.isRemote) bendingState.setWaterArc(null);
			}
			
		}
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		WaterbendingState bendingState = ctx.getData().getBendingState(controller);
		World world = ctx.getWorld();
		EntityPlayer player = ctx.getPlayerEntity();
		
		boolean needsSync = false;
		
		if (bendingState.isBendingWater()) {
			EntityWaterArc water = bendingState.getWaterArc();
			water.setGravityEnabled(true);
			bendingState.releaseWater();
			needsSync = true;
		}
		
		VectorI targetPos = ctx.getClientLookBlock();
		if (targetPos != null) {
			Block lookAt = world.getBlockState(targetPos.toBlockPos()).getBlock();
			if (lookAt == Blocks.WATER || lookAt == Blocks.FLOWING_WATER) {
				
				EntityWaterArc water = new EntityWaterArc(world);
				System.out.println("Made new water arc");
				water.setOwner(player);
				water.setPosition(targetPos.x() + 0.5, targetPos.y() - 0.5, targetPos.z() + 0.5);
				water.setGravityEnabled(false);
				bendingState.setWaterArc(water);
				
				world.spawnEntityInWorld(water);
				
				needsSync = true;
				
			}
		}
		
		if (needsSync) ctx.getData().sendBendingState(bendingState);
	}
	
	@Override
	public int getIconIndex() {
		return 4;
	}
	
	@Override
	public Info getRaytrace() {
		return raytrace;
	}
	
}
