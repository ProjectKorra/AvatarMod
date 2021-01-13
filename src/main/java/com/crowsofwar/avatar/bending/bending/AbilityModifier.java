package com.crowsofwar.avatar.bending.bending;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.crowsofwar.gorecore.util.GoreCoreByteBufUtil.readString;
import static com.crowsofwar.gorecore.util.GoreCoreByteBufUtil.writeString;

/**
 * Serves as a wrapper class for modifying abilities. Allows you to alter the cooldown,
 * burnout, exhaustion, e.t.c of an ability through its use event. Also useful for
 * mobs.
 */
public class AbilityModifier {

    //I should probably support modifying booleans in the future

    private final List<String> propertyNames;
    private final List<Number> propertyValues;
    private HashMap<String, Number> properties;
    //UUID's to distinguish modifies; e.g the staff modifier
    private UUID id = UUID.fromString("55c88686-6fc1-4cf5-8a31-887702fb2d5e");

    public AbilityModifier() {
        properties = new HashMap<>();
        this.propertyNames = new ArrayList<>();
        this.propertyValues = new ArrayList<>();
    }

    public AbilityModifier(HashMap<String, Number> properties) {
        this.properties = properties;
        //Ensures they're modifiable
        this.propertyNames = new ArrayList<>();
        this.propertyValues = new ArrayList<>();

        this.propertyNames.addAll(properties.keySet());
        this.propertyValues.addAll(properties.values());
    }

    public AbilityModifier(UUID id) {
        this.id = id;
        this.properties = new HashMap<>();
        this.propertyNames = new ArrayList<>();
        this.propertyValues = new ArrayList<>();
    }

    public AbilityModifier(HashMap<String, Number> properties, UUID id) {
        this.properties = properties;
        this.id = id;
        //Ensures they're modifiable
        this.propertyNames = new ArrayList<>();
        this.propertyValues = new ArrayList<>();

        this.propertyNames.addAll(properties.keySet());
        this.propertyValues.addAll(properties.values());
    }

    public static AbilityModifier staticFromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        //Names
        int size = buffer.readVarInt();
        List<String> propertyNames = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            propertyNames.add(i, readString(buffer));
        }
        //Numbers
        size = buffer.readVarInt();
        List<Number> propertyValues = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            propertyValues.add(i, buffer.readFloat());
        }
        //Properties
        HashMap<String, Number> properties = new HashMap<>();
        for (int i = 0; i < propertyNames.size(); i++)
            properties.put(propertyNames.get(i), propertyValues.get(i));
        return new AbilityModifier(properties, buffer.readUniqueId());
    }

    public static AbilityModifier staticFromNBT(NBTTagCompound nbt) {
        //Names
        int size = nbt.getInteger("Property Name Size");
        List<String> propertyNames = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            propertyNames.add(i, nbt.getString("Property Name " + (i + 1)));
        }
        //Values
        size = nbt.getInteger("Property Value Size");
        List<Number> propertyValues = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            propertyValues.add(i, nbt.getFloat("Property Value " + (i + 1)));
        }
        HashMap<String, Number> map = new HashMap<>();
        for (int i = 0; i < size; i++) {
            map.put(propertyNames.get(i), propertyValues.get(i));
        }
        return new AbilityModifier(map, nbt.getUniqueId("ID"));
    }

    public void addProperties(HashMap<String, Number> properties) {
        this.properties.putAll(properties);
        this.propertyNames.addAll(properties.keySet());
        this.propertyValues.addAll(properties.values());
    }

    public void removeProperty(String property) {
        Number num = properties.get(property);
        this.properties.remove(property);
        this.propertyNames.remove(property);
        this.propertyValues.remove(num);
    }

    public void addProperty(String propertyName, Number propertyValue) {
        this.properties.put(propertyName, propertyValue);
        this.propertyNames.add(propertyName);
        this.propertyValues.add(propertyValue);
    }

    public void clearPropertyList() {
        this.properties.clear();
    }

    public void clearProperties() {
        this.properties.clear();
        this.propertyNames.clear();
        this.propertyValues.clear();
    }

    public boolean hasProperty(String property) {
        return this.properties.containsKey(property);
    }

    public Number getProperty(String property) {
        return this.properties.getOrDefault(property, 1);
    }

    public UUID getID() {
        return this.id;
    }

    public void setID(UUID id) {
        this.id = id;
    }

    //Read and write methods for bytes and nbt
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        //Writes the names
        buffer.writeVarInt(propertyNames.size());
        //Copies it, prevents exceptions
        List<String> propertyNames = new ArrayList<>(this.propertyNames);
        if (!propertyNames.isEmpty()) {
            for (String string : propertyNames)
                writeString(buf, string);
        }
        //Writes the numbers
        buffer.writeVarInt(propertyValues.size());
        //Copies it, prevents exceptions
        List<Number> propertyValues = new ArrayList<>(this.propertyValues);
        if (!propertyValues.isEmpty()) {
            for (Number number : propertyValues)
                buffer.writeFloat(number.floatValue());
        }
        buffer.writeUniqueId(getID());
    }

    public AbilityModifier fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);

        //Clear properties
        clearProperties();
        //Names
        int size = buffer.readVarInt();
        List<String> propertyNames = new ArrayList<>();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                propertyNames.add(i, readString(buf));
            }
        }
        //Numbers
        size = buffer.readVarInt();
        List<Number> propertyValues = new ArrayList<>();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                propertyValues.add(i, buffer.readFloat());
            }
        }
        //Properties
        if (!propertyNames.isEmpty() && !propertyValues.isEmpty())
            for (int i = 0; i < propertyNames.size(); i++)
                addProperty(propertyNames.get(i), propertyValues.get(i));
        //ID
        setID(buffer.readUniqueId());
        return this;
    }

    public void toNBT(NBTTagCompound nbt) {
        //Names
        nbt.setInteger("Property Name Size", propertyNames.size());
        for (int i = 0; i < propertyNames.size(); i++)
            nbt.setString("Property Name " + (i + 1), propertyNames.get(i));
        //Values
        nbt.setInteger("Property Value Size", propertyValues.size());
        for (int i = 0; i < propertyValues.size(); i++)
            nbt.setFloat("Property Value " + (i + 1), propertyValues.get(i).floatValue());
        //ID
        nbt.setUniqueId("ID", getID());
    }

    public AbilityModifier fromNBT(NBTTagCompound nbt) {
        //Names
        int size = nbt.getInteger("Property Name Size");
        propertyNames.clear();
        for (int i = 0; i < size; i++) {
            propertyNames.add(i, nbt.getString("Property Name " + (i + 1)));
        }
        //Values
        size = nbt.getInteger("Property Value Size");
        propertyValues.clear();
        for (int i = 0; i < size; i++) {
            propertyValues.add(i, nbt.getFloat("Property Value " + (i + 1)));
        }
        setID(nbt.getUniqueId("ID"));
        return this;
    }

}
