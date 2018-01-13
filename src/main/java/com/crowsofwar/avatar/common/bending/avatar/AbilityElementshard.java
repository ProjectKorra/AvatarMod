package com.crowsofwar.avatar.common.bending.avatar;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.bending.fire.AiFireball;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityElementshard;
import com.crowsofwar.avatar.common.entity.EntityFireball;
import com.crowsofwar.avatar.common.entity.data.ElementshardBehavior;
import com.crowsofwar.avatar.common.entity.data.FireballBehavior;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import java.util.UUID;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.gorecore.util.Vector.getEyePos;
import static com.crowsofwar.gorecore.util.Vector.getLookRectangular;

public class AbilityElementshard extends Ability {

	public AbilityElementshard() {
		super(Avatarbending.ID, "element_shard");
		requireRaytrace(2.5, false);
	}
	@Override
	public void execute(AbilityContext ctx) {

		EntityLivingBase entity = ctx.getBenderEntity();
		Bender bender = ctx.getBender();
		World world = ctx.getWorld();
		BendingData data = ctx.getData();

		float chi = 4F;
		float damage = 0.5F;
		int shardsLeft = 5;

		if (ctx.getLevel() == 1){
			damage = 1F;
			shardsLeft = 8;
		}
		if (ctx.getLevel() == 2){
			shardsLeft = 10;
			//create an explosion; code is in fireball but 2 lazy to do it right now
		}
		if (data.hasStatusControl(StatusControl.THROW_ELEMENTSHARD)) return;

		if (bender.consumeChi(STATS_CONFIG.chiFireball)) {

			Vector target;
			if (ctx.isLookingAtBlock()) {
				target = ctx.getLookPos();
			} else {
				Vector playerPos = getEyePos(entity);
				target = playerPos.plus(getLookRectangular(entity).times(2.5));
			}

			damage *= ctx.getPowerRatingDamageMod();
			for (int i = 0; i<shardsLeft; i++ ) {

				EntityElementshard elementshard = new EntityElementshard(world);
				elementshard.setPosition(target);
				elementshard.setOwner(entity);
				elementshard.setBehavior(new ElementshardBehavior.PlayerControlled());
				elementshard.setDamage(damage);
				elementshard.setPowerRating(bender.calcPowerRating(Firebending.ID));
				if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) elementshard.setSize(20);
				world.spawnEntity(elementshard);

			}
			data.addStatusControl(StatusControl.THROW_ELEMENTSHARD);
		}

	}

	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiElementshard(this, entity, bender);
	}

}
