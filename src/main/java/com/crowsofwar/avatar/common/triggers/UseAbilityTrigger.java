package com.crowsofwar.avatar.common.triggers;

import com.crowsofwar.avatar.bending.bending.Ability;
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

public class UseAbilityTrigger implements ICriterionTrigger<UseAbilityTrigger.Instance> {
    private final ResourceLocation RL;
    private final Map<PlayerAdvancements, UseAbilityTrigger.Listeners> listeners = Maps.newHashMap();

    /**
     * Instantiates a new custom trigger.
     *
     * @param parString the par string
     */
    public UseAbilityTrigger(String parString) {
        super();
        RL = new ResourceLocation(parString);
    }

    /**
     * Instantiates a new custom trigger.
     *
     * @param parRL the par RL
     */
    public UseAbilityTrigger(ResourceLocation parRL) {
        super();
        RL = parRL;
    }

    /* (non-Javadoc)
     * @see net.minecraft.advancements.ICriterionTrigger#getId()
     */
    @Override
    public ResourceLocation getId() {
        return RL;
    }

    /* (non-Javadoc)
     * @see net.minecraft.advancements.ICriterionTrigger#addListener(net.minecraft.advancements.PlayerAdvancements, net.minecraft.advancements.ICriterionTrigger.Listener)
     */
    @Override
    public void addListener(PlayerAdvancements playerAdvancementsIn, Listener<UseAbilityTrigger.Instance> listener) {
        UseAbilityTrigger.Listeners myCustomTrigger$listeners = listeners.get(playerAdvancementsIn);

        if (myCustomTrigger$listeners == null) {
            myCustomTrigger$listeners = new UseAbilityTrigger.Listeners(playerAdvancementsIn);
            listeners.put(playerAdvancementsIn, myCustomTrigger$listeners);
        }

        myCustomTrigger$listeners.add(listener);
    }

    /* (non-Javadoc)
     * @see net.minecraft.advancements.ICriterionTrigger#removeListener(net.minecraft.advancements.PlayerAdvancements, net.minecraft.advancements.ICriterionTrigger.Listener)
     */
    @Override
    public void removeListener(PlayerAdvancements playerAdvancementsIn, Listener<UseAbilityTrigger.Instance> listener) {
        UseAbilityTrigger.Listeners tameanimaltrigger$listeners = listeners.get(playerAdvancementsIn);

        if (tameanimaltrigger$listeners != null) {
            tameanimaltrigger$listeners.remove(listener);

            if (tameanimaltrigger$listeners.isEmpty()) {
                listeners.remove(playerAdvancementsIn);
            }
        }
    }

    /* (non-Javadoc)
     * @see net.minecraft.advancements.ICriterionTrigger#removeAllListeners(net.minecraft.advancements.PlayerAdvancements)
     */
    @Override
    public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
        listeners.remove(playerAdvancementsIn);
    }

    /**
     * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
     *
     * @param json    the json
     * @param context the context
     * @return the tame bird trigger. instance
     */
    @Override
    public UseAbilityTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return new UseAbilityTrigger.Instance(getId(),
                AbilityPredicate.deserialize(json.get("ability")));
    }

    /**
     * Trigger.
     *
     * @param parPlayer the player
     */
    public void trigger(EntityPlayerMP parPlayer, Ability ability, int level) {
        UseAbilityTrigger.Listeners useAbilityTrigger$listeners = listeners.get(parPlayer.getAdvancements());

        if (useAbilityTrigger$listeners != null) {
            useAbilityTrigger$listeners.trigger(ability, level);
        }
    }

    public static class Instance extends AbstractCriterionInstance {
        private final AbilityPredicate ability;

        /**
         * Instantiates a new instance.
         *
         * @param criterion the criterion RL
         */
        public Instance(ResourceLocation criterion, AbilityPredicate ability) {
            super(criterion);
            this.ability = ability;
        }

        /**
         * Test.
         *
         * @return true, if successful
         */
        public boolean test(Ability ability, int level) {
            return this.ability.test(ability, level);
        }
    }

    static class Listeners {
        private final PlayerAdvancements playerAdvancements;
        private final Set<Listener<UseAbilityTrigger.Instance>> listeners = Sets.newHashSet();

        /**
         * Instantiates a new listeners.
         *
         * @param playerAdvancementsIn the player advancements in
         */
        public Listeners(PlayerAdvancements playerAdvancementsIn) {
            playerAdvancements = playerAdvancementsIn;
        }

        /**
         * Checks if is empty.
         *
         * @return true, if is empty
         */
        public boolean isEmpty() {
            return listeners.isEmpty();
        }

        /**
         * Adds the listener.
         *
         * @param listener the listener
         */
        public void add(Listener<UseAbilityTrigger.Instance> listener) {
            listeners.add(listener);
        }

        /**
         * Removes the listener.
         *
         * @param listener the listener
         */
        public void remove(Listener<UseAbilityTrigger.Instance> listener) {
            listeners.remove(listener);
        }

        /**
         * Trigger.
         *
         * @param ability
         */
        public void trigger(Ability ability, int level) {
            ArrayList<Listener<UseAbilityTrigger.Instance>> list = null;

            for (Listener<UseAbilityTrigger.Instance> listener : listeners) {
                if (listener.getCriterionInstance().test(ability, level)) {
                    if (list == null) {
                        list = Lists.newArrayList();
                    }

                    list.add(listener);
                }
            }

            if (list != null) {
                for (Listener<UseAbilityTrigger.Instance> listener1 : list) {
                    listener1.grantCriterion(playerAdvancements);
                }
            }
        }
    }
}