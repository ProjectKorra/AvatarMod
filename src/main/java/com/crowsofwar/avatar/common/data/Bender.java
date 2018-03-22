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
package com.crowsofwar.avatar.common.data;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.AvatarChatMessages;
import com.crowsofwar.avatar.common.QueuedAbilityExecutionHandler;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.data.ctx.PlayerBender;
import com.crowsofwar.avatar.common.entity.EntityLightningArc;
import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import com.crowsofwar.avatar.common.network.packets.PacketCPowerRating;
import com.crowsofwar.avatar.common.powerrating.PrModifierHandler;
import com.crowsofwar.avatar.common.util.Raytrace;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.crowsofwar.avatar.common.config.ConfigChi.CHI_CONFIG;

/**
 * A wrapper for any mob/player that can bend to provide greater abstraction
 * over useful methods.
 *
 * @author CrowsOfWar
 */
public abstract class Bender {

	protected WallJumpManager wallJumpManager = new WallJumpManager(this);

	/**
	 * Creates an appropriate Bender instance for that entity
	 */
	@Nullable
	public static Bender get(@Nullable EntityLivingBase entity) {
		if (entity == null) {
			return null;
		} else if (entity instanceof EntityBender) {
			return ((EntityBender) entity).getBender();
		} else if (entity instanceof EntityPlayer) {
			return new PlayerBender((EntityPlayer) entity);
		} else {
			throw new IllegalArgumentException("Unsure how to get bender for entity " + entity);
		}
	}

	public static boolean isBenderSupported(EntityLivingBase entity) {
		return entity == null || entity instanceof EntityPlayer || entity instanceof EntityBender;
	}

	/**
	 * For players, returns the username. For mobs, returns the mob's name (e.g.
	 * Chicken).
	 */
	public String getName() {
		return getEntity().getName();
	}

	/**
	 * Gets this bender in entity form
	 */
	public abstract EntityLivingBase getEntity();

	/**
	 * Get the world this entity is currently in
	 */
	public World getWorld() {
		return getEntity().world;
	}

	/**
	 * Get a BenderInfo object, a way to store the Bender's lookup information on disk so it can be
	 * found again later.
	 */
	public abstract BenderInfo getInfo();

	public abstract BendingData getData();

	/**
	 * Returns whether this bender is in creative mode. <strong>This should usually be
	 * avoided;</strong> it ruins abstraction. There are a few scenarios where this would be
	 * necessary however - for example, disabling item drops if the player is on creative mode.
	 * Nonetheless, it is usually not recommended to use this, and instead add a method to Bender
	 * and have PlayerBender override that method.
	 */
	public abstract boolean isCreativeMode();

	public abstract boolean isFlying();

	;

	/**
	 * If any water pouches are in the inventory, checks if there is enough
	 * water. If there is, consumes the total amount of water in those pouches
	 * and returns true.
	 */
	public abstract boolean consumeWaterLevel(int amount);

	/**
	 * Tries to consume the given amount of chi from the Bender. Returns true if successful (ie
	 * there was enough chi); false on failure
	 */
	public boolean consumeChi(float amount) {
		// TODO Account for entity Chi?
		return true;
	}

	/**
	 * Calculates the current power rating based off the current environment.
	 */
	public double calcPowerRating(UUID bendingId) {
		BendingContext ctx = new BendingContext(getData(), getEntity(), this, new Raytrace.Result());

		PowerRatingManager manager = getData().getPowerRatingManager(bendingId);
		if (manager != null) {
			return manager.getRating(ctx);
		}
		return 0;

	}

	/**
	 * Gets the power rating, but in the range 0.25 to 2.0 for convenience in damage calculations.
	 * <ul>
	 * <li>-100 power rating gives 0.25; damage would be 1/4 of normal</li>
	 * <li>0 power rating gives 1; damage would be the same as normal</li>
	 * <li>100 power rating gives 2; damage would be twice as much as usual</li>
	 */
	public double getDamageMult(UUID bendingId) {
		double powerRating = calcPowerRating(bendingId);
		if (powerRating < 0) {
			return 0.0075 * powerRating + 1;
		} else {
			return 0.01 * powerRating + 1;
		}
	}

	/**
	 * Checks whether the Bender can use that given ability.
	 */
	protected boolean canUseAbility(Ability ability) {
		BendingData data = getData();
		return data.hasBendingId(ability.getBendingId()) && !data.getAbilityData(ability)
				.isLocked();
	}

	/**
	 * Executes the given ability in the correct context. This will eventually lead to calling
	 * Ability{@link Ability#execute(AbilityContext)}.
	 * <p>
	 * In certain conditions, other action might be taken; e.g. on client, send a packet to the
	 * server to execute the ability.
	 */
	public void executeAbility(Ability ability) {

		Raytrace.Result raytrace = Raytrace.getTargetBlock(getEntity(),
				ability.getRaytrace());
		executeAbility(ability, raytrace);

	}

