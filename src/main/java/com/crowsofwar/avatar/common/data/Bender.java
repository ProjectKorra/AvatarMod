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
import com.crowsofwar.avatar.common.bending.Abilities;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.bending.earth.Earthbending;
import com.crowsofwar.avatar.common.bending.water.Waterbending;
import com.crowsofwar.avatar.common.config.ConfigClient;
import com.crowsofwar.avatar.common.config.ConfigMobs;
import com.crowsofwar.avatar.common.config.ConfigSkills;
import com.crowsofwar.avatar.common.config.ConfigStats;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.data.ctx.PlayerBender;
import com.crowsofwar.avatar.common.entity.EntityLightningArc;
import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import com.crowsofwar.avatar.common.event.AbilityUseEvent;
import com.crowsofwar.avatar.common.network.packets.PacketCPowerRating;
import com.crowsofwar.avatar.common.powerrating.PrModifierHandler;
import com.crowsofwar.avatar.common.util.Raytrace;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static com.crowsofwar.avatar.common.config.ConfigChi.CHI_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

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
        return entity == null || (entity instanceof EntityPlayer && !(entity instanceof FakePlayer)) || entity instanceof EntityBender;
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
     * Gets the power rating, but in the range 0.5 to 1.5 for convenience in damage calculations.
     * <ul>
     * <li>-100 power rating gives 0.5; damage would be 1/2 of normal</li>
     * <li>0 power rating gives 1; damage would be the same as normal</li>
     * <li>100 power rating gives 1.5; damage would be 1.5 times as much as usual</li>
     * Powerrating goes from -1000 to 1000, to allow for insane buffs (avatar).
     */
    public double getDamageMult(UUID bendingId) {
        double powerRating = calcPowerRating(bendingId);
        if (powerRating < 0) {
            return 0.005 * powerRating + 1 < 0 ? 1F / 50 : 0.005 * powerRating + 1;
        } else {
            return 0.005 * powerRating + 1;
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
    public void executeAbility(Ability ability, boolean switchPath) {

        Raytrace.Result raytrace = Raytrace.getTargetBlock(getEntity(),
                ability.getRaytrace());
        executeAbility(ability, raytrace, switchPath);

    }

    /**
     * Same as regular {@link #executeAbility(Ability, boolean)}, but allows a provided raytrace, instead
     * of performing another on the fly.
     *
     * @see #executeAbility(Ability, Raytrace.Result, boolean) (Ability)
     */
    public void executeAbility(Ability ability, Raytrace.Result raytrace, boolean switchPath) {

        /**
         * Going to be used so I can autogen default values for each ability.
         * <p>
         *  Basically, I just use the abilities ingame and it fills out their actual
         * 		 * values. Minimal effort, maximum efficiency
         * </p>
         *
         */


        BendingData data = getData();
        EntityLivingBase entity = getEntity();
        AbilityData aD = AbilityData.get(getEntity(), ability.getName());
        int level = aD.getLevel();
        double powerRating = calcPowerRating(ability.getBendingId());
        AbilityData.AbilityTreePath path = aD.getPath();
        AbilityContext abilityCtx = new AbilityContext(data, raytrace, ability,
                entity, powerRating, switchPath);

        if (canUseAbility(ability) && !MinecraftForge.EVENT_BUS.post(new AbilityUseEvent(entity, ability, level + 1, path))) {
            if (data.getMiscData().getCanUseAbilities()) {
                if (consumeChi(ability.getChiCost(abilityCtx)) && aD.getAbilityCooldown() == 0 || entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
                    ability.execute(abilityCtx);
                    if (entity instanceof EntityPlayer)
                        ((EntityPlayer) entity).addExhaustion(ability.getExhaustion(abilityCtx));
                    aD.setAbilityCooldown(ability.getCooldown(abilityCtx));
                    //We set the burnout last as it affects all of the other inhibiting stats
                    aD.setBurnOut(ability.getBurnOut(abilityCtx));

                } else {
                    Objects.requireNonNull(Bender.get(getEntity())).sendMessage("avatar.abilityCooldown");
                }

            } else {
                if (getWorld().isRemote)
                    AvatarChatMessages.MSG_SKATING_BENDING_DISABLED.send(getEntity());

                //	QueuedAbilityExecutionHandler.queueAbilityExecution(entity, data, ability,
                //			raytrace, powerRating, switchPath);
            }
        } else {
            sendMessage("avatar.abilityLocked");
        }

        //	}

        // On client-side, players will send a packet to the server, while other entities will do
        // nothing. Particles will be spawned. Remember to check what side you're on when executing abilities!

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

        List<Ability> abilities = Abilities.all().stream().filter(ability -> AbilityData.get(entity, ability.getName()).getAbilityCooldown() > 0).collect(Collectors.toList());
        for (Ability ability : abilities) {
            AbilityData aD = AbilityData.get(entity, ability.getName());
            aD.decrementCooldown();
            data.save(DataCategory.ABILITY_DATA);
        }

        BendingContext ctx = new BendingContext(data, entity, this, new Raytrace.Result());

        // Update chi

        if (!world.isRemote) {
            Chi chi = data.chi();

            if (CHI_CONFIG.lowChiDebuffs) {
                if (chi.getMaxChi() > 0) {
                    if (chi.getTotalChi() <= chi.getMaxChi() / 15) {
                        entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 5));
                    }
                    if (chi.getTotalChi() <= chi.getMaxChi() / 10) {
                        entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 5));
                    }
                }

            }

            if (chi.getAvailableChi() < CHI_CONFIG.maxAvailableChi)
                chi.changeAvailableChi(CHI_CONFIG.availablePerSecond / 20f);

            if (data.getPerformance().getScore() != 0)
                chi.changeTotalChi(CHI_CONFIG.regenInCombat / 20F);

            if (entity.isPlayerSleeping())
                chi.changeTotalChi(CHI_CONFIG.regenInBed / 20f);

            else if (data.hasBendingId(Waterbending.ID) && entity.isInWater())
                chi.changeTotalChi(CHI_CONFIG.regenInWater / 20F);

            else if (data.hasBendingId(Airbending.ID))
                chi.changeTotalChi(CHI_CONFIG.regenPerSecond / 15);

            else if (data.hasBendingId(Earthbending.ID)) {
                if (STATS_CONFIG.bendableBlocks.contains(world.getBlockState(entity.getPosition()).getBlock()))
                    chi.changeTotalChi(CHI_CONFIG.regenOnEarth / 20F);
            }


        }

        // Tick the TickHandlers

        List<TickHandler> tickHandlers = data.getAllTickHandlers();
        if (tickHandlers != null) {
            for (TickHandler handler : tickHandlers) {
                if (handler != null) {
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
            data.save(DataCategory.TICK_HANDLERS);
        }

        //Config updates
        if (entity.ticksExisted % 400 == 0) {
            ConfigClient.load();
            ConfigStats.load();
            ConfigSkills.load();
            ConfigMobs.load();
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

        data.saveAll();

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

        AvatarMod.network.sendTo(new PacketCPowerRating(powerRatings), (EntityPlayerMP) getEntity());

    }

    public WallJumpManager getWallJumpManager() {
        return wallJumpManager;
    }

}
