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
import com.crowsofwar.avatar.entity.mob.EntityBender;
import com.crowsofwar.avatar.entity.mob.EntityHumanBender;
import com.crowsofwar.avatar.item.scroll.ItemScroll;
import com.crowsofwar.avatar.item.scroll.Scrolls;
import com.crowsofwar.avatar.network.packets.PacketCSyncAbilityProperties;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
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
//TODO: Add elemental abstraction layers for different abilities (esp cause they share properties)
@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public abstract class Ability {

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
            RADIUS = "radius",
            EFFECT_RADIUS = "effectRadius",
            EFFECT_DAMAGE = "effectDamage",
            AIM_ASSIST = "aimAssist",
            RANGE = "range",
            COLOUR_R = "colouR",
            COLOUR_G = "colourG",
            COLOUR_B = "colourB",
            POTION_EFFECTS = "potionEffects",
            PIERCING = "piercing",
            R = "R",
            G = "G",
            B = "B",
            //For melee abilities
            MAX_COMBO = "maxCombo";
    //Airbending stuff
    public static final String
            PUSH_REDSTONE = "pushRedstone",
            PUSH_IRONDOOR = "pushIronDoor",
            PUSH_STONE = "pushStoneButton",
            PUSH_IRON_TRAPDOOR = "pushIronTrapDoor";
    //Firebending stuff
    public static final String
            FIRE_R = "fireR",
            FIRE_G = "fireG",
            FIRE_B = "fireB",
            FADE_R = "fadeR",
            FADE_G = "fadeG",
            FADE_B = "fadeB",
            SMELTS = "smelts",
            SETS_FIRES = "setsFires";
    //Waterbending stuff
    public static final String
            //Amount is the amount consumed
            WATER_AMOUNT = "waterAmount",
            //Level is the HP of it (applies to source abilities such as water bubble)
            WATER_LEVEL = "waterLevel",
            SOURCE_RANGE = "sourceRange",
            SOURCE_ANGLES = "sourceAngles",
            PLANT_BEND = "plantbend";
    //Buff abilities
    public static final String
            POWERRATING = "powerrating",
            EFFECT_LEVEl = "effectLevel",
            EFFECT_STRENGTH = "effectStrength",
            EFFECT_DURATION = "effectDuration",
            CHI_BOOST = "chiBoost",
            CHI_REGEN_BOOST = "chiRegenBoost",
            REGEN_LEVEL = "regenLevel",
            REGEN_DURATION = "regenDuration",
            RESISTANCE_LEVEL = "resistanceLevel",
            RESISTANCE_DURATION = "resistanceDuration",
            SLOWNESS_LEVEL = "slownessLevel",
            SLOWNESS_DURATION = "slownessDuration",
            SATURATION_LEVEL = "saturationLevel",
            SATURATION_DURATION = "saturationDuration",
            STRENGTH_LEVEL = "strengthLevel",
            STRENGTH_DURATION = "strengthDuration",
            SPEED_LEVEL = "speedLevel",
            SPEED_DURATION = "speedDuration",
            HEALTH_LEVEL = "healthLevel",
            HEALTH_DURATION = "healthDuration",
            JUMP_LEVEL = "jumpLevel",
            JUMP_DURATION = "jumpDuration";
    //Combustion/boom stuff
    public static final String
            EXPLOSION_SIZE = "explosionSize",
            EXPLOSION_DAMAGE = "explosionDamage";
    //Chargeable stuff
    public static final String
            MAX_SIZE = "maxSize",
            MAX_DAMAGE = "maxDamage",
            MAX_CHICOST = "maxChiCost",
            MAX_BURNOUT = "maxBurnout",
            MAX_EXHAUSTION = "maxExhaustion",
            MAX_COOLDOWN = "maxCooldown";
    //Jump stuff
    public static final String
            JUMP_HEIGHT = "jumpHeight",
            STOP_SHOCKWAVE = "stopShockwave",
            GROUND_POUND = "groundPound",
            JUMPS = "numberOfJumps",
            FALL_ABSORPTION = "fallAbsorption";
    //Shield stuff
    public static final String
            CHI_PERCENT = "chiPercentOnHit",
            CHI_PER_SECOND = "chiPerSecond",
            BURNOUT_HIT = "burnOutOnHit",
            MAX_HEALTH = "maxHealth",
            EXHAUSTION_HIT = "exhaustionOnHit";

    //Redirect
    public static final String
            DESTROY_TIER = "destroyTier",
            REDIRECT_TIER = "redirectTier",
            ABSORB_FIRE = "absorbFires",
            ABSORB_TIER = "absorbTier",
            POWER_BOOST = "powerBoost",
            POWER_DURATION = "powerDuration";

    public static final int MAX_TIER = 7;

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
     * A reference to the global properties for this ability, so they are only loaded once.
     */
    private AbilityProperties globalProperties;
    private Raytrace.Info raytrace;


    /**
     * NOTE: DO NOT CREATE A NEW INSTANCE OF AN ABILITY FOR GETTING PROPERTIES, IT'LL JUST RETURN NULL.
     * INSTEAD, call {@code Abilities.get(String name)} and use that.
     */
    public Ability(UUID bendingType, String name) {
        this.type = bendingType;
        this.name = name;
        this.raytrace = new Raytrace.Info();
    }

    /**
     * Called from the event handler when a player logs in.
     */
    public static void syncProperties(EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP)) {
            // On the client side, wipe the ability properties so the new ones can be set
            // TESTME: Can we guarantee this happens before the packet arrives?
            clearProperties();
        } else {
            AbilityProperties.loadWorldSpecificAbilityProperties(player.world);
            for (Ability ability : Abilities.all()) {
                if (!ability.arePropertiesInitialised()) ability.setProperties(ability.globalProperties);
            }
            // On the server side, send a packet to the player to synchronise their spell properties
            List<Ability> abilities = Abilities.all();
            AvatarMod.network.sendToAll(new PacketCSyncAbilityProperties(abilities.stream().map(a -> a.properties).toArray(AbilityProperties[]::new)));

        }
    }

    public static void syncEntityProperties() {
        //Syncs properties for everything upon initial world generation
        for (Ability ability : Abilities.all()) {
            if (!ability.arePropertiesInitialised()) ability.setProperties(ability.globalProperties);
        }
        // On the server side, send a packet to the player to synchronise their spell properties
        List<Ability> abilities = Abilities.all();
        AvatarMod.network.sendToAll(new PacketCSyncAbilityProperties(abilities.stream().map(a -> a.properties).toArray(AbilityProperties[]::new)));

        //Prevents NPCS from yeeting
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

    public static boolean propertyEqualsInhibitor(String property) {
        return property.equals(CHI_COST) || property.equals(EXHAUSTION) || property.equals(COOLDOWN)
                || property.equals(BURNOUT) || property.equals(BURNOUT_HIT) || property.equals(CHI_PER_SECOND)
                || property.equals(CHI_PERCENT) || property.equals(EXHAUSTION_HIT);
    }

    /**
     * All configurable stats. Should be implemented here so that
     * it automagically gets the correct stats from the json files based on the ability name
     * with 0 need for an override (except in the case of status control abilities).
     */

    public void init() {
        //Base properties belonging to all abilities
        addProperties(TIER, CHI_COST, BURNOUT, BURNOUT_REGEN, COOLDOWN, EXHAUSTION);

        if (isProjectile() || isOffensive()) {
            addProperties(LIFETIME, SPEED, CHI_HIT, PERFORMANCE, XP_HIT, SIZE, KNOCKBACK);
            if (isOffensive())
                addProperties(DAMAGE);
        }
        if (isBuff())
            addProperties(DURATION, XP_USE, POWERRATING);
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
     * Used for adjusting requireRaytrace so that consuming water levels works properly.
     * Also affects other aspects of BendingContext and AbilityContext if one so wishes.
     * You can use your AbilityProperties here!
     */
    public void postInit() {
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
    //NOTE: These first three methods *ARE NOT* affected by ability modifiers. Use the other two methods if you wish for something
    //to be affected. I.e, all the time.
    public final Number getProperty(String identifier, int abilityLevel) {
        return properties == null ? 1 : properties.getBaseValue(identifier, abilityLevel, AbilityData.AbilityTreePath.MAIN);
    }

    public final Number getProperty(String identifier, int abilityLevel, AbilityData.AbilityTreePath path) {
        return properties == null ? 1 : properties.getBaseValue(identifier, abilityLevel, path);
    }

    public final Number getProperty(String identifier) {
        return properties == null ? 1 : properties.getBaseValue(identifier, 0, AbilityData.AbilityTreePath.MAIN);
    }

    public final Number getProperty(String identifier, AbilityContext ctx) {
        return properties == null ? 1 : ctx.getAbilityData().modify(identifier, properties.getBaseValue(identifier, ctx.getLevel(), ctx.getDynamicPath()));
    }

    public final Number getProperty(String identifier, AbilityData data) {
        return properties == null ? 1 : data.modify(identifier, properties.getBaseValue(identifier, data.getLevel(), data.getDynamicPath()));
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
        return properties != null && properties.getBaseBooleanValue(identifier, abilityLevel, AbilityData.AbilityTreePath.MAIN);
    }

    public final boolean getBooleanProperty(String identifier, int abilityLevel, AbilityData.AbilityTreePath path) {
        return properties != null && properties.getBaseBooleanValue(identifier, abilityLevel, path);
    }

    public final boolean getBooleanProperty(String identifier) {
        return properties != null && properties.getBaseBooleanValue(identifier, 0, AbilityData.AbilityTreePath.MAIN);
    }

    public final boolean getBooleanProperty(String identifier, AbilityContext ctx) {
        return properties != null && properties.getBaseBooleanValue(identifier, ctx.getLevel(), ctx.getDynamicPath());
    }

    public final boolean getBooleanProperty(String identifier, AbilityData data) {
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
        int coolDown = getProperty(COOLDOWN, ctx).intValue();
        coolDown *= (2 - ctx.getAbilityData().getDamageMult()) * (1 / ctx.getAbilityData().getXpModifier());
        //Cooldown has a 1.5x multiplier with max burnout
        return (int) (coolDown * (1 + ctx.getAbilityData().getBurnOut() / 200));
    }

    public float getBurnOut(AbilityContext ctx) {
        if (ctx.getBenderEntity() instanceof EntityPlayer && ((EntityPlayer) ctx.getBenderEntity()).isCreative())
            return 0;
        float burnout = getProperty(BURNOUT, ctx).floatValue();
        burnout *= (2 - ctx.getAbilityData().getDamageMult()) * (1 / ctx.getAbilityData().getXpModifier());
        return burnout;
    }

    public float getExhaustion(AbilityContext ctx) {
        if (ctx.getBenderEntity() instanceof EntityPlayer && ((EntityPlayer) ctx.getBenderEntity()).isCreative())
            return 0;
        float exhaustion = getProperty(EXHAUSTION, ctx).floatValue();
        exhaustion *= (2 - ctx.getAbilityData().getDamageMult()) * (1 / ctx.getAbilityData().getXpModifier());
        //Burnout has a 2x multiplier on exhaustion
        return exhaustion * (1 + ctx.getAbilityData().getBurnOut() / 100F);
    }

    //Same stuff but with AbilityData

    /**
     * We now handle chi costs here and in {@code Bender.execute()}
     * rather than manually getting our value value for each ability. Pretty snazzy.
     * Can be overriden if the chi cost shouldn't be applied immediately.
     */
    public float getChiCost(AbilityContext ctx) {
        if (ctx.getBenderEntity() instanceof EntityPlayer && ((EntityPlayer) ctx.getBenderEntity()).isCreative() || ctx.getBenderEntity() instanceof EntityHumanBender)
            return 0;
        float chi = getProperty(CHI_COST, ctx).floatValue();
        chi *= (2 - ctx.getAbilityData().getDamageMult()) * (1 / ctx.getAbilityData().getXpModifier());
        //Burnout has a 1.5x multipler on chi cost
        return chi * (1 + ctx.getAbilityData().getBurnOut() / 200);
    }

    /**
     * Gets cooldown to be added after the ability is activated.
     * Not used by charge or status control abilities, as they'll apply a cooldown after the status control
     * is executed.
     */
    //NOTE: These aren't called by Bender.execute, so no need to override these! Just override the AbilityContext ones,
    //and call these when you need to apply them.
    public int getCooldown(AbilityData data) {
        int coolDown = getProperty(COOLDOWN, data).intValue();
        coolDown *= (2 - data.getDamageMult()) * (1 / data.getXpModifier());
        //Cooldown has a 1.5x multiplier with max burnout
        return (int) (coolDown * (1 + data.getBurnOut() / 200));
    }

    public float getBurnOut(AbilityData data) {
        float burnout = getProperty(BURNOUT, data).floatValue();
        burnout *= (2 - data.getDamageMult()) * (1 / data.getXpModifier());
        return burnout;
    }

    public float getExhaustion(AbilityData data) {
        float exhaustion = getProperty(EXHAUSTION, data).floatValue();
        exhaustion *= (2 - data.getDamageMult()) * (1 / data.getXpModifier());
        //Burnout has a 2x multiplier on exhaustion
        return exhaustion * (1 + data.getBurnOut() / 100F);
    }

    /**
     * We now handle chi costs here and in {@code Bender.execute()}
     * rather than manually getting our value value for each ability. Pretty snazzy.
     * Can be overriden if the chi cost shouldn't be applied immediately.
     */
    public float getChiCost(AbilityData data) {
        float chi = getProperty(CHI_COST, data).floatValue();
        chi *= (2 - data.getDamageMult()) * (1 / data.getXpModifier());
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
        return getProperty(TIER, ctx).intValue();
    }

    //Only for abilities in sub-elements.
    public int getCurrentParentTier(AbilityContext ctx) {
        return getProperty(PARENT_TIER, ctx).intValue();
    }

    public int getCurrentTier(AbilityData data) {
        return getProperty(TIER, data).intValue();
    }

    //Only for abilities in sub-elements.
    public int getCurrentParentTier(AbilityData data) {
        return getProperty(PARENT_TIER, data).intValue();
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
            if (this.globalProperties == null)
                this.globalProperties = properties;
        } else {
         //   AvatarLog.info("A mod attempted to set an ability's properties, but they were already initialised.");
            //   Thread.dumpStack();
        }
    }

    public float powerModify(float val, AbilityData abilityData) {
        val *= abilityData.getDamageMult() * abilityData.getXpModifier() * abilityData.getAbilityPower();
        return val;
    }

}
