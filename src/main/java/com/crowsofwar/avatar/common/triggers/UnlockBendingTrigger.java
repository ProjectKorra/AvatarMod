package com.crowsofwar.avatar.common.triggers;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import scala.xml.Elem;

public class UnlockBendingTrigger implements ICriterionTrigger<UnlockBendingTrigger.Instance>
{
    private final ResourceLocation RL;
    private final Map<PlayerAdvancements, UnlockBendingTrigger.Listeners> listeners = Maps.newHashMap();

    /**
     * Instantiates a new custom trigger.
     *
     * @param parString the par string
     */
    public UnlockBendingTrigger(String parString)
    {
        super();
        RL = new ResourceLocation(parString);
    }

    /**
     * Instantiates a new custom trigger.
     *
     * @param parRL the par RL
     */
    public UnlockBendingTrigger(ResourceLocation parRL)
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
    public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<UnlockBendingTrigger.Instance> listener)
    {
        UnlockBendingTrigger.Listeners myCustomTrigger$listeners = listeners.get(playerAdvancementsIn);

        if (myCustomTrigger$listeners == null)
        {
            myCustomTrigger$listeners = new UnlockBendingTrigger.Listeners(playerAdvancementsIn);
            listeners.put(playerAdvancementsIn, myCustomTrigger$listeners);
        }

        myCustomTrigger$listeners.add(listener);
    }

    /* (non-Javadoc)
     * @see net.minecraft.advancements.ICriterionTrigger#removeListener(net.minecraft.advancements.PlayerAdvancements, net.minecraft.advancements.ICriterionTrigger.Listener)
     */
    @Override
    public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<UnlockBendingTrigger.Instance> listener)
    {
        UnlockBendingTrigger.Listeners tameanimaltrigger$listeners = listeners.get(playerAdvancementsIn);

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
    public UnlockBendingTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context)
    {
        return new UnlockBendingTrigger.Instance(getId(),
                ElementPredicate.deserialize(json.get("element")));
    }

    /**
     * Trigger.
     *
     * @param parPlayer the player
     */
    public void trigger(EntityPlayerMP parPlayer, BendingStyle bendingStyle)
    {
        UnlockBendingTrigger.Listeners tameanimaltrigger$listeners = listeners.get(parPlayer.getAdvancements());

        if (tameanimaltrigger$listeners != null)
        {
            tameanimaltrigger$listeners.trigger(bendingStyle);
        }
    }

    public static class Instance extends AbstractCriterionInstance
    {
        private final ElementPredicate element;
        /**
         * Instantiates a new instance.
         *
         * @param criterion the criterion RL
         */
        public Instance(ResourceLocation criterion, ElementPredicate element)
        {
            super(criterion);
            this.element = element;
        }

        /**
         * Test.
         *
         * @return true, if successful
         */
        public boolean test(BendingStyle element)
        {
            return this.element.test(element);
        }
    }

    static class Listeners
    {
        private final PlayerAdvancements playerAdvancements;
        private final Set<ICriterionTrigger.Listener<UnlockBendingTrigger.Instance>> listeners = Sets.newHashSet();

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
        public void add(ICriterionTrigger.Listener<UnlockBendingTrigger.Instance> listener)
        {
            listeners.add(listener);
        }

        /**
         * Removes the listener.
         *
         * @param listener the listener
         */
        public void remove(ICriterionTrigger.Listener<UnlockBendingTrigger.Instance> listener)
        {
            listeners.remove(listener);
        }

        /**
         * Trigger.
         *
         * @param bendingStyle
         */
        public void trigger(BendingStyle bendingStyle)
        {
            ArrayList<ICriterionTrigger.Listener<UnlockBendingTrigger.Instance>> list = null;

            for (ICriterionTrigger.Listener<UnlockBendingTrigger.Instance> listener : listeners)
            {
                if (listener.getCriterionInstance().test(bendingStyle))
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
                for (ICriterionTrigger.Listener<UnlockBendingTrigger.Instance> listener1 : list)
                {
                    listener1.grantCriterion(playerAdvancements);
                }
            }
        }
    }
}