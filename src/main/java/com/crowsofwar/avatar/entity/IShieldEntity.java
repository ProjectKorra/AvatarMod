package com.crowsofwar.avatar.entity;

//For projectile entities that can shield you
public interface IShieldEntity {

	float getHealth();

	float getMaxHealth();

	void setHealth(float health);

	void setMaxHealth(float maxHealth);
}
