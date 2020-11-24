package com.crowsofwar.avatar.entity;

import com.crowsofwar.avatar.entity.data.LightCylinderBehaviour;

import com.zeitheron.hammercore.api.lighting.ColoredLight;
import com.zeitheron.hammercore.api.lighting.impl.IGlowingEntity;
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
@Optional.Interface(iface = "com.zeitheron.hammercore.api.lighting.impl.IGlowingEntity", modid = "hammercore")
public class EntityLightCylinder extends AvatarEntity implements IGlowingEntity {

	private static final DataParameter<String> SYNC_TEXTURE = EntityDataManager.createKey(EntityLightCylinder.class,
			DataSerializers.STRING);
	private static final DataParameter<Integer> SYNC_TYPE = EntityDataManager.createKey(EntityLightCylinder.class,
			DataSerializers.VARINT);
	private static final DataParameter<Float> SYNC_SIZE = EntityDataManager.createKey(EntityLightCylinder.class,
			DataSerializers.FLOAT);
	private static final DataParameter<Integer> SYNC_LIGHT_AMOUNT = EntityDataManager
			.createKey(EntityLightCylinder.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> SYNC_RADIUS = EntityDataManager.createKey(EntityLightCylinder.class,
			DataSerializers.VARINT);
	private static final DataParameter<Float> SYNC_LENGTH = EntityDataManager.createKey(EntityLightCylinder.class,
			DataSerializers.FLOAT);
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
	private static final DataParameter<Boolean> SYNC_SHOULD_SPIN = EntityDataManager.createKey(EntityLightCylinder.class,
			DataSerializers.BOOLEAN);
	private static final DataParameter<Float> SYNC_DEGREES_PER_SECOND = EntityDataManager.createKey(EntityLightCylinder.class,
			DataSerializers.FLOAT);
	private static final DataParameter<LightCylinderBehaviour> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityLightCylinder.class, LightCylinderBehaviour.DATA_SERIALIZER);

	public EntityLightCylinder(World world) {
		super(world);
		setSize(0.1F, 0.1F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_BEHAVIOR, new LightCylinderBehaviour.Idle());
		dataManager.register(SYNC_TEXTURE, "avatarmod:textures/entity/fire-ribbon.png");
		dataManager.register(SYNC_TYPE, EnumType.SQUARE.ordinal());
		dataManager.register(SYNC_SIZE, 1F);
		dataManager.register(SYNC_LIGHT_AMOUNT, 3);
		dataManager.register(SYNC_RADIUS, 10);
		dataManager.register(SYNC_LENGTH, 4F);
		dataManager.register(SYNC_COLOR_R, 1F);
		dataManager.register(SYNC_COLOR_G, 1F);
		dataManager.register(SYNC_COLOR_B, 1F);
		dataManager.register(SYNC_COLOR_A, 1F);
		dataManager.register(SYNC_YAW, 0F);
		dataManager.register(SYNC_PITCH, 0F);
		dataManager.register(SYNC_SHOULD_SPIN, false);
		dataManager.register(SYNC_DEGREES_PER_SECOND, 20F);
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
	public void onUpdate() {
		super.onUpdate();
		setBehaviour((LightCylinderBehaviour) getBehaviour().onUpdate(this));
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setTexture(nbt.getString("CylTexture"));
		setType(EnumType.values()[nbt.getInteger("CylType")]);
		setLightAmount(nbt.getInteger("CylAmount"));
		setCylinderSize(nbt.getFloat("CylSize"));
		setLightRadius(nbt.getInteger("CylRadius"));
		setCylinderLength(nbt.getFloat("CylLength"));
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
		nbt.setInteger("CylType", getType().ordinal());
		nbt.setInteger("CylAmount", getLightAmount());
		nbt.setFloat("CylSize", getCylinderSize());
		nbt.setInteger("CylRadius", getLightRadius());
		nbt.setFloat("CylLength", getCylinderLength());
		nbt.setFloat("CylColorR", getColorR());
		nbt.setFloat("CylColorG", getColorG());
		nbt.setFloat("CylColorB", getColorB());
		nbt.setFloat("CylColorA", getColorA());
		nbt.setFloat("CylYaw", getCylinderYaw());
		nbt.setFloat("CylPitch", getCylinderPitch());
	}

	public float getCylinderLength() {
		return dataManager.get(SYNC_LENGTH);
	}

	public void setCylinderLength(float length) {
		dataManager.set(SYNC_LENGTH, length);
	}

	public float getCylinderSize() {
		return dataManager.get(SYNC_SIZE);
	}

	public void setCylinderSize(float size) {
		dataManager.set(SYNC_SIZE, size);
	}

	public float getCylinderYaw() {
		return dataManager.get(SYNC_YAW);
	}

	public void setCylinderYaw(float value) {
		dataManager.set(SYNC_YAW, value);
	}

	public float getCylinderPitch() {
		return dataManager.get(SYNC_PITCH);
	}

	public void setCylinderPitch(float value) {
		dataManager.set(SYNC_PITCH, value);
	}

	public int getLightRadius() {
		return dataManager.get(SYNC_RADIUS);
	}

	public void setLightRadius(int radius) {
		dataManager.set(SYNC_RADIUS, radius);
	}

	public EnumType getType() {
		return EnumType.values()[dataManager.get(SYNC_TYPE)];
	}

	public void setType(EnumType type) {
		dataManager.set(SYNC_TYPE, type.ordinal());
	}

	public int getLightAmount() {
		return dataManager.get(SYNC_LIGHT_AMOUNT);
	}

	public void setLightAmount(int amount) {
		dataManager.set(SYNC_LIGHT_AMOUNT, amount);
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

	public void setColorR(float value) {
		dataManager.set(SYNC_COLOR_R, value);
	}

	public float getColorG() {
		return dataManager.get(SYNC_COLOR_G);
	}

	public void setColorG(float value) {
		dataManager.set(SYNC_COLOR_G, value);
	}

	public float getColorB() {
		return dataManager.get(SYNC_COLOR_B);
	}

	public void setColorB(float value) {
		dataManager.set(SYNC_COLOR_B, value);
	}

	public float getColorA() {
		return dataManager.get(SYNC_COLOR_A);
	}

	public void setColorA(float value) {
		dataManager.set(SYNC_COLOR_A, value);
	}

	public void setShouldSpin(boolean spin) {
		dataManager.set(SYNC_SHOULD_SPIN, spin);
	}

	public boolean shouldSpin() {
		return dataManager.get(SYNC_SHOULD_SPIN);
	}

	public float getDegreesPerSecond() {
		return dataManager.get(SYNC_DEGREES_PER_SECOND);
	}

	public void setDegreesPerSecond(float degrees) {
		dataManager.set(SYNC_DEGREES_PER_SECOND, degrees);
	}

	public void setBehaviour(LightCylinderBehaviour behaviour) {
		dataManager.set(SYNC_BEHAVIOR, behaviour);
	}

	public LightCylinderBehaviour getBehaviour() {
		return dataManager.get(SYNC_BEHAVIOR);
	}

	@Override
	@Optional.Method(modid = "hammercore")
	public ColoredLight produceColoredLight(float partialTicks) {
		return ColoredLight.builder().pos(this).color(87, 161, 235).radius(10f).build();

	}

	public enum EnumType {
		ROUND, SQUARE
	}
}