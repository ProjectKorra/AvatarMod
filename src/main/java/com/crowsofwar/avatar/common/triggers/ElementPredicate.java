package com.crowsofwar.avatar.common.triggers;

import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
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
    private final int oldRank;
    private final int newRank;

    public ElementPredicate() {
        this.element = null;
        this.elements = BendingStyles.all().toArray(new BendingStyle[BendingStyles.all().size()]);
        this.oldRank = 0;
        this.newRank = 0;
    }

    public ElementPredicate(@Nullable BendingStyle element, BendingStyle[] elements, int oldRank, int newRank) {
        this.element = element;
        this.elements = elements;
        this.oldRank = oldRank;
        this.newRank = newRank;
    }

    public static ElementPredicate deserialize(@Nullable JsonElement element) {

        if (element != null && !element.isJsonNull()) {

            JsonObject jsonobject = JsonUtils.getJsonObject(element, "element");

            BendingStyle style = null;

            if (jsonobject.has("element")) {

                String s = JsonUtils.getString(jsonobject, "element");
                style = BendingStyles.get(s);

                if (style == null) {
                    throw new JsonSyntaxException("Unknown element name '" + s + "'");
                }
            }

            BendingStyle[] elements = BendingStyles.all().toArray(new BendingStyle[BendingStyles.all().size()]);

            if (jsonobject.has("elements")) {
                try {
                    JsonArray array = JsonUtils.getJsonArray(jsonobject, "elements");
                    elements = Streams.stream(array)
                            .map(je -> BendingStyles.get(JsonUtils.getString(je, "element")))
                            .toArray(BendingStyle[]::new);
                } catch (IllegalArgumentException e) {
                    throw new JsonSyntaxException("Incorrect style predicate value", e);
                }
            }

            int oldRank = 0;
            if (jsonobject.has("oldRank")) {
                try {
                    oldRank = JsonUtils.getInt(jsonobject, "oldRank");
                } catch (IllegalArgumentException e) {
                    throw new JsonSyntaxException("Incorrect rank value", e);
                }
            }

            int newRank = 0;
            if (jsonobject.has("newRank")) {
                try {
                    newRank = JsonUtils.getInt(jsonobject, "newRank");
                } catch (IllegalArgumentException e) {
                    throw new JsonSyntaxException("Incorrect rank value", e);
                }
            }

            return new ElementPredicate(style, elements, oldRank, newRank);

        } else {
            return ANY;
        }
    }

    public static ElementPredicate[] deserializeArray(@Nullable JsonElement element) {

        if (element != null && !element.isJsonNull()) {

            JsonArray jsonarray = JsonUtils.getJsonArray(element, "spells");
            ElementPredicate[] predicates = new ElementPredicate[jsonarray.size()];

            for (int i = 0; i < predicates.length; ++i) {
                predicates[i] = deserialize(jsonarray.get(i));
            }

            return predicates;

        } else {
            return new ElementPredicate[0];
        }
    }

    public boolean test(BendingStyle element) {

        if (this.element != null && element != this.element) {
            return false;
        } else return Arrays.asList(this.elements).contains(element);
    }

    public boolean test(BendingStyle element, int oldRank, int newRank) {
        if (this.element != null && element != this.element) {
            return false;
        }
        else if (newRank <= oldRank) {
            return false;
        }
        else if (this.newRank != newRank || this.oldRank != oldRank) {
            return false;
        }
        else return Arrays.asList(this.elements).contains(element);
    }
}