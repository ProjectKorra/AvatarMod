package com.crowsofwar.avatar.common.data;

import crowsofwar.gorecore.data.GoreCorePlayerData;
import crowsofwar.gorecore.data.GoreCoreWorldDataPlayers;
import net.minecraft.world.World;

public class AvatarWorldData extends GoreCoreWorldDataPlayers {

	public static final String WORLD_DATA_KEY = "Avatar";
	
	public AvatarWorldData() {
		super(WORLD_DATA_KEY);
		// TODO Auto-generated constructor stub
	}
	
	public AvatarWorldData(World world) {
		super(world, WORLD_DATA_KEY);
	}
	
	public AvatarWorldData(World world, String key) {
		this(world);
	}

	public AvatarWorldData(String key) {
		super(WORLD_DATA_KEY);
	}
	
	@Override
	public Class<? extends GoreCorePlayerData> playerDataClass() {
		return AvatarPlayerData.class;
	}
	
	public static AvatarWorldData getDataFromWorld(World world) {
		return getDataForWorld(AvatarWorldData.class, WORLD_DATA_KEY, world, false);
	}

}
