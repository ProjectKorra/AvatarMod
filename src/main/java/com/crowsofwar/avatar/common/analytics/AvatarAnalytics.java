package com.crowsofwar.avatar.common.analytics;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.server.AvatarServerProxy;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author CrowsOfWar
 */
public class AvatarAnalytics {

	/**
	 * Maximum amount of events that can be sent in bulk
	 */
	public static final int MAX_BULK_EVENTS = 20;

	public static final AvatarAnalytics INSTANCE = new AvatarAnalytics();

	private final Queue<AnalyticEvent> queuedEvents;

	public AvatarAnalytics() {
		queuedEvents = new LinkedList<>();
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
	 * Gets the appropriate parameters to use when sending the event to google
	 */
	private String getEventParameters(AnalyticEvent event) {
		String params = AnalyticsUtils.getBasicParameters();
		params += "&t=event";
		params += "&ec=" + event.getCategory();
		params += "&ea=" + event.getAction();
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
