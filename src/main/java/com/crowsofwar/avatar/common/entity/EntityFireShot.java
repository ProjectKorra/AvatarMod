package com.crowsofwar.avatar.common.entity;

import elucent.albedo.event.GatherLightsEvent;
import elucent.albedo.lighting.ILightProvider;
import elucent.albedo.lighting.Light;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "elucent.albedo.lighting.ILightProvider", modid = "albedo")
public class EntityFireShot extends AvatarEntity implements ILightProvider {

	public EntityFireShot(World world) {
		super(world);
	}

	@Override
	public boolean isProjectile() {
		return true;
	}

	@Override
	@Optional.Method(modid = "albedo")
	public Light provideLight() {
		return Light.builder().pos(this).color(2F, 1F, 0F).radius(8).build();
	}

	@Override
	@Optional.Method(modid = "albedo")
	public void gatherLights(GatherLightsEvent arg0, Entity arg1) {

	}
}
