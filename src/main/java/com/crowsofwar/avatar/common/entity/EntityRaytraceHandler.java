package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.entity.data.RaytraceHandlerBehaviour;
import com.crowsofwar.avatar.common.util.AvatarEntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityRaytraceHandler extends EntityOffensive implements ICustomHitbox {

	private static final DataParameter<RaytraceHandlerBehaviour> SYNC_BEHAVIOUR = EntityDataManager.createKey(EntityRaytraceHandler.class,
			RaytraceHandlerBehaviour.DATA_SERIALIZER);
	private static final DataParameter<String> SYNC_FOLLOWING_ENTITY = EntityDataManager.createKey(EntityRaytraceHandler.class,
			DataSerializers.STRING);
	private ResourceLocation particleType;
	private double range;
	private DamageSource element;
	//This class handles most laser-like abilities, such as air burst's second mechanic.
	public EntityRaytraceHandler(World world) {
		super(world);
	}

	@Override
	public DamageSource getDamageSource(Entity target) {
		return AvatarDamageSource.causeBeamDamage(target, getOwner(), element);
	}

	@Override
	public Vec3d calculateIntercept(Vec3d origin, Vec3d endpoint, float fuzziness) {

		// We want the intercept between the line and a sphere
		// First we need to find the point where the line is closest to the centre
		// Then we can use a bit of geometry to find the intercept

		// Find the closest point to the centre
		// http://mathworld.wolfram.com/Point-LineDistance3-Dimensional.html
		Vec3d line = endpoint.subtract(origin);
		double t = -origin.subtract(this.getPositionVector()).dotProduct(line) / line.lengthSquared();
		Vec3d closestPoint = origin.add(line.scale(t));
		// Now calculate the distance from that point to the centre (squared because that's all we need)
		double dsquared = closestPoint.squareDistanceTo(this.getPositionVector());
		double rsquared = Math.pow(getAvgSize() + fuzziness, 2);
		// If the minimum distance is outside the radius (plus fuzziness) then there is no intercept
		if (dsquared > rsquared) return null;
		// Now do pythagoras to find the other side of the triangle, which is the distance along the line from
		// the closest point to the edge of the sphere, and go that far back towards the origin - and that's it!
		return closestPoint.subtract(line.normalize().scale(MathHelper.sqrt(rsquared - dsquared)));
	}

	@Override
	public boolean contains(Vec3d point) {
		return point.distanceTo(this.getPositionVector()) < getAvgSize();
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_BEHAVIOUR, new RaytraceHandlerBehaviour.Idle());
		dataManager.register(SYNC_FOLLOWING_ENTITY, UUID.randomUUID().toString());
	}

	public RaytraceHandlerBehaviour getBehaviour() {
		return dataManager.get(SYNC_BEHAVIOUR);
	}

	public void setBehaviour(RaytraceHandlerBehaviour behaviour) {
		dataManager.set(SYNC_BEHAVIOUR, behaviour);
	}

	public void setFollowingEntity(Entity entity) {
		dataManager.set(SYNC_FOLLOWING_ENTITY, entity.getUniqueID().toString());
	}

	public Entity getFollowingEntity() {
		return AvatarEntityUtils.getEntityFromStringID(dataManager.get(SYNC_FOLLOWING_ENTITY));
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		setBehaviour((RaytraceHandlerBehaviour) getBehaviour().onUpdate(this));
	}

	@Override
	public boolean shouldDissipate() {
		return false;
	}

	@Override
	public boolean shouldExplode() {
		return false;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public boolean canBeAttackedWithItem() {
		return false;
	}

	@Override
	public boolean canCollideWith(Entity entity) {
		return false;
	}

	@Override
	protected boolean canBeRidden(Entity entityIn) {
		return false;
	}
}
