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

package com.crowsofwar.avatar.common.bending;

import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import com.crowsofwar.avatar.common.item.scroll.ItemScroll;
import com.crowsofwar.avatar.common.item.scroll.Scrolls;
import com.crowsofwar.avatar.common.util.Raytrace;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Encapsulates all logic required for a bending ability. There is 1 instance of
 * a bending ability for each ability present - similar to BendingController.
 *
 * @author CrowsOfWar
 */
public abstract class Ability {

    //NOTE: No client side particles can be spawned in an ability class due to abilities being executed server-side.

    private final UUID type;
    private final String name;
    /**
     * Used in initialisation.
     */
    private final Set<String> propertyKeys = new HashSet<>();
    private Raytrace.Info raytrace;

    public Ability(UUID bendingType, String name) {
        this.type = bendingType;
        this.name = name;
        this.raytrace = new Raytrace.Info();
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
     * Execute this ability. Only called on server.
     *
     * @param ctx Information for the ability
     */
    public void execute(AbilityContext ctx) {
        if (ctx.getBenderEntity() instanceof EntityBender)
            //Used for AI purposes
            ((EntityBender) ctx.getBenderEntity()).modifyAbilities(this);
    }

    /**
     * All configurable stats. Should be implemented here so that
     * it automagically gets the correct stats from the json files based on the ability name
     * with 0 need for an override (except in the case of status control abilities).
     */

    /**
     * Gets cooldown to be added after the ability is activated.
     * Not used by charge or status control abilities, as they'll apply a cooldown after the status control
     * is executed.
     */
    public int getCooldown(AbilityContext ctx) {
        return 0;
    }

    public float getBurnOut(AbilityContext ctx) {
        return 0F;
    }

    public float getExhaustion(AbilityContext ctx) {
        return 0F;
    }

    /**
     * We now handle chi costs here and in Bender.execute automagically,
     * rather than manually calling it in each ability. Pretty snazzy.
     * Can be overriden if the chi cost shouldn't be applied immediately.
     */
    public float getChiCost(AbilityContext ctx) {
        return 0F;
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
        return 1;
    }


    public int getCurrentTier(int level) {
        int tier = getBaseTier();
        switch (level) {
            default:
                break;
            case 2:
                tier++;
                break;
            case 3:
                tier += 2;
                break;
        }
        return tier;
    }

    //Only for abilities in sub-elements.
    public int getCurrentParentTier(int level) {
        int tier = getBaseParentTier();
        switch (level) {
            default:
                break;
            case 2:
                tier++;
                break;
            case 3:
                tier += 2;
                break;
        }
        return tier;
    }

    public BendingStyle getElement() {
        return BendingStyles.get(getBendingId());
    }

    public int getBaseParentTier() {
        if (this.getElement().getParentBendingId() != null) {
            return 1;
        }
        return 0;
    }

    public boolean isCompatibleScroll(ItemStack stack, int level) {
        if (getBendingId() != null) {
            if (stack.getItem() instanceof ItemScroll) {
                Scrolls.ScrollType type = Scrolls.getTypeForStack(stack);
                assert type != null;
                if (type.getBendingId() == getBendingId() || type == Scrolls.ScrollType.ALL)
                    return Scrolls.getTierForStack(stack) >= getCurrentTier(level);

                if (getBaseParentTier() > 0)
                    if (Objects.requireNonNull(BendingStyles.get(getBendingId())).getParentBendingId() == type
                            .getBendingId())
                        return Scrolls.getTierForStack(stack) >= getCurrentParentTier(level);
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

    /* Properties; have to fix. Copied from Wizardry. */
    /**
     * Adds the given JSON identifiers to the configurable base properties of this spell. This should be called from
     * the constructor or {@link Spell#init()}. <i>It is highly recommended that property keys be defined as constants,
     * as they will be needed later to retrieve the properties during the casting methods.</i>
     * <p></p>
     * General spell classes will call this method to set any properties they require in order to work properly, and
     * the relevant keys will be public constants.
     * @param keys One or more spell property keys to add to the spell. By convention, these are lowercase_with_underscores.
     *             If any of these already exists, a warning will be printed to the console.
     * @return The spell instance, allowing this method to be chained onto the constructor.
     * @throws IllegalStateException if this method is called after the spell properties have been initialised.
     */
    // Nobody can remove property keys, which guarantees that spell classes always have the properties they need.
    // It also means that subclasses need not worry about properties already defined and used in their superclass.
    // Conversely, general spell classes ONLY EVER define the properties they ACTUALLY USE.
    /*public final Spell addProperties(String... keys){

        if(arePropertiesInitialised()) throw new IllegalStateException("Tried to add spell properties after they were initialised");

        for(String key : keys) if(propertyKeys.contains(key)) Wizardry.logger.warn("Tried to add a duplicate property key '"
                + key + "' to spell " + this.getRegistryName());

        Collections.addAll(propertyKeys, keys);

        return this;
    }

    /** Internal, do not use. */
   /* public final String[] getPropertyKeys(){
        return propertyKeys.toArray(new String[0]);
    }

    /** Returns true if this spell's properties have been initialised, false if not. Check this if you're attempting
     * to access them from code that could be called before wizardry's {@code init()} method (e.g. item attributes). */
    /*public final boolean arePropertiesInitialised(){
        return properties != null;
    }

    /** Sets this spell's properties to the given {@link SpellProperties} object, but only if it doesn't already
     * have one. This prevents spell properties from being changed after initialisation. */
    /*public void setProperties(@Nonnull SpellProperties properties){

        if(!arePropertiesInitialised()){
            this.properties = properties;
            if(this.globalProperties == null) this.globalProperties = properties;
        }else{
            Wizardry.logger.info("A mod attempted to set a spell's properties, but they were already initialised.");
        }
    }

    /** Called from the event handler when a player logs in. */
   /* public static void syncProperties(EntityPlayer player){
        if(player instanceof EntityPlayerMP){
            // On the server side, send a packet to the player to synchronise their spell properties
            // To avoid sending extra data unnecessarily, the spell properties are sent in order of spell ID
            List<Spell> spells = new ArrayList<>(registry.getValuesCollection());
            spells.sort(Comparator.comparingInt(Spell::networkID));
            WizardryPacketHandler.net.sendToAll(new PacketSpellProperties.Message(spells.stream()
                    .map(s -> s.properties).toArray(SpellProperties[]::new)));
        }else{
            // On the client side, wipe the spell properties so the new ones can be set
            // TESTME: Can we guarantee this happens before the packet arrives?
            clearProperties();
        }
    }

    private static void clearProperties(){
        for(Spell spell : registry){
            spell.properties = null;
        }
    }**/
}
