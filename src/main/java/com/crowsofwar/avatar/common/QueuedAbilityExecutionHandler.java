package com.crowsofwar.avatar.common;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.util.Raytrace;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Sometimes ability executions are blocked due to cooldown, but should still be fired after the
 * cooldown is over. This class manages these on-hold ability executions, and performs them when
 * ready.
 *
 * @author CrowsOfWar
 */
@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class QueuedAbilityExecutionHandler {

	private static final List<QueuedAbilityExecution> abilityExecutions = new ArrayList<>();

	private QueuedAbilityExecutionHandler() {}

	@SubscribeEvent
	public static void tick(TickEvent.WorldTickEvent e) {
		World world = e.world;
		if (e.phase == TickEvent.Phase.START && !world.isRemote) {
			Iterator<QueuedAbilityExecution> iterator = abilityExecutions.iterator();
			while (iterator.hasNext()) {
				QueuedAbilityExecution par = iterator.next();
				par.ticks--;
				if (par.ticks <= 0 && par.data.getAbilityCooldown() == 0 && par.data.getCanUseAbilities()) {
					par.ability.execute(new AbilityContext(par.data, par.raytrace, par.ability,
							par.entity, par.powerRating));
					iterator.remove();
				}
			}
		}
	}

	public static void queueAbilityExecution(EntityLivingBase entity, BendingData data, Ability
			ability, Raytrace.Result raytrace, double powerRating) {

		abilityExecutions.add(new QueuedAbilityExecution(data.getAbilityCooldown(), entity, data,
				ability, raytrace, powerRating));

	}

	private static class QueuedAbilityExecution {

		private int ticks;
		private final EntityLivingBase entity;
		private final BendingData data;
		private final Ability ability;
		private final Raytrace.Result raytrace;
		private final double powerRating;

		public QueuedAbilityExecution(int ticks, EntityLivingBase entity, BendingData data,
									  Ability ability, Raytrace.Result raytrace, double powerRating) {
			this.ticks = ticks;
			this.entity = entity;
			this.data = data;
			this.ability = ability;
			this.raytrace = raytrace;
			this.powerRating = powerRating;
		}

	}

}
