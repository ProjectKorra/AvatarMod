package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.entity.data.LightOrbBehavior;

import elucent.albedo.event.GatherLightsEvent;
import elucent.albedo.lighting.ILightProvider;
import elucent.albedo.lighting.Light;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author Aang23
 */
@Optional.Interface(iface = "elucent.albedo.lighting.ILightProvider", modid = "albedo")
public class EntityLightOrb extends AvatarEntity implements ILightProvider {

    private static final DataParameter<LightOrbBehavior> SYNC_BEHAVIOR = EntityDataManager
            .createKey(EntityLightOrb.class, LightOrbBehavior.DATA_SERIALIZER);
    private static final DataParameter<String> SYNC_TEXTURE = EntityDataManager.createKey(EntityLightOrb.class,
            DataSerializers.STRING);
    private static final DataParameter<Integer> SYNC_TYPE = EntityDataManager.createKey(EntityLightOrb.class,
            DataSerializers.VARINT);
    private static final DataParameter<Float> SYNC_SIZE = EntityDataManager.createKey(EntityLightOrb.class,
            DataSerializers.FLOAT);
    private static final DataParameter<Integer> SYNC_RADIUS = EntityDataManager.createKey(EntityLightOrb.class,
            DataSerializers.VARINT);
    private static final DataParameter<Float> SYNC_COLOR_R = EntityDataManager.createKey(EntityLightOrb.class,
            DataSerializers.FLOAT);
    private static final DataParameter<Float> SYNC_COLOR_G = EntityDataManager.createKey(EntityLightOrb.class,
            DataSerializers.FLOAT);
    private static final DataParameter<Float> SYNC_COLOR_B = EntityDataManager.createKey(EntityLightOrb.class,
            DataSerializers.FLOAT);
    private static final DataParameter<Float> SYNC_COLOR_A = EntityDataManager.createKey(EntityLightOrb.class,
            DataSerializers.FLOAT);
    private static final DataParameter<String> SYNC_EMITTING_ENTITY = EntityDataManager.createKey(EntityLightOrb.class,
            DataSerializers.STRING);

    public EntityLightOrb(World world) {
        super(world);
        setSize(0.1F, 0.1F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SYNC_BEHAVIOR, new LightOrbBehavior.Idle());
        dataManager.register(SYNC_TEXTURE, "avatarmod:textures/entity/sphere.png");
        dataManager.register(SYNC_TYPE, EnumType.COLOR_SPHERE.ordinal());
        dataManager.register(SYNC_SIZE, 2F);
        dataManager.register(SYNC_RADIUS, 20);
        dataManager.register(SYNC_COLOR_R, 1F);
        dataManager.register(SYNC_COLOR_G, 1F);
        dataManager.register(SYNC_COLOR_B, 1F);
        dataManager.register(SYNC_COLOR_A, 1F);
        dataManager.register(SYNC_EMITTING_ENTITY, "06256730-5d15-4e0f-b49b-997644ac6f59");
    }

    public LightOrbBehavior getBehavior() {
        return dataManager.get(SYNC_BEHAVIOR);
    }

    public void setBehavior(LightOrbBehavior behavior) {
        dataManager.set(SYNC_BEHAVIOR, behavior);
    }

    public String getTexture() {
        return dataManager.get(SYNC_TEXTURE);
    }

    public void setTexture(String texture) {
        dataManager.set(SYNC_TEXTURE, texture);
    }

    public EnumType getType() {
        return EnumType.values()[dataManager.get(SYNC_TYPE)];
    }

    public void setType(EnumType type) {
        dataManager.set(SYNC_TYPE, type.ordinal());
    }

    public String getEmittingEntity() {
        return dataManager.get(SYNC_EMITTING_ENTITY);
    }

    public void setEmittingEntity(String entityID) {
        dataManager.set(SYNC_EMITTING_ENTITY, entityID);
    }

    /**
     * Sets the Orb's color. RGBA format
     */
    public void setColor(float r, float g, float b, float a) {
        dataManager.set(SYNC_COLOR_R, r);
        dataManager.set(SYNC_COLOR_G, g);
        dataManager.set(SYNC_COLOR_B, b);
        dataManager.set(SYNC_COLOR_A, a);
    }

    /**
     * Sets the light's radius
     * 
     * @param radius
     */
    public void setLightRadius(int radius) {
        dataManager.set(SYNC_RADIUS, radius);
    }

    /**
     * Sets the orb size
     * 
     * @param size
     */
    public void setOrbSize(float size) {
        dataManager.set(SYNC_SIZE, size);
    }

    public int getLightRadius() {
        return dataManager.get(SYNC_RADIUS);
    }

    public float getOrbSize() {
        return dataManager.get(SYNC_SIZE);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        setBehavior((LightOrbBehavior) getBehavior().onUpdate(this));
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        return true;
    }

    @Override
    @Optional.Method(modid = "albedo")
    public Light provideLight() {
        return Light.builder().pos(this).color(getColorR(), getColorG(), getColorB()).radius(getLightRadius()).build();
    }

    @Override
    @Optional.Method(modid = "albedo")
    public void gatherLights(GatherLightsEvent event, Entity entity) {

    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        setTexture(nbt.getString("OrbTexture"));
        setType(EnumType.values()[nbt.getInteger("OrbType")]);
        setOrbSize(nbt.getFloat("OrbSize"));
        setLightRadius(nbt.getInteger("OrbRadius"));
        setColorR(nbt.getFloat("OrbColorR"));
        setColorG(nbt.getFloat("OrbColorG"));
        setColorB(nbt.getFloat("OrbColorB"));
        setColorA(nbt.getFloat("OrbColorA"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setString("OrbTexture", getTexture());
        nbt.setInteger("OrbType", getType().ordinal());
        nbt.setFloat("OrbSize", getOrbSize());
        nbt.setInteger("OrbRadius", getLightRadius());
        nbt.setFloat("OrbColorR", getColorR());
        nbt.setFloat("OrbColorG", getColorG());
        nbt.setFloat("OrbColorB", getColorB());
        nbt.setFloat("OrbColorA", getColorA());
    }

    public float getColorR() {
        return dataManager.get(SYNC_COLOR_R);
    }

    public float getColorG() {
        return dataManager.get(SYNC_COLOR_G);
    }

    public float getColorB() {
        return dataManager.get(SYNC_COLOR_B);
    }

    public float getColorA() {
        return dataManager.get(SYNC_COLOR_A);
    }

    public void setColorR(float value) {
        dataManager.set(SYNC_COLOR_R, value);
    }

    public void setColorG(float value) {
        dataManager.set(SYNC_COLOR_G, value);
    }

    public void setColorB(float value) {
        dataManager.set(SYNC_COLOR_B, value);
    }

    public void setColorA(float value) {
        dataManager.set(SYNC_COLOR_A, value);
    }

    public boolean shouldUseCustomTexture() {
        return getType() == EnumType.TEXTURE_CUBE || getType() == EnumType.TEXTURE_SPHERE;
    }

    public boolean isColorSphere() {
        return getType() == EnumType.COLOR_SPHERE;
    }

    public boolean isTextureSphere() {
        return getType() == EnumType.TEXTURE_SPHERE;
    }

    public boolean isSphere() {
        return isTextureSphere() || isColorSphere();
    }

    public enum EnumType {
        COLOR_SPHERE, COLOR_CUBE, TEXTURE_SPHERE, TEXTURE_CUBE
    }
}