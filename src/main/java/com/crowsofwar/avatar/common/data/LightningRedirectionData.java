package com.crowsofwar.avatar.common.data;

import net.minecraft.entity.EntityLivingBase;

/**
 * @author CrowsOfWar
 */
public class LightningRedirectionData {

	private final EntityLivingBase originalShooter, redirector;

	private final double speed;
	private final float sizeMultiplier, turbulence, damage;
	private final boolean mainArc;

	public LightningRedirectionData(EntityLivingBase originalShooter, EntityLivingBase
			redirector, double speed, float damage, float sizeMultiplier, float turbulence,
									boolean mainArc) {
		this.originalShooter = originalShooter;
		this.redirector = redirector;
		this.speed = speed;
		this.damage = damage;
		this.sizeMultiplier = sizeMultiplier;
		this.turbulence = turbulence;
		this.mainArc = mainArc;
	}

	public EntityLivingBase getOriginalShooter() {
		return originalShooter;
	}

	public EntityLivingBase getRedirector() {
		return redirector;
	}

	public double getSpeed() {
		return speed;
	}

	public float getDamage() {
		return damage;
	}

	public float getSizeMultiplier() {
		return sizeMultiplier;
	}

	public float getTurbulence() {
		return turbulence;
	}

	public boolean isMainArc() {
		return mainArc;
	}
}
