package com.crowsofwar.avatar.util.damageutils;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityShield;
import com.crowsofwar.avatar.entity.IShieldEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.util.DamageSource;

public class DamageUtils {

	//Handles dragon damage; I recommend using this to avoid weird shenanigans with ender dragons. EntityOffensive has this built in.
	public static void attackEntity(EntityLivingBase attacker, Entity hit, DamageSource source, float damage, int performance, Ability ability, float xp) {
		if (hit != null && attacker != null && ability != null) {
			if (hit instanceof AvatarEntity) {
				if (hit instanceof IShieldEntity)
					((IShieldEntity) hit).setHealth(((IShieldEntity) hit).getHealth() - damage);
				else if (hit instanceof EntityShield)
					((EntityShield) hit).setHealth(((EntityShield) hit).getHealth() - damage);
			} else {
				boolean ds = hit.attackEntityFrom(source, damage);
				AbilityData data = AbilityData.get(attacker, ability.getName());
				if (data != null) {
					if (!ds && hit instanceof EntityDragon) {
						((EntityDragon) hit).attackEntityFromPart(((EntityDragon) hit).dragonPartBody, source,
								damage);
						BattlePerformanceScore.addScore(attacker, performance);
						data.addXp(xp);
					} else if (hit instanceof EntityLivingBase && ds) {
						BattlePerformanceScore.addScore(attacker, performance);
						data.addXp(xp);
					}
				}
			}
		}
	}

}
