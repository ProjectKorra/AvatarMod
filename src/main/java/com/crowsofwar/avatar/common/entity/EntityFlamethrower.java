package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.gorecore.util.Vector;
import elucent.albedo.event.GatherLightsEvent;
import elucent.albedo.lighting.ILightProvider;
import elucent.albedo.lighting.Light;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@Optional.Interface(iface = "elucent.albedo.lighting.ILightProvider", modid = "albedo")
public class EntityFlamethrower extends EntityOffensive implements ILightProvider {

	public float hitboxWidth, hitboxHeight;
	private Vec3d knockback = new Vec3d(0, 0, 0);
	//Used for particle maths.
	private Vec3d spawnPos = Vec3d.ZERO;
	private double range = 0;
	private boolean shouldExplode = false;

	public EntityFlamethrower(World world) {
		super(world);
		this.ignoreFrustumCheck = true;
		this.lightTnt = true;
		this.noClip = false;
		this.setsFires = false;
	}

	public void setShouldExplode(boolean explode) {
		this.shouldExplode = explode;
	}

	@Override
	public void setPosition(Vec3d position) {
		super.setPosition(position);
		this.spawnPos = position;
	}

	@Override
	public void setPosition(Vector position) {
		super.setPosition(position);
		this.spawnPos = position.toMinecraft();
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
		return shouldExplode;
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

	}

	@Override
	public void spawnPiercingParticles(World world, Vec3d pos) {
		//We don't need to spawn any
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
	public void applyPiercingCollision() {
		super.applyPiercingCollision();
	}

	//We don't want sounds playing
	@Nullable
	@Override
	public SoundEvent[] getSounds() {
		return null;
	}

	@Override
	public void playExplosionSounds(Entity entity) {

	}

	@Override
	public void playPiercingSounds(Entity entity) {

	}

	@Override
	public void playDissipateSounds(Entity entity) {

	}

	@Override
	public boolean onMinorWaterContact() {
		if (getTier() < 5) {
			spawnExtinguishIndicators();
			setDead();
			return true;
		}
		spawnExtinguishIndicators();
		return true;

	}

	@Override
	public boolean onMajorWaterContact() {
		spawnExtinguishIndicators();
		setDead();
		return true;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
	}


	@Override
	public double getExpandedHitboxWidth() {
		return getAvgSize() / 2;
	}

	@Override
	public double getExpandedHitboxHeight() {
		return getAvgSize() / 2;
	}


	@Override
	public Vec3d getKnockback() {
		return knockback;
	}

	public void setKnockback(Vec3d knockback) {
		this.knockback = knockback;
	}

	@Override
	public DamageSource getDamageSource(Entity target) {
		return AvatarDamageSource.causeFlamethrowerDamage(target, getOwner());
	}

	public void shouldLightFires(boolean lightFires) {
		this.setsFires = lightFires;
	}

	public void setExpandedHitbox(float width, float height) {
		this.hitboxHeight = height;
		this.hitboxWidth = width;
	}

	public void setRange(float range) {
		this.range = range;
	}


	@Override
	public boolean isProjectile() {
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return true;
	}

	@Override
	@Optional.Method(modid = "albedo")
	public Light provideLight() {
		return Light.builder().pos(this).color(2F, 1F, 0F).radius(6 + getAvgSize()).build();
	}

	@Override
	@Optional.Method(modid = "albedo")
	public void gatherLights(GatherLightsEvent event, Entity entity) {

	}

}
