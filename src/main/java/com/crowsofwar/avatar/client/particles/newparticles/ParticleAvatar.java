package com.crowsofwar.avatar.client.particles.newparticles;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.network.AvatarClientProxy;
import com.crowsofwar.avatar.client.particles.newparticles.behaviour.ParticleAvatarBehaviour;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.entity.*;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Abstract superclass for all of wizardry's particles. This replaces {@code ParticleCustomTexture} (the functionality of
 * which is no longer necessary since wizardry now uses {@code TextureAtlasSprite}s to do the rendering), and fits into
 * {@code ParticleBuilder} by exposing all the necessary variables through getters, allowing them to be set on the fly
 * rather than needing to be passed into the  constructor.
 * <p></p>
 * The new system is as follows:
 * <p></p>
 * - All particle classes have a single constructor which takes a world and a position only.<br>
 * - Each particle class defines any relevant default values in its constructor, including velocity.<br>
 * - The particle builder then overwrites any other values that were set during building.
 * <p></p>
 * This beauty of this system is that there are never any redundant parameters when spawning particles, since you can set
 * as many or as few parameters as necessary - and in addition, common defaults don't need setting at all. For example,
 * snow particles nearly always fall at the same speed, which can now be defined in the particle class and no longer
 * needs to be defined when spawning the particle - but importantly, it can still be overridden if desired.
 *
 * @author Electroblob, modified by FavouritDragon and Aang23
 * @see com.crowsofwar.avatar.client.particle.ParticleBuilder ParticleBuilder
 * @since AvatarMod 1.6.0
 */
//@SideOnly(Side.CLIENT)
public abstract class ParticleAvatar extends Particle {

	//TODO: How to adjust Electroblob's particle system for av2.
	/**
	 * The fraction of the impact velocity that should be the maximum spread speed added on impact.
	 */
	private static final double SPREAD_FACTOR = 0.2;
	/**
	 * Lateral velocity is reduced by this factor on impact, before adding random spread velocity.
	 */
	private static final double IMPACT_FRICTION = 0.2;
	/**
	 * Implementation of animated particles using the TextureAtlasSprite system. Why vanilla doesn't support this I
	 * don't know, considering it too has animated particles.
	 */
	protected final TextureAtlasSprite[] sprites;
	/**
	 * A long value used by the renderer as a random number seed, ensuring anything that is randomised remains the
	 * same across multiple frames. For example, lightning particles use this to keep their shape across ticks.
	 * This value can also be set during particle creation, allowing users to keep randomised properties the same
	 * even across multiple particles. If unspecified, the seed is chosen at random.
	 */
	protected long seed;
	/**
	 * This particle's random number generator. All particles should use this in preference to any other random
	 * instance (like random), even if it isn't actually necessary to keep properties across frames. Note that
	 * if you <b>do</b> need to generate the same sequence of random numbers each frame, you must call
	 * {@code random.setSeed(seed)} from the {@link ParticleAvatar#renderParticle(BufferBuilder, Entity, float, float, float, float, float, float)}
	 * method - this is not done automatically.
	 */
	protected Random random = new Random(); // If we're not using a seed, this defaults to any old seed
	/**
	 * True if the particle is shaded, false if the particle always renders at full brightness. Defaults to false.
	 */
	protected boolean shaded = false;
	protected float initialRed;
	protected float initialGreen;
	protected float initialBlue;
	protected float fadeRed = 0;
	protected float fadeGreen = 0;
	protected float fadeBlue = 0;
	public float angle;
	protected double radius = 0;
	protected double speed = 0;
	protected UUID uuid = UUID.fromString("ccc7dd56-8fcc-4477-9782-7f0423e5616d");
	protected BendingStyle element;
	protected Ability ability;
	//For flash particles
	protected boolean glow, sparkle;
	//If the particle expands to a max size, like Flash, this makes it expand faster.
	protected float expansionRate;
	//Has R, G, B, and A, in that order.
	protected float[] colourShiftRange = new float[4];
	//Eventually will be used for better colour shifting (either shift up and down the spectrum or shift randomly)
	protected boolean shiftRandomly = false;
	protected float[] colourShiftInterval = new float[4];
	//Currently not changed by methods.
	protected boolean scaleOnUpdate;
	protected double scaleChange;
	protected boolean speedChangeOnUpdate;
	protected double speedChange;
	public double ticksExisted;
	/**
	 * The entity this particle is linked to. The particle will move with this entity.
	 */
	@Nullable
	protected Entity entity = null;
	/**
	 * The entity that spawned the particle. Used for collision detection and nothing else. Setting the normal entity will set this.
	 */
	@Nullable
	protected Entity spawnEntity;
	/**
	 * Coordinates of this particle relative to the linked entity. If the linked entity is null, these are used as
	 * the absolute coordinates of the centre of rotation for particles with spin. If the particle has neither a
	 * linked entity nor spin, these are not used.
	 */
	protected double relativeX, relativeY, relativeZ;
	/**
	 * Velocity of this particle relative to the linked entity. If the linked entity is null, these are not used.
	 */
	protected double relativeMotionX, relativeMotionY, relativeMotionZ;
	/**
	 * The yaw angle this particle is facing, or {@code NaN} if this particle always faces the viewer (default behaviour).
	 */
	protected float yaw = Float.NaN;
	// Note that roll (equivalent to rotating the texture) is effectively handled by particleAngle - although that is
	// actually the rotation speed and not the angle itself.
	/**
	 * The pitch angle this particle is facing, or {@code NaN} if this particle always faces the viewer (default behaviour).
	 */
	protected float pitch = Float.NaN;
	/**
	 * ParticleBuilder adjustments:
	 * -More spawning options. This includes colour shifting for particles, custom behaviour, ability to set the alpha, e.t.c.
	 * -Better spinning. This means that you'll be able to spin particles along an axis- yay!!!!
	 * -Particle AI!!!
	 * <p>
	 * Particles:
	 * -ParticleSphere: Alpha setting!
	 * -ParticleMagicBubble: Actually spins! Behaviour so it can function like MagicFire!
	 * -More particles in general! Namely, cube, sphere, cylinder, rectangular prism, and water particles!
	 */


