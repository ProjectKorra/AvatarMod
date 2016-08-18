package com.crowsofwar.gorecore.util;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * <p>
 * A version checker that has the server greet players on login.
 * </p>
 * 
 * @author CrowsOfWar
 */
public class GoreCoreVersionCheckerServerChat extends GoreCoreVersionChecker {
	
	private final String chatKey;
	
	public GoreCoreVersionCheckerServerChat(String chatKey, String currentVersion, String url) {
		this(chatKey, true, currentVersion, url);
	}
	
	public GoreCoreVersionCheckerServerChat(String chatKey, boolean enabled, String currentVersion, String url) {
		super(currentVersion, url);
		this.chatKey = chatKey;
		if (enabled) FMLCommonHandler.instance().bus().register(this);
	}
	
	@SubscribeEvent
	public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
		event.player.addChatMessage(
				new ChatComponentTranslation(chatKey + (upToDate() ? ".upToDate" : ".needsUpdate"), currentVersion(), latestVersion()));
	}
	
}
