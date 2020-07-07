package com.crowsofwar.avatar.common.triggers;

import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.BendingStyles;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.JsonUtils;

import javax.annotation.Nullable;
import java.util.Arrays;

public class ElementPredicate {
    public static final ElementPredicate ANY = new ElementPredicate();
    private final BendingStyle element;
    private final BendingStyle[] elements;

    public ElementPredicate(){
        this.element = null;
        this.elements = BendingStyles.all().toArray(new BendingStyle[BendingStyles.all().size()]);
    }

    public ElementPredicate(@Nullable BendingStyle element, BendingStyle[] elements){
        this.element = element;
        this.elements = elements;
    }

    public boolean test(BendingStyle spell){

        if(this.element != null && spell != this.element){
            return false;
        }else if(!Arrays.asList(this.elements).contains(spell)){
            return false;
        }

        return true;
    }

    public static ElementPredicate deserialize(@Nullable JsonElement element){

        if(element != null && !element.isJsonNull()){

            JsonObject jsonobject = JsonUtils.getJsonObject(element, "element");

            BendingStyle spell = null;

            if(jsonobject.has("element")){

                String s = JsonUtils.getString(jsonobject, "element");
                spell = BendingStyles.get(s);

                if(spell == null){
                    throw new JsonSyntaxException("Unknown element name '" + s + "'");
                }
            }

            BendingStyle[] elements = BendingStyles.all().toArray(new BendingStyle[BendingStyles.all().size()]);

            if(jsonobject.has("elements")){
                try{
                    JsonArray array = JsonUtils.getJsonArray(jsonobject, "elements");
                    elements = Streams.stream(array)
                            .map(je -> BendingStyles.get(JsonUtils.getString(je, "element")))
                            .toArray(BendingStyle[]::new);
                }catch(IllegalArgumentException e){
                    throw new JsonSyntaxException("Incorrect spell predicate value", e);
                }
            }

            return new ElementPredicate(spell, elements);

        }else{
            return ANY;
        }
    }

    public static ElementPredicate[] deserializeArray(@Nullable JsonElement element){

        if(element != null && !element.isJsonNull()){

            JsonArray jsonarray = JsonUtils.getJsonArray(element, "spells");
            ElementPredicate[] predicates = new ElementPredicate[jsonarray.size()];

            for(int i = 0; i < predicates.length; ++i){
                predicates[i] = deserialize(jsonarray.get(i));
            }

            return predicates;

        }else{
            return new ElementPredicate[0];
        }
    }
}
