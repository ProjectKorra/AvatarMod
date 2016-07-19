package com.crowsofwar.avatar.client.particles;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IIcon;

@SideOnly(Side.CLIENT)
public class IconParticle implements IIcon {
	
	private final float width, height;
	private final float u, v;
	private final int widthPx, heightPx;
	private final String name;
	private int currentAnimation;
	private final float animOffU;
	
	public IconParticle(String name, int width, int height, int u, int v, int textureWidth, int textureHeight,
			int animationUOffset) {
		this.width = 1f * width / textureWidth;
		this.height = 1f * height / textureHeight;
		this.widthPx = width;
		this.heightPx = height;
		this.u = 1f * u / textureWidth;
		this.v = 1f * v / textureHeight;
		this.animOffU = 1f * animationUOffset / textureWidth;
		this.name = name;
		this.currentAnimation = 0;
	}

	@Override
	public int getIconWidth() {
		return widthPx;
	}
	
	@Override
	public int getIconHeight() {
		return heightPx;
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
		return (float) (getMinU() + (1.0 * width * lerp / 16));
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
		return (float) (getMinV() + (1.0 * height * lerp / 16));
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
