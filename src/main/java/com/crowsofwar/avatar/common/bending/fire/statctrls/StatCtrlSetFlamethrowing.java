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

package com.crowsofwar.avatar.common.bending.fire.statctrls;

import com.crowsofwar.avatar.common.bending.fire.AbilityFlamethrower;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.bending.fire.tickhandlers.FlamethrowerUpdateTick;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.StatusControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityLightOrb;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.bending.fire.tickhandlers.FlamethrowerUpdateTick.FLAMETHROWER_MOVEMENT_MODIFIER_ID;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_RIGHT_CLICK_DOWN;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_RIGHT_CLICK_UP;
import static com.crowsofwar.avatar.common.data.StatusControl.CrosshairPosition.RIGHT_OF_CROSSHAIR;
import static com.crowsofwar.avatar.common.data.StatusControlController.STOP_FLAMETHROW;
import static com.crowsofwar.avatar.common.data.TickHandlerController.FLAMETHROWER;

/**
 * @author CrowsOfWar
 */
public class StatCtrlSetFlamethrowing extends StatusControl {

	private final boolean setting;

	public StatCtrlSetFlamethrowing(boolean setting) {
		super(setting ? 4 : 5, setting ? CONTROL_RIGHT_CLICK_DOWN : CONTROL_RIGHT_CLICK_UP,
				RIGHT_OF_CROSSHAIR);
		this.setting = setting;
	}

	@Override
	public boolean execute(BendingContext ctx) {

		BendingData data = ctx.getData();
		EntityLivingBase bender = ctx.getBenderEntity();
		World world = ctx.getWorld();
		EntityLightOrb existing = AvatarEntity.lookupControlledEntity(world, EntityLightOrb.class, bender);

		if (data.hasBendingId(Firebending.ID)) {
			if (setting) {
				if (!(bender instanceof EntityPlayer) && (existing == null || !(existing.getBehavior() instanceof FlamethrowerUpdateTick.FlamethrowerBehaviour))) {
					EntityLightOrb orb = new EntityLightOrb(world);
					orb.setOwner(bender);
					orb.setPosition(bender.getPositionVector().add(0, bender.getEyeHeight() - 0.5, 0));
					orb.setOrbSize(0.25F);
					orb.setAbility(new AbilityFlamethrower());
					orb.setColor(1F, 77 / 255F, 0F, 1F);
					orb.setType(EntityLightOrb.EnumType.COLOR_CUBE);
					orb.setLightRadius(3);
					orb.setElement(new Firebending());
					orb.setBehavior(new FlamethrowerUpdateTick.FlamethrowerBehaviour());
					if (!world.isRemote)
						world.spawnEntity(orb);
				}
				data.addStatusControl(STOP_FLAMETHROW);
				data.addTickHandler(FLAMETHROWER);
			} else {
				if (bender.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(FLAMETHROWER_MOVEMENT_MODIFIER_ID) != null)
					bender.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(FLAMETHROWER_MOVEMENT_MODIFIER_ID);
				data.removeTickHandler(FLAMETHROWER);
			}
		}

		return true;
	}

}
