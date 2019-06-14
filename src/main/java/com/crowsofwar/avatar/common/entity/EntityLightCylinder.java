package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.gorecore.util.Vector;

import elucent.albedo.event.GatherLightsEvent;
import elucent.albedo.lighting.ILightProvider;
import elucent.albedo.lighting.Light;
import elucent.albedo.lighting.LightManager;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author Aang23
 */
@Optional.Interface(iface = "elucent.albedo.lighting.ILightProvider", modid = "albedo")
public class EntityLightCylinder extends AvatarEntity implements ILightProvider {

    private static final DataParameter<String> SYNC_TEXTURE = EntityDataManager.createKey(EntityLightCylinder.class,
            DataSerializers.STRING);
    private static final DataParameter<Float> SYNC_SIZE = EntityDataManager.createKey(EntityLightCylinder.class,
            DataSerializers.FLOAT);
    private static final DataParameter<Integer> SYNC_LIGHT_AMOUNT = EntityDataManager
            .createKey(EntityLightCylinder.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> SYNC_RADIUS = EntityDataManager.createKey(EntityLightCylinder.class,
            DataSerializers.VARINT);
    private static final DataParameter<Integer> SYNC_LENGHT = EntityDataManager.createKey(EntityLightCylinder.class,
            DataSerializers.VARINT);
    private static final DataParameter<Float> SYNC_COLOR_R = EntityDataManager.createKey(EntityLightCylinder.class,
            DataSerializers.FLOAT);
    private static final DataParameter<Float> SYNC_COLOR_G = EntityDataManager.createKey(EntityLightCylinder.class,
            DataSerializers.FLOAT);
    private static final DataParameter<Float> SYNC_COLOR_B = EntityDataManager.createKey(EntityLightCylinder.class,
            DataSerializers.FLOAT);
    private static final DataParameter<Float> SYNC_COLOR_A = EntityDataManager.createKey(EntityLightCylinder.class,
            DataSerializers.FLOAT);
    private static final DataParameter<Float> SYNC_YAW = EntityDataManager.createKey(EntityLightCylinder.class,
            DataSerializers.FLOAT);
    private static final DataParameter<Float> SYNC_PITCH = EntityDataManager.createKey(EntityLightCylinder.class,
            DataSerializers.FLOAT);

    public EntityLightCylinder(World world) {
        super(world);
        setSize(0.1F, 0.1F);
        // TODO sync rotation
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SYNC_TEXTURE, "avatarmod:textures/entity/fire-ribbon.png");
        dataManager.register(SYNC_SIZE, 1F);
        dataManager.register(SYNC_LIGHT_AMOUNT, 3);
        dataManager.register(SYNC_RADIUS, 10);
        dataManager.register(SYNC_LENGHT, 4);
        dataManager.register(SYNC_COLOR_R, 1F);
        dataManager.register(SYNC_COLOR_G, 1F);
        dataManager.register(SYNC_COLOR_B, 1F);
        dataManager.register(SYNC_COLOR_A, 1F);
        dataManager.register(SYNC_YAW, 0F);
        dataManager.register(SYNC_PITCH, 0F);
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
        for (int i = 0; i < getCylinderLenght(); i++) {
            if (i % (getCylinderLenght() / getLightAmount()) == 0) {
                Vector end = new Vector(posX, posY, posZ).plus(new Vector(this.getLookVec()).times(i * (0.3 * 2)));
                LightManager.lights.add(Light.builder().pos(end.toBlockPos())
                        .color(getColorR(), getColorG(), getColorB()).radius(getLightRadius()).build());
            }
        }
        return Light.builder().pos(this).color(getColorR(), getColorG(), getColorB()).radius(10).build();
    }

    @Override
    @Optional.Method(modid = "albedo")
    public void gatherLights(GatherLightsEvent event, Entity entity) {

    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        setTexture(nbt.getString("CylTexture"));
        setLightAmount(nbt.getInteger("CylAmount"));
        setCylinderSize(nbt.getFloat("CylSize"));
        setLightRadius(nbt.getInteger("CylRadius"));
        setCylinderLenght(nbt.getInteger("CylLength"));
        setColorR(nbt.getFloat("CylColorR"));
        setColorG(nbt.getFloat("CylColorG"));
        setColorB(nbt.getFloat("CylColorB"));
        setColorA(nbt.getFloat("CylColorA"));
        setCylinderYaw(nbt.getFloat("CylYaw"));
        setCylinderPitch(nbt.getFloat("CylPitch"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setString("CylTexture", getTexture());
        nbt.setInteger("CylAmount", getLightAmount());
        nbt.setFloat("CylSize", getCylinderSize());
        nbt.setInteger("CylRadius", getLightRadius());
        nbt.setInteger("CylLength", getCylinderLenght());
        nbt.setFloat("CylColorR", getColorR());
        nbt.setFloat("CylColorG", getColorG());
        nbt.setFloat("CylColorB", getColorB());
        nbt.setFloat("CylColorA", getColorA());
        nbt.setFloat("CylYaw", getCylinderYaw());
        nbt.setFloat("CylPitch", getCylinderPitch());
    }

    public void setCylinderLenght(int length) {
        dataManager.set(SYNC_LENGHT, length);
    }

    public int getCylinderLenght() {
        return dataManager.get(SYNC_LENGHT);
    }

    public void setCylinderSize(float size) {
        dataManager.set(SYNC_SIZE, size);
    }

    public float getCylinderSize() {
        return dataManager.get(SYNC_SIZE);
    }

    public void setCylinderYaw(float value) {
        dataManager.set(SYNC_YAW, value);
    }

    public float getCylinderYaw() {
        return dataManager.get(SYNC_YAW);
    }

    public void setCylinderPitch(float value) {
        dataManager.set(SYNC_PITCH, value);
    }

    public float getCylinderPitch() {
        return dataManager.get(SYNC_PITCH);
    }

    public void setLightRadius(int radius) {
        dataManager.set(SYNC_RADIUS, radius);
    }

    public int getLightRadius() {
        return dataManager.get(SYNC_RADIUS);
    }

    public void setLightAmount(int amount) {
        dataManager.set(SYNC_LIGHT_AMOUNT, amount);
    }

    public int getLightAmount() {
        return dataManager.get(SYNC_LIGHT_AMOUNT);
    }

    public String getTexture() {
        return dataManager.get(SYNC_TEXTURE);
    }

    public void setTexture(String texture) {
        dataManager.set(SYNC_TEXTURE, texture);
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