package com.crowsofwar.avatar.common.triggers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.JsonUtils;

import javax.annotation.Nullable;
import java.util.Arrays;

public class AbilityPredicate {
    public static final AbilityPredicate ANY = new AbilityPredicate();
    private final int initialLevel;
    private final int newLevel;
    private final int level;
    private Ability ability;
    private final Ability[] abilities;

    public AbilityPredicate() {
        this.ability = null;
        this.initialLevel = 0;
        this.newLevel = 0;
        this.level = 0;
        this.abilities = Abilities.all().toArray(new Ability[0]).clone();
    }

    public AbilityPredicate(@Nullable Ability ability, Ability[] abilities, int initialLevel, int newLevel, int level) {
        this.ability = ability;
        this.initialLevel = initialLevel;
        this.newLevel = newLevel;
        this.level = level;
        this.abilities = abilities;
    }

    public static AbilityPredicate deserialize(@Nullable JsonElement element) {

        if (element != null && !element.isJsonNull()) {

            JsonObject jsonobject = JsonUtils.getJsonObject(element, "ability");

            Ability ability = null;

            if (jsonobject.has("ability")) {

                String s = JsonUtils.getString(jsonobject, "ability");
                ability = Abilities.get(s);

                if (ability == null) {
                    throw new JsonSyntaxException("Unknown ability name '" + s + "'");
                }
            }

            int fromLevel = -1;

            if (jsonobject.has("fromLevel")) {

                String s = JsonUtils.getString(jsonobject, "fromLevel");

                fromLevel = Integer.parseInt(s);

                if (fromLevel < 0) {
                    throw new JsonSyntaxException("Unknown fromLevel value '" + s + "'");
                }
            }

            int toLevel = -1;

            if (jsonobject.has("toLevel")) {

                String s = JsonUtils.getString(jsonobject, "toLevel");

                toLevel = Integer.parseInt(s);

                if (toLevel < 0) {
                    throw new JsonSyntaxException("Unknown toLevel value '" + s + "'");
                }
            }

            int level = -1;

            if (jsonobject.has("level")) {

                String s = JsonUtils.getString(jsonobject, "level");

                level = Integer.parseInt(s);

                if (level < 0) {
                    throw new JsonSyntaxException("Unknown toLevel value '" + s + "'");
                }
            }

            Ability[] abilities = Abilities.all().toArray(new Ability[Abilities.all().size()]);

            if (jsonobject.has("abilities")) {
                try {
                    JsonArray array = JsonUtils.getJsonArray(jsonobject, "abilities");
                    abilities = Streams.stream(array)
                            .map(je -> Abilities.get(JsonUtils.getString(je, "ability")))
                            .toArray(Ability[]::new);
                } catch (IllegalArgumentException e) {
                    throw new JsonSyntaxException("Incorrect ability predicate value", e);
                }
            }

            return new AbilityPredicate(ability, abilities, fromLevel, toLevel, level);

        } else {
            return ANY;
        }
    }

    public static AbilityPredicate[] deserializeArray(@Nullable JsonElement element) {

        if (element != null && !element.isJsonNull()) {

            JsonArray jsonarray = JsonUtils.getJsonArray(element, "abilities");
            AbilityPredicate[] predicates = new AbilityPredicate[jsonarray.size()];

            for (int i = 0; i < predicates.length; ++i) {
                predicates[i] = deserialize(jsonarray.get(i));
            }

            return predicates;

        } else {
            return new AbilityPredicate[0];
        }
    }

    public boolean test(Ability ability, int initialLevel, int newLevel) {

        if (this.ability != null && ability != this.ability) {
            return false;
        } else if (!Arrays.asList(this.abilities).contains(ability)) {
            return false;
        } else return this.initialLevel == initialLevel && this.newLevel == newLevel;
    }

    public boolean test(Ability ability, int level) {

        if (this.ability != null && ability != this.ability) {
            return false;
        } else if (!Arrays.asList(this.abilities).contains(ability)) {
            return false;
        } else return this.level == level;
    }
}