	private ParticleAvatarBehaviour behaviour;
	private boolean collidedWithSolid, collidedWithParticle = false;
	private boolean dynamicCollidedWithEntity;
	/**
	 * Previous-tick velocity, used in collision detection.
	 */
	private double prevVelX, prevVelY, prevVelZ;

	/**
	 * Creates a new particle in the given world at the given position. All other parameters are set via the various
	 * setter methods ({@link com.crowsofwar.avatar.client.particle.ParticleBuilder ParticleBuilder} deals with all of that anyway).
	 *
	 * @param world    The world in which to create the particle.
	 * @param x        The x-coordinate at which to create the particle.
	 * @param y        The y-coordinate at which to create the particle.
	 * @param z        The z-coordinate at which to create the particle.
	 * @param textures One or more {@code ResourceLocation}s representing the texture(s) used by this particle. These
	 *                 <b>must</b> be registered as {@link TextureAtlasSprite}s using {@link TextureStitchEvent} or the textures will be
	 *                 missing. If more than one {@code ResourceLocation} is specified, the particle will be animated with each texture
	 *                 shown in order for an equal proportion of the particle's lifetime. If this argument is omitted (or a zero-length
	 *                 array is given), the particle will use the vanilla system instead (based on the X/Y texture indices).
	 */
	public ParticleAvatar(World world, double x, double y, double z, ResourceLocation... textures) {

		super(world, x, y, z);

		// Sets the relative coordinates in case they are needed
		this.relativeX = x;
		this.relativeY = y;
		this.relativeZ = z;

		// Deals with the textures
		if (textures.length > 0) {

			sprites = Arrays.stream(textures).map(t -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(
					t.toString())).collect(Collectors.toList()).toArray(new TextureAtlasSprite[0]);

			this.setParticleTexture(sprites[0]);

		} else {
			sprites = new TextureAtlasSprite[0];
		}
	}

	// ============================================== Parameter Setters ==============================================

	// Setters for parameters that affect all particles - these are implemented in this class (although they may be
	// reimplemented in subclasses)

	/**
	 * Static helper method that generates an array of n ResourceLocations using the particle file naming convention,
	 * which is the given stem plus an underscore plus the integer index.
	 */
	public static ResourceLocation[] generateTextures(String stem, int n) {

		ResourceLocation[] textures = new ResourceLocation[n];

		for (int i = 0; i < n; i++) {
			textures[i] = new ResourceLocation(AvatarInfo.MOD_ID, "particles/newparticles/" + stem + "_" + i);
		}

		return textures;
	}

