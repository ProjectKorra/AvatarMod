package com.crowsofwar.avatar.util.event;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.data.AbilityData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * @author Aang23
 */
@Cancelable
public class AbilityUseEvent extends BendingEvent {
	private Ability ability;
	//Levels returns the intuitive level, not the hardcoded level. Ex: Level III is level 3, not level 2. Creative is level 0.
	private int level;
	//Is MAIN unless level 4.
	private AbilityData.AbilityTreePath path;

	public AbilityUseEvent(EntityLivingBase entity, Ability ability, int level, AbilityData.AbilityTreePath path) {
		super(entity);
		this.ability = ability;
		this.level = level;
		this.path = path;
	}

	public Ability getAbility() {
		return ability;
	}

	public int getLevel() {
		return level;
	}

	public AbilityData.AbilityTreePath getPath() {
		return path;
	}

}
