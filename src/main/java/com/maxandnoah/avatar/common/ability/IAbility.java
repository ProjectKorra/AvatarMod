package com.maxandnoah.avatar.common.ability;

import com.maxandnoah.avatar.common.data.AvatarPlayerData;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Base for an ability. Abilities provide a convenient
 * way for BendingControllers to share similar functionality.
 * Look at component-based design for more information.
 * <br /><br />
 * Aside from that, the mod will eventually work in that
 * players activate specific abilities for specific bending
 * moves.(e.g. lightning strike, or rock-machine-gun)
 * 
 */
public interface IAbility {
	
	void onAbilityActive(EntityPlayer player, AvatarPlayerData data);
	
}
