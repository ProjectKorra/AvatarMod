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
public class ElementRankTrigger implements ICriterionTrigger<ElementRankTrigger.Instance>
{
    private final ResourceLocation RL;
    private final Map<PlayerAdvancements, ElementRankTrigger.Listeners> listeners = Maps.newHashMap();

    /**
     * Instantiates a new custom trigger.
     *
     * @param parString the par string
     */
    public ElementRankTrigger(String parString)
    {
        super();
        RL = new ResourceLocation(parString);
    }

    /**
     * Instantiates a new custom trigger.
     *
     * @param parRL the par RL
     */
    public ElementRankTrigger(ResourceLocation parRL)
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
    public void addListener(PlayerAdvancements playerAdvancementsIn, Listener<ElementRankTrigger.Instance> listener)
    {
        ElementRankTrigger.Listeners myCustomTrigger$listeners = listeners.get(playerAdvancementsIn);

        if (myCustomTrigger$listeners == null)
        {
            myCustomTrigger$listeners = new ElementRankTrigger.Listeners(playerAdvancementsIn);
            listeners.put(playerAdvancementsIn, myCustomTrigger$listeners);
        }

        myCustomTrigger$listeners.add(listener);
    }

    /* (non-Javadoc)
     * @see net.minecraft.advancements.ICriterionTrigger#removeListener(net.minecraft.advancements.PlayerAdvancements, net.minecraft.advancements.ICriterionTrigger.Listener)
     */
    @Override
    public void removeListener(PlayerAdvancements playerAdvancementsIn, Listener<ElementRankTrigger.Instance> listener)
    {
        ElementRankTrigger.Listeners tameanimaltrigger$listeners = listeners.get(playerAdvancementsIn);

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
    public ElementRankTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context)
    {
        return new ElementRankTrigger.Instance(getId(),
                ElementPredicate.deserialize(json.get("element")));
    }

    /**
     * Trigger.
     *
     * @param parPlayer the player
     * @param oldRank
     * @param newRank
     */
    public void trigger(EntityPlayerMP parPlayer, BendingStyle bendingStyle, int oldRank, int newRank)
    {
        ElementRankTrigger.Listeners tameanimaltrigger$listeners = listeners.get(parPlayer.getAdvancements());

        if (tameanimaltrigger$listeners != null)
        {
            tameanimaltrigger$listeners.trigger(bendingStyle, oldRank, newRank);
        }
    }

    public static class Instance extends AbstractCriterionInstance
    {

        private final ElementPredicate element;
        /**
         * Instantiates a new instance.
         *
         * @param parRL the par RL
         * @param element
         */
        public Instance(ResourceLocation parRL, ElementPredicate element)
        {
            super(parRL);
            this.element = element;
        }

        /**
         * Test.
         *
         * @return true, if successful
         * @param bendingStyle
         * @param oldRank
         * @param newRank
         */
        public boolean test(BendingStyle bendingStyle, int oldRank, int newRank)
        {
            return this.element.test(bendingStyle) && newRank > oldRank;
        }
    }

    static class Listeners
    {
        private final PlayerAdvancements playerAdvancements;
        private final Set<Listener<ElementRankTrigger.Instance>> listeners = Sets.newHashSet();

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
        public void add(Listener<ElementRankTrigger.Instance> listener)
        {
            listeners.add(listener);
        }

        /**
         * Removes the listener.
         *
         * @param listener the listener
         */
        public void remove(Listener<ElementRankTrigger.Instance> listener)
        {
            listeners.remove(listener);
        }

        /**
         * Trigger.
         *
         * @param bendingStyle
         * @param oldRank
         * @param newRank
         */
        public void trigger(BendingStyle bendingStyle, int oldRank, int newRank)
        {
            ArrayList<Listener<ElementRankTrigger.Instance>> list = null;

            for (Listener<ElementRankTrigger.Instance> listener : listeners)
            {
                if (listener.getCriterionInstance().test(bendingStyle, oldRank, newRank))
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
                for (Listener<ElementRankTrigger.Instance> listener1 : list)
                {
                    listener1.grantCriterion(playerAdvancements);
                }
            }
        }
    }
}