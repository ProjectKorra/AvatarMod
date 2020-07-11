package com.crowsofwar.avatar.client.sounds;


import com.crowsofwar.avatar.AvatarInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SoundsHandler 
{
	public static SoundEvent ENTITY_FLYINGLEMUR_AMBIENT,ENTITY_FLYINGLEMUR_HURT,ENTITY_FLYINGLEMUR_DEATH;
	
	public static void registerSounds() 
	{

		ENTITY_FLYINGLEMUR_AMBIENT = registerSound("entity.momo.ambient");
		ENTITY_FLYINGLEMUR_HURT = registerSound("entity.momo.hurt");
		ENTITY_FLYINGLEMUR_DEATH = registerSound("entity.momo.death");

	}
	
	private static SoundEvent registerSound(String name) 
	{
		ResourceLocation location = new ResourceLocation(AvatarInfo.MOD_ID, name);
		SoundEvent event = new SoundEvent(location);
		event.setRegistryName(name);
		ForgeRegistries.SOUND_EVENTS.register(event);
		return event;
	}
}
