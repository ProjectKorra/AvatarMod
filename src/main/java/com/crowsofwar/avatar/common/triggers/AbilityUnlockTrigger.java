/**
 Copyright (C) 2017 by jabelar
 This file is part of jabelar's Minecraft Forge modding examples; as such,
 you can redistribute it and/or modify it under the terms of the GNU
 General Public License as published by the Free Software Foundation,
 either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.
 For a copy of the GNU General Public License see <http://www.gnu.org/licenses/>.
 */
package com.crowsofwar.avatar.common.triggers;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

// TODO: Auto-generated Javadoc

/**
 * This class is part of my simple custom advancement triggering tutorial.
 * See: http://jabelarminecraft.blogspot.com/p/minecraft-modding-custom-triggers-aka.html
 *
 * @author jabelar
 */
public class AbilityUnlockTrigger implements ICriterionTrigger<AbilityUnlockTrigger.Instance>
{
    private final ResourceLocation RL;
    private final Map<PlayerAdvancements, AbilityUnlockTrigger.Listeners> listeners = Maps.newHashMap();

    /**
     * Instantiates a new custom trigger.
     *
     * @param parString the par string
     */
    public AbilityUnlockTrigger(String parString)
    {
        super();
        RL = new ResourceLocation(parString);
    }

    /**
     * Instantiates a new custom trigger.
     *
     * @param parRL the par RL
     */
    public AbilityUnlockTrigger(ResourceLocation parRL)
    {
        super();
        RL = parRL;
    }

    /* (non-Javadoc)
     * @see net.minecraft.advancements.ICriterionTrigger#getId()
     */
    @Override
    public ResourceLocation getId()
    {
        return RL;
    }

    /* (non-Javadoc)
     * @see net.minecraft.advancements.ICriterionTrigger#addListener(net.minecraft.advancements.PlayerAdvancements, net.minecraft.advancements.ICriterionTrigger.Listener)
     */
    @Override
    public void addListener(PlayerAdvancements playerAdvancementsIn, Listener<AbilityUnlockTrigger.Instance> listener)
    {
        AbilityUnlockTrigger.Listeners myCustomTrigger$listeners = listeners.get(playerAdvancementsIn);

        if (myCustomTrigger$listeners == null)
        {
            myCustomTrigger$listeners = new AbilityUnlockTrigger.Listeners(playerAdvancementsIn);
            listeners.put(playerAdvancementsIn, myCustomTrigger$listeners);
        }

        myCustomTrigger$listeners.add(listener);
    }

    /* (non-Javadoc)
     * @see net.minecraft.advancements.ICriterionTrigger#removeListener(net.minecraft.advancements.PlayerAdvancements, net.minecraft.advancements.ICriterionTrigger.Listener)
     */
    @Override
    public void removeListener(PlayerAdvancements playerAdvancementsIn, Listener<AbilityUnlockTrigger.Instance> listener)
    {
        AbilityUnlockTrigger.Listeners tameanimaltrigger$listeners = listeners.get(playerAdvancementsIn);

        if (tameanimaltrigger$listeners != null)
        {
            tameanimaltrigger$listeners.remove(listener);

            if (tameanimaltrigger$listeners.isEmpty())
            {
                listeners.remove(playerAdvancementsIn);
            }
        }
    }

    /* (non-Javadoc)
     * @see net.minecraft.advancements.ICriterionTrigger#removeAllListeners(net.minecraft.advancements.PlayerAdvancements)
     */
    @Override
    public void removeAllListeners(PlayerAdvancements playerAdvancementsIn)
    {
        listeners.remove(playerAdvancementsIn);
    }

    /**
     * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
     *
     * @param json the json
     * @param context the context
     * @return the tame bird trigger. instance
     */
    @Override
    public AbilityUnlockTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context)
    {
        return new AbilityUnlockTrigger.Instance(getId(),
                AbilityPredicate.deserialize(json.get("ability")));
    }

    /**
     * Trigger.
     *
     * @param parPlayer the player
     */
    public void trigger(EntityPlayerMP parPlayer, Ability ability)
    {
        AbilityUnlockTrigger.Listeners tameanimaltrigger$listeners = listeners.get(parPlayer.getAdvancements());

        if (tameanimaltrigger$listeners != null)
        {
            tameanimaltrigger$listeners.trigger(ability);
        }
    }

    public static class Instance extends AbstractCriterionInstance
    {
        private final AbilityPredicate ability;
        /**
         * Instantiates a new instance.
         *
         * @param criterion the criterion RL
         */
        public Instance(ResourceLocation criterion, AbilityPredicate ability)
        {
            super(criterion);
            this.ability = ability;
        }

        /**
         * Test.
         *
         * @return true, if successful
         */
        public boolean test(Ability ability, int level)
        {
            return this.ability.test(ability, level);
        }
    }

    static class Listeners
    {
        private final PlayerAdvancements playerAdvancements;
        private final Set<Listener<AbilityUnlockTrigger.Instance>> listeners = Sets.newHashSet();

        /**
         * Instantiates a new listeners.
         *
         * @param playerAdvancementsIn the player advancements in
         */
        public Listeners(PlayerAdvancements playerAdvancementsIn)
        {
            playerAdvancements = playerAdvancementsIn;
        }

        /**
         * Checks if is empty.
         *
         * @return true, if is empty
         */
        public boolean isEmpty()
        {
            return listeners.isEmpty();
        }

        /**
         * Adds the listener.
         *
         * @param listener the listener
         */
        public void add(Listener<AbilityUnlockTrigger.Instance> listener)
        {
            listeners.add(listener);
        }

        /**
         * Removes the listener.
         *
         * @param listener the listener
         */
        public void remove(Listener<AbilityUnlockTrigger.Instance> listener)
        {
            listeners.remove(listener);
        }

        /**
         * Trigger.
         *
         * @param ability
         */
        public void trigger(Ability ability)
        {
            ArrayList<Listener<AbilityUnlockTrigger.Instance>> list = null;

            for (Listener<AbilityUnlockTrigger.Instance> listener : listeners)
            {
                if (listener.getCriterionInstance().test(ability, 0))
                {
                    if (list == null)
                    {
                        list = Lists.newArrayList();
                    }

                    list.add(listener);
                }
            }

            if (list != null)
            {
                for (Listener<AbilityUnlockTrigger.Instance> listener1 : list)
                {
                    listener1.grantCriterion(playerAdvancements);
                }
            }
        }
    }
}