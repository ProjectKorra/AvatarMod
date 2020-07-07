package com.crowsofwar.avatar.common.triggers;

import com.crowsofwar.avatar.common.bending.Abilities;
import com.crowsofwar.avatar.common.bending.Ability;
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
    private final Ability ability;
    private final Ability[] abilities;

    public AbilityPredicate(){
        this.ability = null;
        this.abilities = Abilities.all().toArray(new Ability[Abilities.all().size()]);
    }

    public AbilityPredicate(@Nullable Ability ability, Ability[] abilities){
        this.ability = ability;
        this.abilities = abilities;
    }

    public boolean test(Ability ability){

        if(this.ability != null && ability != this.ability){
            return false;
        }else if(!Arrays.asList(this.abilities).contains(ability)){
            return false;
        }

        return true;
    }

    public static AbilityPredicate deserialize(@Nullable JsonElement element){

        if(element != null && !element.isJsonNull()){

            JsonObject jsonobject = JsonUtils.getJsonObject(element, "ability");

            Ability ability = null;

            if(jsonobject.has("ability")){

                String s = JsonUtils.getString(jsonobject, "ability");
                ability = Abilities.get(s);

                if(ability == null){
                    throw new JsonSyntaxException("Unknown ability name '" + s + "'");
                }
            }

            Ability[] abilities = Abilities.all().toArray(new Ability[Abilities.all().size()]);

            if(jsonobject.has("abilities")){
                try{
                    JsonArray array = JsonUtils.getJsonArray(jsonobject, "abilities");
                    abilities = Streams.stream(array)
                            .map(je -> Abilities.get(JsonUtils.getString(je, "ability")))
                            .toArray(Ability[]::new);
                }catch(IllegalArgumentException e){
                    throw new JsonSyntaxException("Incorrect ability predicate value", e);
                }
            }

            return new AbilityPredicate(ability, abilities);

        }else{
            return ANY;
        }
    }

    public static AbilityPredicate[] deserializeArray(@Nullable JsonElement element){

        if(element != null && !element.isJsonNull()){

            JsonArray jsonarray = JsonUtils.getJsonArray(element, "abilities");
            AbilityPredicate[] predicates = new AbilityPredicate[jsonarray.size()];

            for(int i = 0; i < predicates.length; ++i){
                predicates[i] = deserialize(jsonarray.get(i));
            }

            return predicates;

        }else{
            return new AbilityPredicate[0];
        }
    }
}
