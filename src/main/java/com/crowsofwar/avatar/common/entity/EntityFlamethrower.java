package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityFlamethrower extends EntityOffensive {

	private Vec3d knockback = new Vec3d(0,0,0);
	private float hitboxWidth, hitboxHeight;

	public EntityFlamethrower(World world) {
		super(world);
		this.ignoreFrustumCheck = true;
		this.lightTnt = true;
		this.noClip = true;
	}


	@Override
	public boolean isPiercing() {
		return true;
	}

	@Override
	public boolean shouldDissipate() {
		return true;
	}

	@Override
	public boolean shouldExplode() {
		return false;
	}

	@Override
	public Vec3d getKnockbackMult() {
		return new Vec3d(1, 1, 1);
	}

	@Override
	public BendingStyle getElement() {
		return new Firebending();
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public void spawnDissipateParticles(World world, Vec3d pos) {
		//We don't need to spawn any since particle collision handles it
	}

	@Override
	public void spawnPiercingParticles(World world, Vec3d pos) {
		//We don't need to spawn any since particle collision handles it
	}

	@Override
	public void applyElementalContact(AvatarEntity entity) {
		super.applyElementalContact(entity);
		entity.onFireContact();
		if (entity instanceof EntityOffensive) {
			if (entity.getTier() < getTier()) {
				if (entity.getElement() instanceof Firebending) {
					((EntityOffensive) entity).Dissipate();
				}
			} else if (entity.getTier() == getTier()) {
				if (entity.velocity().magnitude() < velocity().magnitude()) {
					((EntityOffensive) entity).Dissipate();
				}
			}
		}
	}

	@Override
	public float getVolume() {
		return super.getVolume() * 1.5F;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		motionX *= 0.99;
		motionY *= 0.99;
		motionZ *= 0.99;

		if (velocity().sqrMagnitude() <= 0.5 * 0.5)
			Dissipate();
		hitboxWidth *= 1.055;
		hitboxHeight *= 1.055;
	}

	@Override
	public double getExpandedHitboxWidth() {
		return hitboxWidth;
	}

	@Override
	public double getExpandedHitboxHeight() {
		return hitboxHeight;
	}


	@Override
	public Vec3d getKnockback() {
		return knockback;
	}

	@Override
	public DamageSource getDamageSource(Entity target) {
		return AvatarDamageSource.causeFlamethrowerDamage(target, getOwner());
	}

	public void setKnockback(Vec3d knockback) {
		this.knockback = knockback;
	}

	public void shouldLightFires(boolean lightFires) {
		this.setsFires = lightFires;
	}

	public void setExpandedHitbox(float width, float height) {
		this.hitboxHeight = height;
		this.hitboxWidth = width;
	}

}
