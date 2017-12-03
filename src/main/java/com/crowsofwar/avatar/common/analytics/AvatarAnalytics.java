package com.crowsofwar.avatar.common.analytics;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.server.AvatarServerProxy;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;

import java.util.Deque;
import java.util.LinkedList;

/**
 * @author CrowsOfWar
 */
public class AvatarAnalytics {

	/**
	 * Maximum amount of events that can be sent in bulk
	 */
	public static final int MAX_BULK_EVENTS = 20;

	public static final AvatarAnalytics INSTANCE = new AvatarAnalytics();

	private final Deque<AnalyticEvent> queuedEvents;

	public AvatarAnalytics() {
		queuedEvents = new LinkedList<>();
	}

	public void init() {
		// Send initial analytics to server

		boolean isServer = AvatarMod.proxy instanceof AvatarServerProxy;
		String language = isServer ? "server" : FMLCommonHandler.instance().getCurrentLanguage();
		int mods = Loader.instance().getModList().size() - 5;

		String params = AnalyticsUtils.getBasicParameters();
		params += "&t=event";
		params += "&ec=userstats";
		params += "&ea=userstats";
		params += "&cd1=" + isServer;
		params += "&cd3=" + language;
		params += "&cd2=" + AvatarMod.proxy.isOptifinePresent();
		params += "&cm1=" + mods;
		params += "&an=av2";
		params += "&av=" + AvatarInfo.VERSION;
		params += "&sc=start";

		AnalyticsUtils.makeSingleApiRequest(params);

		Runtime.getRuntime().addShutdownHook(new Thread(AvatarAnalytics.INSTANCE::onExit));

	}

	/**
	 * Called when Minecraft is exited, to send statistics/information on shutdown
	 */
	public void onExit() {

		// Send sessionTime metric to google
		String params = AnalyticsUtils.getBasicParameters();
		params += "&t=event";
		params += "&ec=userstats";
		params += "&ea=userstats_end";
		params += "&sc=end";
		AnalyticsUtils.makeSingleApiRequest(params);

	}

	/**
	 * Adds the given events to the queue to be sent later.
	 */
	public void pushEvent(AnalyticEvent event) {
		queuedEvents.add(event);
	}

	/**
	 * Adds the given events to the queue to be sent later.
	 */
	public void pushEvents(AnalyticEvent... events) {
		for (AnalyticEvent event : events) {
			queuedEvents.add(event);
		}
	}

	/**
	 * Sends all currently queued events to the server
	 */
	public void uploadEvents() {
		AnalyticEvent[] queuedEventsArray = queuedEvents.toArray(new AnalyticEvent[0]);
		sendEvents(queuedEventsArray);
		queuedEvents.clear();
	}

	/**
	 * Get the amount of unset events
	 */
	public int getUnsentEventsAmount() {
		return queuedEvents.size();
	}

	/**
	 * Gets the amount of time (in milliseconds) since the latest <strong>unsent</strong> event was
	 * fired. If no new events need to be sent, returns -1.
	 */
	public long getLatestEventTime() {
		if (!queuedEvents.isEmpty()) {
			AnalyticEvent latestEvent = queuedEvents.getLast();
			return System.currentTimeMillis() - latestEvent.getCreationTime();
		}
		return -1;
	}

	/**
	 * Gets the appropriate parameters to use when sending the event to google
	 */
	private String getEventParameters(AnalyticEvent event) {
		String params = AnalyticsUtils.getBasicParameters();
		params += "&t=event";
		params += "&ec=" + event.getCategory();
		params += "&ea=" + event.getAction();
		params += "&qt=" + (System.currentTimeMillis() - event.getCreationTime());
		return params;
	}

	/**
	 * Sends the given events to the server through an HTTP post request.
	 */
	private void sendEvents(AnalyticEvent... events) {

		// Must only allow up to MAX_BULK_EVENTS in each POST request, which results in this
		// algorithm
		// In each "round", i.e. execution of while loop, one batch request is sent, containing
		// several of the events, up to MAX_BULK_EVENTS.
		// Keep track of
		// 1) Total events sent so far (sentEvents); needed to keep track of overall which events
		//    still need sending
		// 2) Events sent in this round (sentHere); lets us know when to end the round (once
		//    MAX_BULK_EVENTS has been reached)

		int sentEvents = 0;

		while (events.length - sentEvents > 0) {

			String params = "";
			int sentHere = 0;
			for (; sentEvents < events.length && sentHere < MAX_BULK_EVENTS; sentHere++) {

				params += getEventParameters(events[sentEvents]) + "\n";
				sentEvents++;

			}

			// In case there's only 1 being sent, just use regular endpoint
			// For multiple, use batch endpoint

			if (sentHere == 1) {
				AnalyticsUtils.makeSingleApiRequest(params);
			} else {
				AnalyticsUtils.makeBatchApiRequest(params);
			}

		}


	}

}
