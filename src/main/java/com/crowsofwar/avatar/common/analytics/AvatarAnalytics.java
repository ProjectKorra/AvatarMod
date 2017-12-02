package com.crowsofwar.avatar.common.analytics;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.server.AvatarServerProxy;
import net.minecraftforge.fml.common.FMLCommonHandler;

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

		String params = AnalyticsUtils.getBasicParameters();
		params += "&t=event";
		params += "&ec=userstats";
		params += "&ea=userstats";
		params += "&cdIsServer=" + isServer;
		params += "&cdLanguage=" + language;
		params += "&cdIsOptifine=" + AvatarMod.proxy.isOptifinePresent();

		AnalyticsUtils.makeSingleApiRequest(params);

	}

	public static void pushEvent(AnalyticEvent event) {
		INSTANCE.events.pushEvent(event);
	}

	public static void uploadEvents() {
		INSTANCE.events.uploadEvents();
	}

	public static void pushEvents(AnalyticEvent... events) {
		INSTANCE.events.pushEvents(events);
	}

	public static int getUnsentEventsAmount() {
		return INSTANCE.events.getUnsentEventsAmount();
	}

}
