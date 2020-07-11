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

package com.crowsofwar.avatar.bending.bending.earth.statctrls;

import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.earth.AbilityEarthControl;
import com.crowsofwar.avatar.bending.bending.earth.Earthbending;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.entity.data.FloatingBlockBehavior;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.SoundType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.client.controls.AvatarControl.CONTROL_RIGHT_CLICK_DOWN;
import static com.crowsofwar.avatar.util.data.StatusControl.CrosshairPosition.RIGHT_OF_CROSSHAIR;
import static com.crowsofwar.avatar.util.data.StatusControlController.THROW_BLOCK;

/**
 * @author CrowsOfWar
 */
public class StatCtrlPlaceBlock extends StatusControl {

	public StatCtrlPlaceBlock() {
		super(1, CONTROL_RIGHT_CLICK_DOWN, RIGHT_OF_CROSSHAIR);

		requireRaytrace(-1, true);

	}

	@Override
	public boolean execute(BendingContext ctx) {

		BendingStyle controller = BendingStyles.get(Earthbending.ID);
		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();

		BendingData data = ctx.getData();

		EntityFloatingBlock floating = AvatarEntity.lookupEntity(ctx.getWorld(), EntityFloatingBlock.class,
				fb -> fb.getBehavior() instanceof FloatingBlockBehavior.PlayerControlled
						&& fb.getOwner() == ctx.getBenderEntity());

		if (floating != null && !world.isRemote) {
			Vec3d start = Vector.getEntityPos(entity).toMinecraft().add(0, entity.getEyeHeight(), 0);
			Vec3d end = start.add(entity.getLookVec().scale(5));

			RayTraceResult result = world.rayTraceBlocks(start, end);
			if (result != null && result.sideHit != null) {
				BlockPos pos = result.getBlockPos().offset(result.sideHit);
				Block block = world.getBlockState(pos).getBlock();

				if (block instanceof BlockSnow)
					pos = pos.down();

				floating.setBehavior(new FloatingBlockBehavior.Place(pos));
				Vector force = new Vector(pos).minus(floating.velocity()).normalize();
				floating.addVelocity(force);

				SoundType sound = floating.getBlock().getSoundType();
				if (sound != null) {
					floating.world.playSound(null, floating.getPosition(), sound.getPlaceSound(),
							SoundCategory.PLAYERS, sound.getVolume(), sound.getPitch());
				}

				data.removeStatusControl(THROW_BLOCK);

				data.getAbilityData(new AbilityEarthControl()).addXp(SKILLS_CONFIG.blockPlaced);

				return true;
			}
			return false;
		}

		return true;

	}

}
