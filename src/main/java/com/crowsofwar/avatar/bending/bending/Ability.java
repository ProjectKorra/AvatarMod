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

package com.crowsofwar.avatar.bending.bending;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.bending.bending.combustion.Combustionbending;
import com.crowsofwar.avatar.bending.bending.fire.Firebending;
import com.crowsofwar.avatar.bending.bending.ice.Icebending;
import com.crowsofwar.avatar.bending.bending.lightning.Lightningbending;
import com.crowsofwar.avatar.bending.bending.sand.Sandbending;
import com.crowsofwar.avatar.config.AbilityProperties;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.avatar.entity.mob.EntityBender;
import com.crowsofwar.avatar.entity.mob.EntityHumanBender;
import com.crowsofwar.avatar.item.scroll.ItemScroll;
import com.crowsofwar.avatar.item.scroll.Scrolls;
import com.crowsofwar.avatar.network.packets.PacketCSyncAbilityProperties;
import com.crowsofwar.avatar.util.Raytrace;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Encapsulates all logic required for a bending ability. There is 1 instance of
 * a bending ability for each ability present - similar to BendingController.
 *
 * @author CrowsOfWar
 */
@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public abstract class Ability {

    //NOTE: No client side particles can be spawned in an ability class due to abilities being executed server-side.

    ///Property time! How many different properties can I add??????
    public static final String
            CHI_COST = "chiCost",
            COOLDOWN = "cooldown",
            EXHAUSTION = "exhaustion",
            BURNOUT = "burnOut",
            BURNOUT_REGEN = "burnOutRecoverTick",
            XP_HIT = "xpOnHit",
            XP_USE = "xpOnUse",
            TIER = "tier",
            PARENT_TIER = "parentTier",
            PERFORMANCE = "performanceAmount",
            FIRE_TIME = "fireTime",
            LIFETIME = "lifeTime",
            SIZE = "size",
            SPEED = "speed",
            KNOCKBACK = "knockback",
            CHI_HIT = "chiOnHit",
            DAMAGE = "damage",
            DURATION = "duration",
            CHARGE_TIME = "chargeTime",
            //Airbending stuff
            PUSH_REDSTONE = "pushRedstone",
            PUSH_IRONDOOR = "pushIronDoor",
            PUSH_STONE = "pushStoneButton",
            PUSH_IRON_TRAPDOOR = "pushIronTrapDoor";

    private final UUID type;
    private final String name;
    /**
     * Used in initialisation.
     */
    private final Set<String> propertyKeys = new HashSet<>();
    /**
     * Used in initialisation.
     */
    private final Set<String> booleanPropertyKeys = new HashSet<>();
    /**
     * This ability's associated AbilityProperties object.
     */
    public AbilityProperties properties;
    /**
     * A reference to the global spell properties for this ability, so they are only loaded once.
     */
    private AbilityProperties globalProperties;
    private Raytrace.Info raytrace;


    public Ability(UUID bendingType, String name) {
        this.type = bendingType;
        this.name = name;
        this.raytrace = new Raytrace.Info();

    }

    /**
     * Called from the event handler when a player logs in.
     */
    public static void syncProperties(EntityPlayer player) {
        if (player instanceof EntityPlayerMP) {
            // On the server side, send a packet to the player to synchronise their spell properties
            List<Ability> abilities = Abilities.all();
            AvatarMod.network.sendToAll(new PacketCSyncAbilityProperties(abilities.stream().map(a -> a.properties).toArray(AbilityProperties[]::new)));

        } else {
            // On the client side, wipe the spell properties so the new ones can be set
            // TESTME: Can we guarantee this happens before the packet arrives?
            clearProperties();
        }
    }

    private static void clearProperties() {
        for (Ability ability : Abilities.all()) {
            ability.properties = null;
        }
    }

    //Event handler stuff for properties
    @SubscribeEvent
    public static void onWorldLoadEvent(WorldEvent.Load event) {
        if (!event.getWorld().isRemote) {
            if (event.getWorld().provider.getDimension() != 0) return; // Only do it once per save file
            clearProperties();
            AbilityProperties.loadWorldSpecificAbilityProperties(event.getWorld());
            for (Ability ability : Abilities.all()) {
                if (!ability.arePropertiesInitialised()) ability.setProperties(ability.globalProperties);
            }
        }
    }

    // ============================================ Event handlers ==============================================

    // Not ideal but it solves the reloading of ability properties without breaking encapsulation

    @SubscribeEvent
    public static void onClientDisconnectEvent(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        // Why does the world UNLOAD event happen during world LOADING? How does that even work?!
        clearProperties();
        for (Ability ability : Abilities.all()) {
            // If someone wants to access them from the menu, they'll get the global ones (not sure why you'd want to)
            // No need to sync here since the server is about to shut down anyway
            ability.setProperties(ability.globalProperties);
        }
    }

    public void init() {
        //Base properties belonging to all abilities
        addProperties(TIER, CHI_COST, BURNOUT, BURNOUT_REGEN, COOLDOWN, EXHAUSTION);

        if (isProjectile() || isOffensive()) {
            addProperties(LIFETIME, SPEED, CHI_HIT, PERFORMANCE, XP_HIT, SIZE);
            if (isOffensive())
                addProperties(DAMAGE, KNOCKBACK);
        }
        if (isBuff())
            addProperties(DURATION, XP_USE);
        if (isUtility())
            addProperties(XP_USE);
        if (isChargeable())
            addProperties(CHARGE_TIME);

        if (getBendingId() == Firebending.ID && isOffensive())
            addProperties(FIRE_TIME);

        //Brute force due to initialisation order
        if (getBendingId() == Lightningbending.ID ||
                getBendingId() == Sandbending.ID || getBendingId() == Combustionbending.ID
                || getBendingId() == Icebending.ID)
            addProperties(PARENT_TIER);
    }

    /**
     * All configurable stats. Should be implemented here so that
     * it automagically gets the correct stats from the json files based on the ability name
     * with 0 need for an override (except in the case of status control abilities).
     */

    /**
     * Returns the base value specified in JSON for the given identifier. This may be used from within the spell
     * class, or from elsewhere (entities, items, etc.) via the spell's instance.
     *
     * @param identifier The JSON identifier for the required property. This <b>must</b> have been defined using
     *                   {@link Ability#addProperties(String...)} or an exception will be thrown.
     * @return The base value of the property, as a {@code Number} object. Internally this is handled as a float, but
     * it is passed through as a {@code Number} to avoid casting. <i>Be careful with rounding when extracting integer
     * values! The JSON parser cannot guarantee that the property file has an integer value.</i>
     * @throws IllegalArgumentException if no property was defined with the given identifier.
     */
    public final Number getProperty(String identifier, int abilityLevel) {
        if (properties == null)
            AvatarLog.warn(AvatarLog.WarningType.CONFIGURATION, "Properties file for " + getName() + " wasn't successfully loaded, things will start breaking!");
        return properties == null ? 1 : properties.getBaseValue(identifier, abilityLevel, AbilityData.AbilityTreePath.MAIN);
    }

    public final Number getProperty(String identifier, int abilityLevel, AbilityData.AbilityTreePath path) {
        if (properties == null)
            AvatarLog.warn(AvatarLog.WarningType.CONFIGURATION, "Properties file for " + getName() + " wasn't successfully loaded, things will start breaking!");
        return properties == null ? 1 : properties.getBaseValue(identifier, abilityLevel, path);
    }

    public final Number getProperty(String identifier) {
        if (properties == null)
            AvatarLog.warn(AvatarLog.WarningType.CONFIGURATION, "Properties file for " + getName() + " wasn't successfully loaded, things will start breaking!");
        return properties == null ? 1 : properties.getBaseValue(identifier, 0, AbilityData.AbilityTreePath.MAIN);
    }

    public final Number getProperty(String identifier, AbilityContext ctx) {
        if (properties == null)
            AvatarLog.warn(AvatarLog.WarningType.CONFIGURATION, "Properties file for " + getName() + " wasn't successfully loaded, things will start breaking!");
        return properties == null ? 1 : properties.getBaseValue(identifier, ctx.getLevel(), ctx.getDynamicPath());
    }

    public final Number getProperty(String identifier, AbilityData data) {
        if (properties == null)
            AvatarLog.warn(AvatarLog.WarningType.CONFIGURATION, "Properties file for " + getName() + " wasn't successfully loaded, things will start breaking!");
        return properties == null ? 1 : properties.getBaseValue(identifier, data.getLevel(), data.getDynamicPath());
    }

    /**
     * Returns the base value specified in JSON for the given identifier. This may be used from within the spell
     * class, or from elsewhere (entities, items, etc.) via the spell's instance.
     *
     * @param identifier The JSON identifier for the required property. This <b>must</b> have been defined using
     *                   {@link Ability#addProperties(String...)} or an exception will be thrown.
     * @return The base value of the property, as a {@code Number} object. Internally this is handled as a float, but
     * it is passed through as a {@code Number} to avoid casting. <i>Be careful with rounding when extracting integer
     * values! The JSON parser cannot guarantee that the property file has an integer value.</i>
     * @throws IllegalArgumentException if no property was defined with the given identifier.
     */
    public final boolean getBooleanProperty(String identifier, int abilityLevel) {
        if (properties == null)
            AvatarLog.warn(AvatarLog.WarningType.CONFIGURATION, "Properties file for " + getName() + " wasn't successfully loaded, things will start breaking!");
        return properties != null && properties.getBaseBooleanValue(identifier, abilityLevel, AbilityData.AbilityTreePath.MAIN);
    }

    public final boolean getBooleanProperty(String identifier, int abilityLevel, AbilityData.AbilityTreePath path) {
        if (properties == null)
            AvatarLog.warn(AvatarLog.WarningType.CONFIGURATION, "Properties file for " + getName() + " wasn't successfully loaded, things will start breaking!");
        return properties != null && properties.getBaseBooleanValue(identifier, abilityLevel, path);
    }

    public final boolean getBooleanProperty(String identifier) {
        if (properties == null)
            AvatarLog.warn(AvatarLog.WarningType.CONFIGURATION, "Properties file for " + getName() + " wasn't successfully loaded, things will start breaking!");
        return properties != null && properties.getBaseBooleanValue(identifier, 0, AbilityData.AbilityTreePath.MAIN);
    }

    public final boolean getBooleanProperty(String identifier, AbilityContext ctx) {
        if (properties == null)
            AvatarLog.warn(AvatarLog.WarningType.CONFIGURATION, "Properties file for " + getName() + " wasn't successfully loaded, things will start breaking!");
        return properties != null && properties.getBaseBooleanValue(identifier, ctx.getLevel(), ctx.getDynamicPath());
    }

    public final boolean getBooleanProperty(String identifier, AbilityData data) {
        if (properties == null)
            AvatarLog.warn(AvatarLog.WarningType.CONFIGURATION, "Properties file for " + getName() + " wasn't successfully loaded, things will start breaking!");
        return properties != null && properties.getBaseBooleanValue(identifier, data
                .getLevel(), data.getDynamicPath());
    }

    protected BendingStyle controller() {
        return BendingStyles.get(type);
    }

    /**
     * Get the id of the bending style that this ability belongs to
     */
    public final UUID getBendingId() {
        return type;
    }

    /**
     * Execute this ability. Called both client and server-side.
     *
     * @param ctx Information for the ability
     */
    public void execute(AbilityContext ctx) {
        if (ctx.getBenderEntity() instanceof EntityBender)
            //Used for AI purposes
            ((EntityBender) ctx.getBenderEntity()).modifyAbilities(this);
    }

    /**
     * Gets cooldown to be added after the ability is activated.
     * Not used by charge or status control abilities, as they'll apply a cooldown after the status control
     * is executed.
     */
    public int getCooldown(AbilityContext ctx) {
        if (ctx.getBenderEntity() instanceof EntityPlayer && ((EntityPlayer) ctx.getBenderEntity()).isCreative())
            return 0;
        int coolDown = getProperty(COOLDOWN, ctx.getLevel(), ctx.getDynamicPath()).intValue();
        //Cooldown has a 1.5x multiplier with max burnout
        return (int) (coolDown * (1 + ctx.getAbilityData().getBurnOut() / 200));
    }

    public float getBurnOut(AbilityContext ctx) {
        if (ctx.getBenderEntity() instanceof EntityPlayer && ((EntityPlayer) ctx.getBenderEntity()).isCreative())
            return 0;
        return getProperty(BURNOUT, ctx.getLevel(), ctx.getDynamicPath()).floatValue();
    }

    public float getExhaustion(AbilityContext ctx) {
        if (ctx.getBenderEntity() instanceof EntityPlayer && ((EntityPlayer) ctx.getBenderEntity()).isCreative())
            return 0;
        float exhaustion = getProperty(EXHAUSTION, ctx.getLevel(), ctx.getDynamicPath()).floatValue();
        //Burnout has a 2x multiplier on exhaustion
        return exhaustion * (1 + ctx.getAbilityData().getBurnOut() / 100F);
    }

    /**
     * We now handle chi costs here and in {@code Bender.execute()}
     * rather than manually getting our value value for each ability. Pretty snazzy.
     * Can be overriden if the chi cost shouldn't be applied immediately.
     */
    public float getChiCost(AbilityContext ctx) {
        if (ctx.getBenderEntity() instanceof EntityPlayer && ((EntityPlayer) ctx.getBenderEntity()).isCreative() || ctx.getBenderEntity() instanceof EntityHumanBender)
            return 0;
        float chi = getProperty(CHI_COST, ctx.getLevel(), ctx.getDynamicPath()).floatValue();
        //Burnout has a 1.5x multipler on chi cost
        return chi * (1 + ctx.getAbilityData().getBurnOut() / 200);
    }

    //Same stuff but with AbilityData

    /**
     * Gets cooldown to be added after the ability is activated.
     * Not used by charge or status control abilities, as they'll apply a cooldown after the status control
     * is executed.
     */
    public int getCooldown(AbilityData data) {
        int coolDown = getProperty(COOLDOWN, data.getLevel(), data.getDynamicPath()).intValue();
        //Cooldown has a 1.5x multiplier with max burnout
        return (int) (coolDown * (1 + data.getBurnOut() / 200));
    }

    public float getBurnOut(AbilityData data) {
        return getProperty(BURNOUT, data.getLevel(), data.getDynamicPath()).floatValue();
    }

    public float getExhaustion(AbilityData data) {
        float exhaustion = getProperty(EXHAUSTION, data.getLevel(), data.getDynamicPath()).floatValue();
        //Burnout has a 2x multiplier on exhaustion
        return exhaustion * (1 + data.getBurnOut() / 100F);
    }

    /**
     * We now handle chi costs here and in {@code Bender.execute()}
     * rather than manually getting our value value for each ability. Pretty snazzy.
     * Can be overriden if the chi cost shouldn't be applied immediately.
     */
    public float getChiCost(AbilityData data) {
        float chi = getProperty(CHI_COST, data.getLevel(), data.getDynamicPath()).floatValue();
        //Burnout has a 1.5x multipler on chi cost
        return chi * (1 + data.getBurnOut() / 200);
    }


    /*
     * Generally used for abilities that grant you stat boosts.
     */
    public boolean isBuff() {
        return false;
    }

    /**
     * Generally used for abilities that help with evreryday tasks, such as mining,
     * moving water sources, or just moving around. Ex: Mine Blocks, Air Jump, and
     * Water Bubble are all utility Abilities.
     */

    public boolean isUtility() {
        return false;
    }

    /**
     * Used for abilities that are charged actively. E.g Water Cannon, Lightning Blast, Swirling Inferno, Air Burst.
     * Not used for passively charged abilities like Fireball's 2nd path.
     */
    public boolean isChargeable() {
        return false;
    }

    //Pretty self-explanatory
    public boolean isProjectile() {
        return false;
    }

    /**
     * Used for abilities that are offensive, like earth spike and ravine.
     */
    public boolean isOffensive() {
        return false;
    }

    /**
     * Require that a raycast be sent prior to {@link #execute(AbilityContext)}.
     * Information for the raytrace will then be available through the
     * {@link AbilityContext}.
     *
     * @param range          Range to raycast. -1 for player's reach.
     * @param raycastLiquids Whether to keep going on hit liquids
     */
    protected void requireRaytrace(double range, boolean raycastLiquids) {
        this.raytrace = new Raytrace.Info(range, raycastLiquids);
    }

    /**
     * Get the request raytrace requirements for when the ability is activated.
     */
    public final Raytrace.Info getRaytrace() {
        return raytrace;
    }

    /**
     * Gets the name of this ability. Will be all lowercase with no spaces.
     */
    public String getName() {
        return name;
    }

    /**
     * Whether the ability is visible in the radial menu. Note that this doesn't
     * hide the ability from the skills menu.
     */
    public boolean isVisibleInRadial() {
        return true;
    }

    public int getBaseTier() {
        //In this case, just use the values found in AbilityData for levels (0 = I, 1 = II, e.t.c)
        return getProperty(TIER, 0).intValue();
    }

    public int getCurrentTier(AbilityContext ctx) {
        return getProperty(TIER, ctx.getLevel(), ctx.getDynamicPath()).intValue();
    }

    //Only for abilities in sub-elements.
    public int getCurrentParentTier(AbilityContext ctx) {
        return getProperty(PARENT_TIER, ctx.getLevel(), ctx.getDynamicPath()).intValue();
    }

    public int getCurrentTier(AbilityData data) {
        return getProperty(TIER, data.getLevel(), data.getDynamicPath()).intValue();
    }

    //Only for abilities in sub-elements.
    public int getCurrentParentTier(AbilityData data) {
        return getProperty(PARENT_TIER, data.getLevel(), data.getDynamicPath()).intValue();
    }


    public BendingStyle getElement() {
        return BendingStyles.get(getBendingId());
    }

    public int getBaseParentTier() {
        if (this.getElement().getParentBendingId() != null) {
            return getProperty(PARENT_TIER, 0).intValue();
        }
        return 0;
    }

    public boolean isCompatibleScroll(ItemStack stack, AbilityContext ctx) {
        if (getBendingId() != null) {
            if (stack.getItem() instanceof ItemScroll) {
                Scrolls.ScrollType type = Scrolls.getTypeForStack(stack);
                assert type != null;
                if (type.getBendingId() == getBendingId() || type == Scrolls.ScrollType.ALL)
                    return Scrolls.getTierForStack(stack) >= getCurrentTier(ctx);

                if (getBaseParentTier() > 0)
                    if (Objects.requireNonNull(BendingStyles.get(getBendingId())).getParentBendingId() == type
                            .getBendingId())
                        return Scrolls.getTierForStack(stack) >= getCurrentParentTier(ctx);
            }
        }
        return false;
    }

    /**
     * Creates a new instance of AI for the given entity/bender.
     */
    public BendingAi getAi(EntityLiving entity, Bender bender) {
        return new DefaultAbilityAi(this, entity, bender);
    }

    /**
     * Adds the given JSON identifiers to the configurable base properties of this ability. This should be called from
     * the constructor  . <i>It is highly recommended that property keys be defined as constants,
     * as they will be needed later to retrieve the properties during the casting methods.</i>
     * <p></p>
     * General spell classes will call this method to set any properties they require in order to work properly, and
     * the relevant keys will be public constants.
     *
     * @param keys One or more spell property keys to add to the spell. By convention, these are lowercase_with_underscores.
     *             If any of these already exists, a warning will be printed to the console.
     * @return The spell instance, allowing this method to be chained onto the constructor.
     * @throws IllegalStateException if this method is called after the spell properties have been initialised.
     */
    // Nobody can remove property keys, which guarantees that spell classes always have the properties they need.
    // It also means that subclasses need not worry about properties already defined and used in their superclass.
    // Conversely, general spell classes ONLY EVER define the properties they ACTUALLY USE.
    public final Ability addProperties(String... keys) {

        //I need to have an option for specifying the level as well
        if (arePropertiesInitialised())
            throw new IllegalStateException("Tried to add ability properties after they were initialised");

        for (String key : keys)
            if (propertyKeys.contains(key))
                AvatarLog.warn(AvatarLog.WarningType.CONFIGURATION, "Tried to add a duplicate property key '"
                        + key + "' to ability " + getName());

        Collections.addAll(propertyKeys, keys);

        return this;
    }

    /**
     * Adds the given JSON identifiers to the configurable base properties of this ability. This should be called from
     * the constructor  . <i>It is highly recommended that property keys be defined as constants,
     * as they will be needed later to retrieve the properties during the casting methods.</i>
     * <p></p>
     * General spell classes will call this method to set any properties they require in order to work properly, and
     * the relevant keys will be public constants.
     *
     * @param keys One or more spell property keys to add to the spell. By convention, these are lowercase_with_underscores.
     *             If any of these already exists, a warning will be printed to the console.
     * @return The spell instance, allowing this method to be chained onto the constructor.
     * @throws IllegalStateException if this method is called after the spell properties have been initialised.
     */
    // Nobody can remove property keys, which guarantees that spell classes always have the properties they need.
    // It also means that subclasses need not worry about properties already defined and used in their superclass.
    // Conversely, general spell classes ONLY EVER define the properties they ACTUALLY USE.
    public final Ability addBooleanProperties(String... keys) {

        //I need to have an option for specifying the level as well
        if (arePropertiesInitialised())
            throw new IllegalStateException("Tried to add ability properties after they were initialised");

        for (String key : keys)
            if (booleanPropertyKeys.contains(key))
                AvatarLog.warn(AvatarLog.WarningType.CONFIGURATION, "Tried to add a duplicate property key '"
                        + key + "' to ability " + getName());

        Collections.addAll(booleanPropertyKeys, keys);

        return this;
    }

    /**
     * Internal, do not use.
     */
    public final String[] getPropertyKeys() {
        return propertyKeys.toArray(new String[0]);
    }


    /**
     * Internal, do not use.
     */
    public final String[] getBooleanPropertyKeys() {
        return booleanPropertyKeys.toArray(new String[0]);
    }

    /**
     * Returns true if this spell's properties have been initialised, false if not. Check this if you're attempting
     * to access them from code that could be called before wizardry's {@code init()} method (e.g. item attributes).
     */
    public final boolean arePropertiesInitialised() {
        return properties != null;
    }

    /**
     * Sets this spell's properties to the given {@link AbilityProperties} object, but only if it doesn't already
     * have one. This prevents spell properties from being changed after initialisation.
     */
    public void setProperties(@Nonnull AbilityProperties properties) {

        if (!arePropertiesInitialised()) {
            this.properties = properties;
            if (this.globalProperties == null) this.globalProperties = properties;
            AvatarLog.info("Successfully set properties for " + getName() + "!");
        } else {
            AvatarLog.info("A mod attempted to set an ability's properties, but they were already initialised.");
        }
    }
}