	/**
	 * Static helper method that generates a 2D m x n array of ResourceLocations using the particle file naming
	 * convention, which is the given stem plus an underscore plus the first index, plus an underscore plus the second
	 * index. Useful for animated particles that also pick a random animation strip.
	 */
	public static ResourceLocation[][] generateTextures(String stem, int m, int n) {

		ResourceLocation[][] textures = new ResourceLocation[m][n];

		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				textures[i][j] = new ResourceLocation(AvatarInfo.MOD_ID, "particles/newparticles/" + stem + "_" + i + "_" + j);
			}
		}

		return textures;
	}

	/**
	 * Associates the given {@link ResourceLocation} with the given {@link IAvatarParticleFactory}, allowing it to
	 * be used in the {@link com.crowsofwar.avatar.client.particle.ParticleBuilder ParticleBuilder}. This is a similar concept to
	 * registering entity renderers, in that it associates the client-only bit with its common-code counterpart - but
	 * of course, particles are client-side only so a simple identifier is all that is necessary. As with entity
	 * renderers, <b>this method may only be called from the client side</b>, probably a client proxy.
	 *
	 * @param name    The {@link ResourceLocation} to use for the particle. This effectively replaces the particle type
	 *                enum from previous versions. Keep a reference to this somewhere in <b>common</b> code for use later.
	 * @param factory A {@link IAvatarParticleFactory} that produces your particle. A constructor reference is usually
	 *                sufficient.
	 */
	public static void registerParticle(ResourceLocation name, IAvatarParticleFactory factory) {
		AvatarClientProxy.addParticleFactory(name, factory);
	}

	public void setGlowing(boolean glow) {
		this.glow = glow;
	}

	public void setSparkle(boolean sparkle) {
		this.sparkle = sparkle;
	}

	public long getSeed() {
		return this.seed;
	}

	/**
	 * Sets the seed for this particle's randomly generated values and resets {@link ParticleAvatar#random} to use
	 * that seed. Implementations will differ between particle types; for example, ParticleLightning has an update
	 * period which changes the seed every few ticks, whereas ParticleVine simply retains the same seed for its entire
	 * lifetime.
	 */
	public void setSeed(long seed) {
		this.seed = seed;
		this.random = new Random(seed);
	}

	public UUID getUUID() {
		return this.uuid;
	}

	/**
	 * Sets the UUID for the particle. Good for distinguishing individual particles, especially if you're storing it in a list.
	 */
	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}

	//Used for collision
	public Vec3d getVelocity() {
		return new Vec3d(motionX, motionY, motionZ);
	}

	/**
	 * Returns the entity that spawned it.
	 */
	public Entity getEntity() {
		return spawnEntity;
	}

	/**
	 * Links this particle to the given entity. This will cause its position and velocity to be relative to the entity.
	 *
	 * @param entity The entity to link to.
	 */
	public void setEntity(Entity entity) {
		this.entity = entity;
		// Set these to the correct values
		if (entity != null) {
			this.setPosition(this.entity.posX + relativeX, this.entity.getEntityBoundingBox().minY
					+ relativeY, this.entity.posZ + relativeZ);
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			// Set these to the correct values
			this.relativeMotionX = motionX;
			this.relativeMotionY = motionY;
			this.relativeMotionZ = motionZ;
		}
	}

	/**
	 * Sets whether the particle should render at full brightness or not. True if the particle is shaded, false if
	 * the particle always renders at full brightness. Defaults to false.
	 */
	public void setShaded(boolean shaded) {
		this.shaded = shaded;
	}

	/**
	 * Sets this particle's gravity. True to enable gravity, false to disable. Defaults to false.
	 */
	public void setGravity(boolean gravity) {
		this.particleGravity = gravity ? 1 : 0;
	}

	/**
	 * Sets this particle's collisions. True to enable block collisions, false to disable. Defaults to false.
	 */
	public void setCollisions(boolean canCollide) {
		this.canCollide = canCollide;
	}

	//Sets the element of the particle, which is used for different effects, depending on the particle.
	public void setElement(BendingStyle element) {
		this.element = element;
	}

	public Ability getAbility() {
		return this.ability;
	}

	// Overridden to set the initial colour values

	public void setAbility(Ability ability) {
		this.ability = ability;
	}

	/**
	 * Sets the velocity of the particle.
	 *
	 * @param vx The x velocity
	 * @param vy The y velocity
	 * @param vz The z velocity
	 */
	public void setVelocity(double vx, double vy, double vz) {
		this.motionX = vx;
		this.motionY = vy;
		this.motionZ = vz;
	}

	public void setParticleAngle(float angle) {
		this.particleAngle = angle;
	}

	/**
	 * Sets the spin parameters of the particle.
	 *
	 * @param radius The spin radius
	 * @param speed  The spin speed in rotations per tick
	 */
	public void setSpin(double radius, double speed) {
		this.radius = radius;
		this.speed = speed * 2 * Math.PI; // Converts rotations per tick into radians per tick for the trig functions
		this.angle = this.rand.nextFloat() * (float) Math.PI * 2; // Random start angle
		// Need to set the start position or the circle won't be centred on the correct position
		this.posX = relativeX - radius * MathHelper.cos(angle);
		this.posZ = relativeZ + radius * MathHelper.sin(angle);
		// Set these to the correct values
		this.relativeMotionX = motionX;
		this.relativeMotionY = motionY;
		this.relativeMotionZ = motionZ;
	}

	//Sets the spawn entity of the particle. Used for particle collision.
	public void setSpawnEntity(Entity entity) {
		this.spawnEntity = entity;
	}

	public Entity getSpawnEntity() {
		return this.spawnEntity;
	}


	// Setters for parameters that only affect some particles - these are unimplemented in this class because they
	// doesn't make sense for most particles


	/**
	 * Sets the base colour of the particle. <i>Note that this also sets the fade colour so that particles without a
	 * fade colour do not change colour at all; as such fade colour must be set <b>after</b> calling this method.</i>
	 *
	 * @param r The red colour component
	 * @param g The green colour component
	 * @param b The blue colour component
	 */
	@Override
	public void setRBGColorF(float r, float g, float b) {
		super.setRBGColorF(r, g, b);
		initialRed = r;
		initialGreen = g;
		initialBlue = b;
		// If fade colour is not specified, it defaults to the main colour - this method is always called first
		setFadeColour(r, g, b);
	}

	/**
	 * Sets the fade colour of the particle.
	 *
	 * @param r The red colour component
	 * @param g The green colour component
	 * @param b The blue colour component
	 */
	public void setFadeColour(float r, float g, float b) {
		this.fadeRed = r;
		this.fadeGreen = g;
		this.fadeBlue = b;
	}

	/**
	 * @param colourShiftInterval The array of values that determine the interval to colour shift by. R, G, B, and A.
	 * @param colourShiftRange    The array of values that determine the total range from the original colour. R, G, B, A.
	 */
	public void setColourShift(float[] colourShiftInterval, float[] colourShiftRange) {
		this.colourShiftInterval = colourShiftInterval;
		this.colourShiftRange = colourShiftRange;
	}

	/**
	 * Sets the direction this particle faces. This will cause the particle to render facing the given direction.
	 *
	 * @param yaw   The yaw angle of this particle in degrees, where 0 is south.
	 * @param pitch The pitch angle of this particle in degrees, where 0 is horizontal.
	 */
	public void setFacing(float yaw, float pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
	}

	/**
	 * Sets the target position for this particle. This will cause it to stretch to touch the given position,
	 * if supported.
	 *
	 * @param x The x-coordinate of the target position.
	 * @param y The y-coordinate of the target position.
	 * @param z The z-coordinate of the target position.
	 */
	public void setTargetPosition(double x, double y, double z) {
		// Does nothing for normal particles since normal particles always render at a single point
	}

	// ============================================== Method Overrides ==============================================

	/**
	 * Sets the target point velocity for this particle. This will cause the position it stretches to touch to move
	 * at the given velocity. Has no effect unless {@link ParticleAvatar#setTargetVelocity(double, double, double)}
	 * is also used.
	 *
	 * @param vx The x velocity of the target point.
	 * @param vy The y velocity of the target point.
	 * @param vz The z velocity of the target point.
	 */
	public void setTargetVelocity(double vx, double vy, double vz) {
		// Does nothing for normal particles since normal particles always render at a single point
	}

	/**
	 * Links this particle to the given target. This will cause it to stretch to touch the target, if supported.
	 *
	 * @param target The target to link to.
	 */
	public void setTargetEntity(Entity target) {
		// Does nothing for normal particles since normal particles always render at a single point
	}

	/**
	 * Sets the length of this particle. This will cause it to stretch to touch a point this distance along its
	 * linked entity's line of sight.
	 *
	 * @param length The length to set.
	 */
	public void setLength(double length) {
		// Does nothing for normal particles since normal particles always render at a single point
	}

	@Override
	public int getFXLayer() {
		return sprites.length == 0 ? super.getFXLayer() : 1; // This has to be 1 for the TextureAtlasSprites to work
	}

	@Override
	public int getBrightnessForRender(float partialTick) {
		return shaded ? super.getBrightnessForRender(partialTick) : 15728880;
	}

	/**
	 * Renders the particle. The mapping names given to the parameters in this method are very misleading; see below for
	 * details of what they actually do. (They're also in a strange order...)
	 *
	 * @param buffer       The {@code BufferBuilder} object.
	 * @param viewer       The entity whose viewpoint the particle is being rendered from; this should always be the
	 *                     client-side player.
	 * @param partialTicks The partial tick time.
	 * @param lookZ        Equal to the cosine of {@code viewer.rotationYaw}. Will be -1 when facing north (negative Z), 0 when
	 *                     east/west, and +1 when facing south (positive Z). Independent of pitch.
	 * @param lookY        Equal to the cosine of {@code viewer.rotationPitch}. Will be 1 when facing directly up or down, and 0
	 *                     when facing directly horizontally.
	 * @param lookX        Equal to the sine of {@code viewer.rotationYaw}.  Will be -1 when facing east (positive X), 0 when
	 *                     facing north/south, and +1 when facing west (negative X). Independent of pitch.
	 * @param lookXY       Equal to {@code lookX} times the sine of {@code viewer.rotationPitch}. Will be 0 when facing directly horizontal.
	 *                     When facing directly up, will be equal to {@code -lookX}. When facing directly down, will be equal to {@code lookX}.
	 * @param lookYZ       Equal to {@code -lookZ} times the sine of {@code viewer.rotationPitch}. Will be 0 when facing directly horizontal.
	 *                     When facing directly up, will be equal to {@code -lookZ}. When facing directly down, will be equal to {@code lookZ}.
	 */
	// Fun fact: unlike entities, particles don't seem to bother checking the camera frustum...
	@Override
	public void renderParticle(BufferBuilder buffer, Entity viewer, float partialTicks, float lookZ, float lookY,
							   float lookX, float lookXY, float lookYZ) {

		updateEntityLinking(partialTicks);

		if (Float.isNaN(this.yaw) || Float.isNaN(this.pitch)) {
			// Normal behaviour (rotates to face the viewer)
			drawParticle(buffer, viewer, partialTicks, lookZ, lookY, lookX, lookXY, lookYZ);
		} else {

			// Specific rotation

			// Copied from ActiveRenderInfo; converts yaw and pitch into the weird parameters used by renderParticle.
			// The 1st/3rd person distinction has been removed since this has nothing to do with the view angle.

			float degToRadFactor = 0.017453292f; // Conversion from degrees to radians

			float rotationX = MathHelper.cos(yaw * degToRadFactor);
			float rotationZ = MathHelper.sin(yaw * degToRadFactor);
			float rotationY = MathHelper.cos(pitch * degToRadFactor);
			float rotationYZ = -rotationZ * MathHelper.sin(pitch * degToRadFactor);
			float rotationXY = rotationX * MathHelper.sin(pitch * degToRadFactor);

			drawParticle(buffer, viewer, partialTicks, rotationX, rotationY, rotationZ, rotationYZ, rotationXY);
		}
	}

	/**
	 * Delegate function for {@link ParticleAvatar#renderParticle(BufferBuilder, Entity, float, float, float, float, float, float)};
	 * does the actual rendering. Subclasses should override this method instead of renderParticle. By default, this
	 * method simply calls super.renderParticle.
	 */
	//TODO: Fix this method and/or the renderParticle method to use AvatarParticle's rendering, so it doesn't have holes in water and such.
	protected void drawParticle(BufferBuilder buffer, Entity viewer, float partialTicks, float rotationX, float rotationY, float rotationZ, float rotationYZ, float rotationXY) {
		super.renderParticle(buffer, viewer, partialTicks, rotationX, rotationY, rotationZ, rotationYZ, rotationXY);
	}


	// =============================================== Helper Methods ===============================================

	protected void updateEntityLinking(float partialTicks) {
		if (this.entity != null) {
			// This is kind of cheating but we know it's always a constant velocity so it works fine
			prevPosX = posX + entity.prevPosX - entity.posX - relativeMotionX * (1 - partialTicks);
			prevPosY = posY + entity.prevPosY - entity.posY - relativeMotionY * (1 - partialTicks);
			prevPosZ = posZ + entity.prevPosZ - entity.posZ - relativeMotionZ * (1 - partialTicks);
		}
	}

	@Override
	public void onUpdate() {

		//TODO: Fix collision so that particles don't collide with their owner's entities!
		super.onUpdate();

		if (behaviour != null)
			behaviour.onUpdate(this);

		if (this.canCollide && this.onGround) {
			// I reject your friction and substitute my own!
			this.motionX /= 0.699999988079071D;
			this.motionZ /= 0.699999988079071D;
		} else if (entity != null || radius > 0) {

			double x = relativeX;
			double y = relativeY;
			double z = relativeZ;

			// Entity linking
			if (this.entity != null) {
				if (this.entity.isDead) {
					this.setExpired();
				} else {
					if (canCollide) {
						//Ensures proper particle collision
						if (!(motionX == 0 && prevVelX != 0 || motionY == 0 && prevVelY == 0 || motionZ == 0 && prevVelZ == 0)) {
							x += this.entity.posX;
							y += this.entity.posY;
							z += this.entity.posZ;
						}
					}
				}
			}

			// Spin
			if (radius > 0) {
				angle += speed;
				// If the particle has spin, x/z relative position is used as centre and coords are changed each tick
				x += radius * -MathHelper.cos(angle);
				z += radius * MathHelper.sin(angle);
			}

			this.setPosition(x, y, z);

			this.relativeX += relativeMotionX;
			this.relativeY += relativeMotionY;
			this.relativeZ += relativeMotionZ;
		}

		ticksExisted = particleAge;
		//Colour shifting! Who needs colour fading, amirite?
		//Copied from my (FavouriteDragon) glorious light orb code.
		if (colourShiftRange[0] != 0 && colourShiftInterval[0] != 0) {
			float rRange = colourShiftRange[0] / 2;
			float gRange = colourShiftRange[1] / 2;
			float bRange = colourShiftRange[2] / 2;
			float aRange = colourShiftRange[3] / 2;
			float r = initialRed;
			float g = initialGreen;
			float b = initialBlue;
			float a = 1;///entity.getInitialColourA();
			for (int i = 0; i < 4; i++) {
				float red, green, blue, alpha;
				float rMin = r < rRange ? 0 : r - rRange;
				float gMin = g < gRange ? 0 : r - gRange;
				float bMin = b < bRange ? 0 : r - bRange;
				float aMin = a < aRange ? 0 : a - aRange;
				float rMax = r + rRange;
				float gMax = b + gRange;
				float bMax = g + bRange;
				float aMax = a + aRange;
				switch (i) {
					case 0:
						float amountR = AvatarUtils.getRandomNumberInRange(0,
								(int) (100 / rMax)) / 100F * colourShiftInterval[0];
						red = entity.world.rand.nextBoolean() ? r + amountR : r - amountR;
						red = MathHelper.clamp(red, rMin, rMax);
						particleRed = red;
						break;

					case 1:
						float amountG = AvatarUtils.getRandomNumberInRange(0,
								(int) (100 / gMax)) / 100F * colourShiftInterval[1];
						green = entity.world.rand.nextBoolean() ? g + amountG : g - amountG;
						green = MathHelper.clamp(green, gMin, gMax);
						particleGreen = green;
						break;

					case 2:
						float amountB = AvatarUtils.getRandomNumberInRange(0,
								(int) (100 / bMax)) / 100F * colourShiftInterval[2];
						blue = entity.world.rand.nextBoolean() ? b + amountB : b - amountB;
						blue = MathHelper.clamp(blue, bMin, bMax);
						particleBlue = blue;
						break;

					case 3:
						float amountA = AvatarUtils.getRandomNumberInRange(0,
								(int) (100 / aMax)) / 100F * colourShiftInterval[3];
						alpha = entity.world.rand.nextBoolean() ? a + amountA : a - amountA;
						alpha = MathHelper.clamp(alpha, aMin, aMax);
						particleAlpha = alpha;
						break;
				}
			}
		}

		// Colour fading
		float ageFraction = (float) this.particleAge / (float) this.particleMaxAge;
		// No longer uses setRBGColorF because that method now also sets the initial values
		this.particleRed = this.initialRed + (this.fadeRed - this.initialRed) * ageFraction;
		this.particleGreen = this.initialGreen + (this.fadeGreen - this.initialGreen) * ageFraction;
		this.particleBlue = this.initialBlue + (this.fadeBlue - this.initialBlue) * ageFraction;

		// Animation
		if (sprites.length > 1) {
			// Math.min included for safety so the index cannot possibly exceed the length - 1 an cause an AIOOBE
			// (which would probably otherwise happen if particleAge == particleMaxAge)
			this.setParticleTexture(sprites[Math.min((int) (ageFraction * sprites.length), sprites.length - 1)]);
		}

		if (scaleOnUpdate)
			particleScale += scaleChange;
		if (speedChangeOnUpdate) {
			motionX += speedChange;
			motionY += speedChange;
			motionZ += speedChange;
		}

		// Collision spreading
		if (canCollide) {

			if (this.motionX == 0 && this.prevVelX != 0) { // If the particle just collided in x
				// Reduce lateral velocity so the added spread speed actually has an effect
				this.motionY *= IMPACT_FRICTION;
				this.motionZ *= IMPACT_FRICTION;
				// Add random velocity in y and z proportional to the impact velocity
				this.motionY += (rand.nextDouble() * 2 - 1) * this.prevVelX * SPREAD_FACTOR;
				this.motionZ += (rand.nextDouble() * 2 - 1) * this.prevVelX * SPREAD_FACTOR;
			}

			if (this.motionY == 0 && this.prevVelY != 0) { // If the particle just collided in y
				// Reduce lateral velocity so the added spread speed actually has an effect
				this.motionX *= IMPACT_FRICTION;
				this.motionZ *= IMPACT_FRICTION;
				// Add random velocity in x and z proportional to the impact velocity
				this.motionX += (rand.nextDouble() * 2 - 1) * this.prevVelY * SPREAD_FACTOR;
				this.motionZ += (rand.nextDouble() * 2 - 1) * this.prevVelY * SPREAD_FACTOR;
			}

			if (this.motionZ == 0 && this.prevVelZ != 0) { // If the particle just collided in z
				// Reduce lateral velocity so the added spread speed actually has an effect
				this.motionX *= IMPACT_FRICTION;
				this.motionY *= IMPACT_FRICTION;
				// Add random velocity in x and y proportional to the impact velocity
				this.motionX += (rand.nextDouble() * 2 - 1) * this.prevVelZ * SPREAD_FACTOR;
				this.motionY += (rand.nextDouble() * 2 - 1) * this.prevVelZ * SPREAD_FACTOR;
			}

			double searchRadius = 20;

			if (spawnEntity != null) {
				List<Entity> nearbyEntities = AvatarEntityUtils.getEntitiesWithinRadius(searchRadius, this.posX,
						this.posY, this.posZ, world);
				//TODO: Add a list of active particles to the player.
				//Normal collision:
				Predicate<? super Entity> customHitboxFilter = entity1 -> !(entity1 instanceof ICustomHitbox && ((ICustomHitbox) entity1).contains(new Vec3d(this.posX, this.posY, this.posZ)));
				customHitboxFilter = customHitboxFilter.or(entity1 -> entity1 instanceof AvatarEntity && ((AvatarEntity) entity1).getOwner() == spawnEntity);
				nearbyEntities.removeIf(customHitboxFilter);
				//TODO: Proper particle damaging and such

				if (nearbyEntities.size() > 0) this.setExpired();
			}

		}

		this.prevVelX = motionX;
		this.prevVelY = motionY;
		this.prevVelZ = motionZ;
	}

	// Overridden and copied to fix the collision behaviour
	@Override
	public void move(double x, double y, double z) {

		double origY = y;
		double origX = x;
		double origZ = z;

		if (this.canCollide) {

			List<AxisAlignedBB> list = this.world.getCollisionBoxes(null, this.getBoundingBox().expand(x, y, z).grow(0.1));
			List<Entity> entityList = this.world.getEntitiesWithinAABB(Entity.class, getBoundingBox().expand(x, y, z).grow(0.15));

			collidedWithSolid = false;
			collidedWithParticle = false;
			dynamicCollidedWithEntity = false;
			for (Entity hit : entityList) {
				onCollideWithEntity(hit);
			}
			for (AxisAlignedBB axisalignedbb : list) {
				y = axisalignedbb.calculateYOffset(this.getBoundingBox(), y);
			}

			this.setBoundingBox(this.getBoundingBox().offset(0.0D, y, 0.0D));

			for (AxisAlignedBB axisalignedbb1 : list) {
				x = axisalignedbb1.calculateXOffset(this.getBoundingBox(), x);
			}

			this.setBoundingBox(this.getBoundingBox().offset(x, 0.0D, 0.0D));

			for (AxisAlignedBB axisalignedbb2 : list) {
				z = axisalignedbb2.calculateZOffset(this.getBoundingBox(), z);
			}

			this.setBoundingBox(this.getBoundingBox().offset(0.0D, 0.0D, z));

		} else {
			this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
		}

		world.getCollisionBoxes(null, getBoundingBox());
		if (!AvatarUtils.getAliveParticles().isEmpty()) {
			Queue<Particle> particles = AvatarUtils.getAliveParticles().stream().filter(particle -> particle.getBoundingBox().intersects(getBoundingBox())
					&& particle instanceof ParticleAvatar && ((ParticleAvatar) particle).spawnEntity != spawnEntity && particle != this)
					.collect(Collectors.toCollection(ArrayDeque::new));
			if (!particles.isEmpty()) {
				//Makes particles spread out on collision, but also makes them push other particles
				collidedWithParticle = true;
				Vec3d hitVel = ((ParticleAvatar) particles.peek()).getVelocity();
				Vec3d pVel = getVelocity();
				if (AvatarUtils.getMagnitude(hitVel) >= AvatarUtils.getMagnitude(pVel))
					motionX = motionY = motionZ = 0;
				else {
					this.motionX += hitVel.x;
					this.motionY += hitVel.y;
					this.motionZ += hitVel.z;
				}
			}
		}
		this.resetPositionToBB();
		this.onGround = origY != y && origY < 0.0D;

		if (collidedWithSolid || collidedWithParticle)
			motionX = motionY = motionZ = 0.0D;

		if (origX != x) this.motionX = 0.0D;
		if (origY != y) this.motionY = 0.0D; // Why doesn't Particle do this for y?
		if (origZ != z) this.motionZ = 0.0D;
	}


	public void onCollideWithEntity(Entity entity) {
		if (entity != getEntity() && (getAbility() != null || this.element != null)) {
			if (entity instanceof EntityShield && ((EntityShield) entity).getOwner() != getEntity() || entity instanceof EntityWall || entity instanceof EntityWallSegment) {
				/*if (getAbility() != null)
					AvatarMod.network.sendToServer(new PacketSParticleCollideEvent(entity, this.getVelocity(), spawnEntity, getAbility()));
				else
					AvatarMod.network.sendToServer(new PacketSParticleCollideEvent(entity, this.getVelocity(), spawnEntity, element.getId()));
				**/collidedWithSolid = true;
			} else if (entity instanceof EntityThrowable || entity instanceof EntityArrow || entity instanceof EntityOffensive && ((EntityOffensive) entity).getOwner() != spawnEntity
					|| entity instanceof IOffensiveEntity && ((AvatarEntity) entity).getOwner() != spawnEntity) {
				dynamicCollidedWithEntity = true;
				Vec3d hitVel = new Vec3d(entity.motionX, entity.motionY, entity.motionZ);
				Vec3d pVel = new Vec3d(motionX, motionY, motionZ);
				if (AvatarUtils.getMagnitude(hitVel) >= AvatarUtils.getMagnitude(pVel))
					motionX = motionY = motionZ = 0;
				else {
					this.motionX += entity.motionX;
					this.motionY += entity.motionY;
					this.motionZ += entity.motionZ;
				}
				if (entity instanceof AvatarEntity)
					applyElementalContact((AvatarEntity) entity);

				//if (entity != null && spawnEntity != null && getAbility() != null)
				//	AvatarMod.network.sendToServer(new PacketSParticleCollideEvent(entity, this.getVelocity(), spawnEntity, getAbility()));
			} else if (spawnEntity != null && getAbility() != null && entity != spawnEntity && !(entity instanceof AvatarEntity) || entity instanceof AvatarEntity && ((AvatarEntity) entity).getOwner() != spawnEntity && !collidedWithSolid) {
				//Send packets
				//TODO: Find a way to reduce lag
				/*if (!entity.getIsInvulnerable()) {
					if (entity instanceof EntityLivingBase) {
						if (((EntityLivingBase) entity).attackable() && entity.canBeAttackedWithItem())
							AvatarMod.network.sendToServer(new PacketSParticleCollideEvent(entity, this.getVelocity(), spawnEntity, getAbility()));

					} else if (entity.canBeAttackedWithItem())
						AvatarMod.network.sendToServer(new PacketSParticleCollideEvent(entity, this.getVelocity(), spawnEntity, getAbility()));
				}**/
			}
		}
	}

	public void applyElementalContact(ParticleAvatar particle) {

	}

	public void applyElementalContact(AvatarEntity entity) {
		if (entity.getOwner() != null && entity.getElement() != null) {
			switch (entity.getElement().getName()) {
				case "waterbending":
					break;
				case "firebending":
					break;
				default:
					break;
			}
		}
	}

	public int getLifetimeRemaining() {
		return this.particleMaxAge - this.particleAge;
	}


	public void setBehaviour(ParticleAvatarBehaviour behaviour) {
		this.behaviour = behaviour;
	}

	/**
	 * Simple particle factory interface which takes a world and a position and returns a particle. Used (via method
	 * references) in the client proxy to link particle enum types to actual particle classes.
	 */
	@SideOnly(Side.CLIENT)
	@FunctionalInterface
	public interface IAvatarParticleFactory {
		ParticleAvatar createParticle(World world, double x, double y, double z);
	}

}
