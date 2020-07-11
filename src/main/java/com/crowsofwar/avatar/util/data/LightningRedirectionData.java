package com.crowsofwar.avatar.util.data;

import com.crowsofwar.avatar.entity.EntityLightningArc;
import net.minecraft.entity.EntityLivingBase;

/**
 * @author CrowsOfWar
 */
public class LightningRedirectionData {

	private final EntityLivingBase originalShooter;

	private final double speed;
	private final float sizeMultiplier, turbulence, damage;
	private final boolean mainArc;

	public LightningRedirectionData(EntityLightningArc arc) {
		this.originalShooter = arc.getOwner();
		this.speed = 25;
		this.sizeMultiplier = arc.getSizeMultiplier();
		this.turbulence = arc.getTurbulence();
		this.damage = arc.getDamage();
		this.mainArc = arc.isMainArc();
	}

	public LightningRedirectionData(EntityLivingBase originalShooter, double speed, float damage, float sizeMultiplier, float turbulence,
									boolean mainArc) {
		this.originalShooter = originalShooter;
		this.speed = speed;
		this.damage = damage;
		this.sizeMultiplier = sizeMultiplier;
		this.turbulence = turbulence;
		this.mainArc = mainArc;
	}

	public EntityLivingBase getOriginalShooter() {
		return originalShooter;
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
