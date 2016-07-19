package com.crowsofwar.avatar.client.particles;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IIcon;

@SideOnly(Side.CLIENT)
public class IconParticle implements IIcon {
	
	private final int width, height;
	private final int u, v;
	private final String name;
	private int currentAnimation;
	private final int animOffU;
	
	public IconParticle(String name, int width, int height, int u, int v, int animationUOffset) {
		this.width = width;
		this.height = height;
		this.u = u;
		this.v = v;
		this.animOffU = animationUOffset;
		this.name = name;
		this.currentAnimation = 0;
	}

	@Override
	public int getIconWidth() {
		return width;
	}
	
	@Override
	public int getIconHeight() {
		return height;
	}

	@Override
	public float getMinU() {
		return u + animOffU * currentAnimation;
	}

	@Override
	public float getMaxU() {
		return u + width + animOffU * currentAnimation;
	}

	@Override
	public float getInterpolatedU(double lerp) {
		return (float) (u + (1.0 * width * lerp / 16));
	}

	@Override
	public float getMinV() {
		return v;
	}

	@Override
	public float getMaxV() {
		return v + height;
	}

	@Override
	public float getInterpolatedV(double lerp) {
		return (float) (v + (1.0 * height * lerp / 16));
	}

	@Override
	public String getIconName() {
		return name;
	}
	
	public int getAnimation() {
		return currentAnimation;
	}
	
	public void setAnimation(int animation) {
		this.currentAnimation = animation;
	}

}
