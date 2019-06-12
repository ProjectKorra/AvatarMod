package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.entity.data.LightningSpearBehavior;

import elucent.albedo.event.GatherLightsEvent;
import elucent.albedo.lighting.ILightProvider;
import elucent.albedo.lighting.Light;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author Aang23
 */
@Optional.Interface(iface = "elucent.albedo.lighting.ILightProvider", modid = "albedo")
public class EntityLightOrb extends AvatarEntity implements ILightProvider {

    private static final DataParameter<Float> SYNC_SIZE = EntityDataManager.createKey(EntityLightOrb.class,
            DataSerializers.FLOAT);
    private static final DataParameter<Integer> SYNC_RADIUS = EntityDataManager.createKey(EntityLightOrb.class,
            DataSerializers.VARINT);
    private static final DataParameter<Boolean> SYNC_IS_SPHERE = EntityDataManager.createKey(EntityLightOrb.class,
            DataSerializers.BOOLEAN);
    private static final DataParameter<Float> SYNC_COLOR_R = EntityDataManager.createKey(EntityLightOrb.class,
            DataSerializers.FLOAT);
    private static final DataParameter<Float> SYNC_COLOR_G = EntityDataManager.createKey(EntityLightOrb.class,
            DataSerializers.FLOAT);
    private static final DataParameter<Float> SYNC_COLOR_B = EntityDataManager.createKey(EntityLightOrb.class,
            DataSerializers.FLOAT);
    private static final DataParameter<Float> SYNC_COLOR_A = EntityDataManager.createKey(EntityLightOrb.class,
            DataSerializers.FLOAT);

    public EntityLightOrb(World world) {
        super(world);
        setSize(0.1F, 0.1F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SYNC_SIZE, 2F);
        dataManager.register(SYNC_RADIUS, 20);
        dataManager.register(SYNC_IS_SPHERE, true);
        dataManager.register(SYNC_COLOR_R, 1F);
        dataManager.register(SYNC_COLOR_G, 1F);
        dataManager.register(SYNC_COLOR_B, 1F);
        dataManager.register(SYNC_COLOR_A, 1F);
    }

    public boolean isSphere() {
        return dataManager.get(SYNC_IS_SPHERE);
    }

    public void setIsSphere(boolean value) {
        dataManager.set(SYNC_IS_SPHERE, value);
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
    public void gatherLights(GatherLightsEvent arg0, Entity arg1) {

    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        setOrbSize(nbt.getFloat("OrbSize"));
        setLightRadius(nbt.getInteger("OrbRadius"));
        setIsSphere(nbt.getBoolean("OrbSphere"));
        setColorR(nbt.getFloat("OrbColorR"));
        setColorG(nbt.getFloat("OrbColorG"));
        setColorB(nbt.getFloat("OrbColorB"));
        setColorA(nbt.getFloat("OrbColorA"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setFloat("OrbSize", getOrbSize());
        nbt.setInteger("OrbRadius", getLightRadius());
        nbt.setBoolean("OrbSphere", isSphere());
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
}