	/**
	 * Same as regular {@link #executeAbility(Ability)}, but allows a provided raytrace, instead
	 * of performing another on the fly.
	 *
	 * @see #executeAbility(Ability)
	 */
	public void executeAbility(Ability ability, Raytrace.Result raytrace) {
		if (!getWorld().isRemote) {
			// Server-side : Execute the ability

			BendingData data = getData();
			EntityLivingBase entity = getEntity();
			if (canUseAbility(ability)) {

				double powerRating = calcPowerRating(ability.getBendingId());

				if (data.getMiscData().getAbilityCooldown() == 0) {

					if (data.getMiscData().getCanUseAbilities()) {

						AbilityContext abilityCtx = new AbilityContext(data, raytrace, ability,
								entity, powerRating);

						ability.execute(abilityCtx);
						data.getMiscData().setAbilityCooldown(ability.getCooldown(abilityCtx));

					} else {
						// TODO make bending disabled available for multiple things
						AvatarChatMessages.MSG_SKATING_BENDING_DISABLED.send(getEntity());
					}

				} else {
					QueuedAbilityExecutionHandler.queueAbilityExecution(entity, data, ability,
							raytrace, powerRating);
				}
			} else {
				sendMessage("avatar.abilityLocked");
			}

		}

		// On client-side, players will send a packet to the server, while other entities will do
		// nothing

	}

	/**
	 * Sends an error message to the Bender. This is really only useful for players, which causes
	 * an error message to be displayed above the hotbar.
	 *
	 * @see com.crowsofwar.avatar.common.network.packets.PacketCErrorMessage
	 */
	public void sendMessage(String message) {
	}

	/*
	 * Called when the bender is hit by lightning, and attempts to redirect it. Returns whether the
	 * redirection was successful.
	 */
	public boolean redirectLightning(EntityLightningArc lightningArc) {
		return false;
	}

	/**
	 * Called every tick; updates things like chi.
	 */
	public void onUpdate() {

		BendingData data = getData();
		World world = getWorld();
		EntityLivingBase entity = getEntity();

		data.getMiscData().decrementCooldown();

		BendingContext ctx = new BendingContext(data, entity, this, new Raytrace.Result());

		// Update chi

		if (!world.isRemote) {
			Chi chi = data.chi();

			if (entity.isPlayerSleeping()) {
				chi.changeTotalChi(CHI_CONFIG.regenInBed / 20f);
			} else {
				chi.changeTotalChi(CHI_CONFIG.regenPerSecond / 20f);
			}

			if (chi.getAvailableChi() < CHI_CONFIG.maxAvailableChi) {
				chi.changeAvailableChi(CHI_CONFIG.availablePerSecond / 20f);
			}

		}

		// Tick the TickHandlers

		List<TickHandler> tickHandlers = data.getAllTickHandlers();
		if (tickHandlers != null) {
			for (TickHandler handler : tickHandlers) {
				if (handler.tick(ctx)) {
					// Can use this since the list is a COPY of the
					// underlying list
					data.removeTickHandler(handler);
				} else {
					int newDuration = data.getTickHandlerDuration(handler) + 1;
					data.setTickHandlerDuration(handler, newDuration);
				}
			}
		}

		// Update bending managers

		List<PowerRatingManager> managers = data.getPowerRatingManagers();
		for (PowerRatingManager manager : managers) {
			manager.tickModifiers(ctx);
		}

		// Update power rating modifiers
		if (!world.isRemote) {
			PrModifierHandler.addPowerRatingModifiers(this);
		}

		data.getPerformance().update();

		if (entity instanceof EntityPlayer && !world.isRemote && entity.ticksExisted % 40 == 0) {
			syncPowerRating();
		}

	}

	public void onDeath() {

		BendingContext ctx = new BendingContext(getData(), getEntity(), new Raytrace.Result());

		BendingData data = getData();

		for (UUID bendingId : data.getAllBendingIds()) {
			PowerRatingManager manager = data.getPowerRatingManager(bendingId);
			//noinspection ConstantConditions
			manager.clearModifiers(ctx);
		}

		Chi chi = data.chi();
		chi.setTotalChi(chi.getMaxChi());
		chi.setAvailableChi(CHI_CONFIG.maxAvailableChi);

	}

	private void syncPowerRating() {

		BendingContext ctx = new BendingContext(getData(), getEntity(), new Raytrace.Result());
		Map<UUID, Double> powerRatings = new HashMap<>();
		List<PowerRatingManager> managers = getData().getPowerRatingManagers();

		for (PowerRatingManager manager : managers) {
			powerRatings.put(manager.getBendingType(), manager.getRating(ctx));
		}

		AvatarMod.proxy.network.sendTo(new PacketCPowerRating(powerRatings), (EntityPlayerMP) getEntity());

	}

	public WallJumpManager getWallJumpManager() {
		return wallJumpManager;
	}

}
