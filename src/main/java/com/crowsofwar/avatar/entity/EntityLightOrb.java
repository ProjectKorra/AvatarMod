package com.crowsofwar.avatar.entity;

import com.crowsofwar.avatar.entity.data.Behavior;
import com.crowsofwar.avatar.entity.data.LightOrbBehavior;
import com.zeitheron.hammercore.api.lighting.ColoredLight;
import com.zeitheron.hammercore.api.lighting.impl.IGlowingEntity;
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
@Optional.Interface(iface = "com.zeitheron.hammercore.api.lighting.impl.IGlowingEntity", modid = "hammercore")
public class EntityLightOrb extends AvatarEntity  implements IGlowingEntity {

	private static final DataParameter<LightOrbBehavior> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityLightOrb.class, LightOrbBehavior.DATA_SERIALIZER);
	private static final DataParameter<String> SYNC_TEXTURE = EntityDataManager.createKey(EntityLightOrb.class,
			DataSerializers.STRING);
	private static final DataParameter<Integer> SYNC_ANIMATED_TEXTURE_FRAMES_COUNT = EntityDataManager.createKey(EntityLightOrb.class,
			DataSerializers.VARINT);
	private static final DataParameter<Integer> SYNC_TYPE = EntityDataManager.createKey(EntityLightOrb.class,
			DataSerializers.VARINT);
	private static final DataParameter<Float> SYNC_SIZE = EntityDataManager.createKey(EntityLightOrb.class,
			DataSerializers.FLOAT);
	private static final DataParameter<Float> SYNC_INITIAL_SIZE = EntityDataManager.createKey(EntityLightOrb.class,
			DataSerializers.FLOAT);
	private static final DataParameter<Integer> SYNC_RADIUS = EntityDataManager.createKey(EntityLightOrb.class,
			DataSerializers.VARINT);
	private static final DataParameter<Float> SYNC_COLOUR_R = EntityDataManager.createKey(EntityLightOrb.class,
			DataSerializers.FLOAT);
	private static final DataParameter<Float> SYNC_COLOUR_G = EntityDataManager.createKey(EntityLightOrb.class,
			DataSerializers.FLOAT);
	private static final DataParameter<Float> SYNC_COLOUR_B = EntityDataManager.createKey(EntityLightOrb.class,
			DataSerializers.FLOAT);
	private static final DataParameter<Float> SYNC_COLOUR_A = EntityDataManager.createKey(EntityLightOrb.class,
			DataSerializers.FLOAT);
	private static final DataParameter<Float> SYNC_INITIAL_COLOUR_R = EntityDataManager.createKey(EntityLightOrb.class,
			DataSerializers.FLOAT);
	private static final DataParameter<Float> SYNC_INITIAL_COLOUR_G = EntityDataManager.createKey(EntityLightOrb.class,
			DataSerializers.FLOAT);
	private static final DataParameter<Float> SYNC_INITIAL_COLOUR_B = EntityDataManager.createKey(EntityLightOrb.class,
			DataSerializers.FLOAT);
	private static final DataParameter<Float> SYNC_INITIAL_COLOUR_A = EntityDataManager.createKey(EntityLightOrb.class,
			DataSerializers.FLOAT);
	private static final DataParameter<String> SYNC_EMITTING_ENTITY = EntityDataManager.createKey(EntityLightOrb.class,
			DataSerializers.STRING);
	private static final DataParameter<Float> SYNC_COLOUR_RANGE = EntityDataManager.createKey(EntityLightOrb.class,
			DataSerializers.FLOAT);
	private static final DataParameter<Float> SYNC_COLOUR_INTERVAL = EntityDataManager.createKey(EntityLightOrb.class,
			DataSerializers.FLOAT);
	private static final DataParameter<Boolean> SYNC_SPINNING = EntityDataManager.createKey(EntityLightOrb.class,
			DataSerializers.BOOLEAN);

	int ticks = 1;
	private int lifeTime = -1;

	public EntityLightOrb(World world) {
		super(world);
		setSize(0.1F, 0.1F);
		noClip = true;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_BEHAVIOR, new LightOrbBehavior.Idle());
		dataManager.register(SYNC_TEXTURE, "avatarmod:textures/entity/sphere.png");
		dataManager.register(SYNC_ANIMATED_TEXTURE_FRAMES_COUNT, 0);
		dataManager.register(SYNC_TYPE, EnumType.COLOR_SPHERE.ordinal());
		dataManager.register(SYNC_SIZE, 2F);
		dataManager.register(SYNC_INITIAL_SIZE, 2F);
		dataManager.register(SYNC_RADIUS, 20);
		dataManager.register(SYNC_COLOUR_R, 1F);
		dataManager.register(SYNC_COLOUR_G, 1F);
		dataManager.register(SYNC_COLOUR_B, 1F);
		dataManager.register(SYNC_COLOUR_A, 1F);
		dataManager.register(SYNC_INITIAL_COLOUR_R, 1F);
		dataManager.register(SYNC_INITIAL_COLOUR_G, 1F);
		dataManager.register(SYNC_INITIAL_COLOUR_B, 1F);
		dataManager.register(SYNC_INITIAL_COLOUR_A, 1F);
		dataManager.register(SYNC_COLOUR_RANGE, 0.1F);
		dataManager.register(SYNC_COLOUR_INTERVAL, 0.025F);
		dataManager.register(SYNC_SPINNING, false);
		//Random string UUID
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

	public String getTrueTexture() {
		if (getTextureFrameCount() > 0) return getTexture().replace("%number%", String.valueOf(ticks));
		else return dataManager.get(SYNC_TEXTURE);
	}

	public int getTextureFrameCount() {
		return dataManager.get(SYNC_ANIMATED_TEXTURE_FRAMES_COUNT);
	}

	public int getCurrentTextureNumber() {
		return ticks;
	}

	/**
	 * Using 0 uses a normal texture. if animated,
	 * put %number% where you want the frame number to change
	 *
	 * @param value
	 */
	public void setTextureFrameCount(int value) {
		dataManager.set(SYNC_ANIMATED_TEXTURE_FRAMES_COUNT, value);
	}

	public EnumType getType() {
		return EnumType.values()[dataManager.get(SYNC_TYPE)];
	}

	public void setType(EnumType type) {
		dataManager.set(SYNC_TYPE, type.ordinal());
	}

	//Will fix this later
	public Entity getEmittingEntity() {
		return getOwner();
	}

	public void setEmittingEntity(Entity entity) {
		dataManager.set(SYNC_EMITTING_ENTITY, entity.getUniqueID().toString());
	}

	public float getColourShiftRange() {
		return dataManager.get(SYNC_COLOUR_RANGE);
	}

	public void setColourShiftRange(float range) {
		dataManager.set(SYNC_COLOUR_RANGE, range);
	}

	public float getColourShiftInterval() {
		return dataManager.get(SYNC_COLOUR_INTERVAL);
	}

	public void setColourShiftInterval(float interval) {
		dataManager.set(SYNC_COLOUR_INTERVAL, interval);
	}

	//Set to -1 for it not to have a lifetime (for stuff like fireball, where it should exist as long as the fireball exists
	public void setLifeTime(int ticks) {
		this.lifeTime = ticks;
	}

	/**
	 * Sets the Orb's color. RGBA format
	 */
	public void setColor(float r, float g, float b, float a) {
		dataManager.set(SYNC_COLOUR_R, r);
		dataManager.set(SYNC_COLOUR_G, g);
		dataManager.set(SYNC_COLOUR_B, b);
		dataManager.set(SYNC_COLOUR_A, a);
		dataManager.set(SYNC_INITIAL_COLOUR_R, r);
		dataManager.set(SYNC_INITIAL_COLOUR_G, g);
		dataManager.set(SYNC_INITIAL_COLOUR_B, b);
		dataManager.set(SYNC_INITIAL_COLOUR_A, a);
	}

	public int getLightRadius() {
		return dataManager.get(SYNC_RADIUS);
	}

	/**
	 * Sets the light's radius
	 *
	 * @param radius
	 */
	public void setLightRadius(int radius) {
		dataManager.set(SYNC_RADIUS, radius);
	}

	public float getOrbSize() {
		return dataManager.get(SYNC_SIZE);
	}

	public void setSpinning(boolean spinning) {
		dataManager.set(SYNC_SPINNING, spinning);
	}

	public boolean isSpinning() {
		return dataManager.get(SYNC_SPINNING);
	}
	/**
	 * Sets the orb size
	 *
	 * @param size
	 */
	public void setOrbSize(float size) {
		dataManager.set(SYNC_SIZE, size);
	}

	public void setInitialSize(float size) {
		dataManager.set(SYNC_INITIAL_SIZE, size);
	}

	public float getInitialSize() {
		return dataManager.get(SYNC_INITIAL_SIZE);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		setBehavior((LightOrbBehavior) getBehavior().onUpdate(this));

		if (world.isRemote && getTextureFrameCount() > 0) {
			ticks++;
			if (ticks == getTextureFrameCount()) ticks = 1;
		}
		if (!world.isRemote && ticksExisted > lifeTime && lifeTime != -1) {
			setDead();
		}
		if (getBehavior() instanceof LightOrbBehavior.FollowEntity && getEmittingEntity() == null) {
			setDead();
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public boolean canBeAttackedWithItem() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance) {
		return true;
	}



	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setTexture(nbt.getString("OrbTexture"));
		setType(EnumType.values()[nbt.getInteger("OrbType")]);
		setTextureFrameCount(nbt.getInteger("FrameCount"));
		setOrbSize(nbt.getFloat("OrbSize"));
		setLightRadius(nbt.getInteger("OrbRadius"));
		setColorR(nbt.getFloat("OrbColorR"));
		setColorG(nbt.getFloat("OrbColorG"));
		setColorB(nbt.getFloat("OrbColorB"));
		setColorA(nbt.getFloat("OrbColorA"));
		setBehavior((LightOrbBehavior) Behavior.lookup(nbt.getInteger("Behaviour"), this));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setString("OrbTexture", getTexture());
		nbt.setInteger("OrbType", getType().ordinal());
		nbt.setInteger("FrameCount", getTextureFrameCount());
		nbt.setFloat("OrbSize", getOrbSize());
		nbt.setInteger("OrbRadius", getLightRadius());
		nbt.setFloat("OrbColorR", getColorR());
		nbt.setFloat("OrbColorG", getColorG());
		nbt.setFloat("OrbColorB", getColorB());
		nbt.setFloat("OrbColorA", getColorA());
		nbt.setInteger("Behaviour", getBehavior().getId());
	}

	public float getColorR() {
		return dataManager.get(SYNC_COLOUR_R);
	}

	public void setColorR(float value) {
		dataManager.set(SYNC_COLOUR_R, value);
	}

	public float getColorG() {
		return dataManager.get(SYNC_COLOUR_G);
	}

	public void setColorG(float value) {
		dataManager.set(SYNC_COLOUR_G, value);
	}

	public float getColorB() {
		return dataManager.get(SYNC_COLOUR_B);
	}

	public void setColorB(float value) {
		dataManager.set(SYNC_COLOUR_B, value);
	}

	public float getColorA() {
		return dataManager.get(SYNC_COLOUR_A);
	}

	public void setColorA(float value) {
		dataManager.set(SYNC_COLOUR_A, value);
	}

	public float getInitialColourR() {
		return dataManager.get(SYNC_INITIAL_COLOUR_R);
	}
	public float getInitialColourG() {
		return dataManager.get(SYNC_INITIAL_COLOUR_G);
	}
	public float getInitialColourB() {
		return dataManager.get(SYNC_INITIAL_COLOUR_B);
	}
	public float getInitialColourA() {
		return dataManager.get(SYNC_INITIAL_COLOUR_A);
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

	@Override
	@Optional.Method(modid = "hammercore")
	public ColoredLight produceColoredLight(float partialTicks) {
		return ColoredLight.builder().pos(this).color(getColorR(), getColorG(), getColorB()).radius(getLightRadius()).build();
	}

	public enum EnumType {
		COLOR_SPHERE, COLOR_CUBE, TEXTURE_SPHERE, TEXTURE_CUBE, NOTHING
	}
}