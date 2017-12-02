package com.crowsofwar.avatar.common.analytics;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.server.AvatarServerProxy;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;

/**
 * @author CrowsOfWar
 */
public class AvatarAnalytics {

	public static final AvatarAnalytics INSTANCE = new AvatarAnalytics();

	private final EventAnalytics events;

	public AvatarAnalytics() {
		events = new EventAnalytics();
	}

	public void init() {
		// Send initial analytics to server

		boolean isServer = AvatarMod.proxy instanceof AvatarServerProxy;
		String language = isServer ? "server" : FMLCommonHandler.instance().getCurrentLanguage();
		int mods = Loader.instance().getModList().size() - 3;

		String params = AnalyticsUtils.getBasicParameters();
		params += "\nserver=" + isServer;
		params += "\nlanguage=" + language;
		params += "\nmodCount=" + mods;

		AnalyticsUtils.makeSingleApiRequest(params);

	}

	public void pushEvent(AnalyticEvent event) {
		events.pushEvent(event);
	}

	public void uploadEvents() {
		events.uploadEvents();
	}

	public void pushEvents(AnalyticEvent... events) {
		this.events.pushEvents(events);
	}

}
