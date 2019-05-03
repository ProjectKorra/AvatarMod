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

package com.crowsofwar.avatar.common.data.ctx;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import com.crowsofwar.gorecore.util.VectorI;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 * Information when something is executed. Only is used server-side.
 *
 * @author CrowsOfWar
 */
public class BendingContext {

	private final BendingData data;
	private final Bender bender;

	/**
	 * The results of a raytrace which was performed client-side.
	 */
	@Nullable
	private Raytrace.Result raytrace;

	/**
	 * Create context for execution.
	 *
	 * @param data     Player data instance.
	 * @param raytrace Result of the raytrace, from client
	 */
	public BendingContext(BendingData data, EntityLivingBase entity, Raytrace.Result raytrace) {
		this.data = data;
		this.bender = Bender.get(entity);
		this.raytrace = raytrace;
		verifyClientRaytrace();
	}

	public BendingContext(BendingData data, EntityLivingBase entity, Bender bender,
						  Raytrace.Result raytrace) {

		this.data = data;
		this.bender = bender;
		this.raytrace = raytrace;
		verifyClientRaytrace();

	}

	public BendingData getData() {
		return data;
	}

	public Bender getBender() {
		return bender;
	}

	public EntityLivingBase getBenderEntity() {
		return bender.getEntity();
	}

	public World getWorld() {
		return bender == null ? null : bender.getWorld();
	}

	@Nullable
	public VectorI getLookPosI() {
		if (raytrace == null) {
			return null;
		}
		return raytrace.getPos();
	}

	@Nullable
	public EnumFacing getLookSide() {
		if (raytrace == null) {
			return null;
		}
		return raytrace.getSide();
	}

	/**
	 * Returns whether the player is looking at a block right now
	 */
	public boolean isLookingAtBlock() {
		return raytrace != null && raytrace.hitSomething();
	}

	@Nullable
	public Vector getLookPos() {
		if (raytrace == null) {
			return null;
		}
		return raytrace.getPosPrecise();
	}

	/**
	 * For certain circumstances, the client performs a raytrace and then sends the result to the server, which is
	 * usable here (ex. {@link #isLookingAtBlock()}). Raytrace isn't performed on server since the server and client can
	 * have minor discrepancies, so the server might think the player's rotation is 20 degrees off from what the client
	 * thinks, resulting in glitchy raytracing.
	 * <p>
	 * Performed once to ensure that the client's targeted block is reasonable, to avoid hacking.
	 */
	private void verifyClientRaytrace() {

		if (raytrace != null) {

			// Simply verify if the client's look-block is reasonable

			Vector benderPos = Vector.getEntityPos(getBenderEntity());
			Vector blockPos = getLookPos();
			if (blockPos != null) {

				double dist = benderPos.dist(blockPos);

				if (dist >= 5) {
					AvatarLog.warnHacking(bender.getName(), "Sent suspicious raytrace block, ignoring");
					raytrace = null;
				}

			}

		}

	}

	/**
	 * Consumes the given amount of water either from direct water source, from
	 * a water pouch, or several other sources.
	 * <p>
	 * First looks to see if looking at water block - any values >= 3 will also
	 * consume the water block. Then, tries to see if there is a water pouch
	 * with sufficient amount of water.
	 * <p>
	 * <b>NOTE:</b> If this is not working, ensure that the Ability constructor is calling
	 * requireRaytrace, because otherwise no raytrace will be performed, and then this won't be able
	 * to detect if the player is looking at water.
	 */
	public boolean consumeWater(int amount) {

		World world = bender.getWorld();

		if (world.isRainingAt(bender.getEntity().getPosition())) {
			return true;
		}

		EntityLivingBase entity = bender.getEntity();

		if (entity.getHeldItemMainhand().getItem() == Items.WATER_BUCKET) {
			entity.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.BUCKET, 1));
			return true;
		}
		if (entity.getHeldItemOffhand().getItem() == Items.WATER_BUCKET) {
			entity.setHeldItem(EnumHand.OFF_HAND, new ItemStack(Items.BUCKET, 1));
			return true;
		}


		VectorI targetPos = getLookPosI();
		if (targetPos != null) {
			Block lookAt = world.getBlockState(targetPos.toBlockPos()).getBlock();
			//Will need to adjust for passives
			if (lookAt == Blocks.WATER || lookAt == Blocks.FLOWING_WATER) {

				if (amount >= 3) {
					world.setBlockToAir(targetPos.toBlockPos());
				}
				return true;

			}

			if (lookAt == Blocks.CAULDRON) {
				IBlockState ibs = world.getBlockState(targetPos.toBlockPos());
				int waterLevel = ibs.getValue(BlockCauldron.LEVEL);
				if (waterLevel > 0) {
					world.setBlockState(targetPos.toBlockPos(),
							ibs.withProperty(BlockCauldron.LEVEL, waterLevel - 1));
					return true;
				}
			}

		}

		return bender.consumeWaterLevel(amount);

	}



	public boolean consumeSnow(int amount) {

		World world = bender.getWorld();

		if (world.isRainingAt(bender.getEntity().getPosition())) {
			return true;
		}

		VectorI targetPos = getLookPosI();
		if (targetPos != null) {
			Block lookAt = world.getBlockState(targetPos.toBlockPos()).getBlock();
			//Will need to adjust for passives
			if (STATS_CONFIG.waterBendableBlocks.contains(lookAt)) {
				if (amount >= 3) {
					world.setBlockToAir(targetPos.toBlockPos());
				}
				return true;

			}


		}

		return bender.consumeWaterLevel(amount);

	}



	public boolean consumePlants(int amount) {

		World world = bender.getWorld();

		if (world.isRainingAt(bender.getEntity().getPosition())) {
			return true;
		}

		VectorI targetPos = getLookPosI();
		if (targetPos != null) {
			Block lookAt = world.getBlockState(targetPos.toBlockPos()).getBlock();
			//Will need to adjust for passives
			if (STATS_CONFIG.plantBendableBlocks.contains(lookAt)) {

				if (amount >= 3) {
					world.setBlockToAir(targetPos.toBlockPos());
				}
				return true;

			}


		}

		return bender.consumeWaterLevel(amount);

	}
}