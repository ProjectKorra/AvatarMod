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

public class ElementRankupTrigger implements ICriterionTrigger<ElementRankupTrigger.Instance>
{
    private final ResourceLocation RL;
    private final Map<PlayerAdvancements, ElementRankupTrigger.Listeners> listeners = Maps.newHashMap();

    /**
     * Instantiates a new custom trigger.
     *
     * @param parString the par string
     */
    public ElementRankupTrigger(String parString)
    {
        super();
        RL = new ResourceLocation(parString);
    }

    /**
     * Instantiates a new custom trigger.
     *
     * @param parRL the par RL
     */
    public ElementRankupTrigger(ResourceLocation parRL)
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
    public void addListener(PlayerAdvancements playerAdvancementsIn, Listener<ElementRankupTrigger.Instance> listener)
    {
        ElementRankupTrigger.Listeners myCustomTrigger$listeners = listeners.get(playerAdvancementsIn);

        if (myCustomTrigger$listeners == null)
        {
            myCustomTrigger$listeners = new ElementRankupTrigger.Listeners(playerAdvancementsIn);
            listeners.put(playerAdvancementsIn, myCustomTrigger$listeners);
        }

        myCustomTrigger$listeners.add(listener);
    }

    /* (non-Javadoc)
     * @see net.minecraft.advancements.ICriterionTrigger#removeListener(net.minecraft.advancements.PlayerAdvancements, net.minecraft.advancements.ICriterionTrigger.Listener)
     */
    @Override
    public void removeListener(PlayerAdvancements playerAdvancementsIn, Listener<ElementRankupTrigger.Instance> listener)
    {
        ElementRankupTrigger.Listeners tameanimaltrigger$listeners = listeners.get(playerAdvancementsIn);

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
    public ElementRankupTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context)
    {
        return new ElementRankupTrigger.Instance(getId(),
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
        ElementRankupTrigger.Listeners tameanimaltrigger$listeners = listeners.get(parPlayer.getAdvancements());

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
        private final Set<Listener<ElementRankupTrigger.Instance>> listeners = Sets.newHashSet();

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
        public void add(Listener<ElementRankupTrigger.Instance> listener)
        {
            listeners.add(listener);
        }

        /**
         * Removes the listener.
         *
         * @param listener the listener
         */
        public void remove(Listener<ElementRankupTrigger.Instance> listener)
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
            ArrayList<Listener<ElementRankupTrigger.Instance>> list = null;

            for (Listener<ElementRankupTrigger.Instance> listener : listeners)
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
                for (Listener<ElementRankupTrigger.Instance> listener1 : list)
                {
                    listener1.grantCriterion(playerAdvancements);
                }
            }
        }
    }
}