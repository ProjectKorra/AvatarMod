package com.crowsofwar.avatar.client;

import com.crowsofwar.avatar.common.entity.EntityArc;
import com.crowsofwar.avatar.common.entity.EntityControlPoint;
import com.crowsofwar.avatar.common.network.packets.PacketCControlPoints;

import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

/**
 * <p>
 * Allows an EntityArc to have a reference to its EntityControlPoints,
 * and vice versa.
 * <p>
 * When the client receives a {@link PacketCControlPoints}, the client's
 * packet handler creates an instance of ArcControlPointLinker. This waits
 * until the arc and points are spawned in the world, then links the arcs
 * together.
 * 
 * @author CrowsOfWar
 */
@SideOnly(Side.CLIENT)
public class ArcControlPointLinker {
	
	/**
	 * The Id of the EntityArc that we are waiting to be spawned.
	 * This will be linked to the control points.
	 */
	private final int arcId;
	/**
	 * The Ids of the Control Points that we are waiting to be spawned.
	 * These will be linked to the arc specified by arcId.
	 */
	private final int[] controlPointIds;
	/**
	 * The EntityArc with arcId that has been found. Can be null.
	 */
	private EntityArc foundArc;
	/**
	 * The control points which have been discovered.
	 */
	private EntityControlPoint[] foundControlPoints;
	
	private ArcControlPointLinker(int arcId, int[] controlPointIds) {
		this.arcId = arcId;
		this.controlPointIds = controlPointIds;
		this.foundArc = null;
		this.foundControlPoints = new EntityControlPoint[controlPointIds.length];
		MinecraftForge.EVENT_BUS.register(this);
		tryLink(); // Try to link right now
	}
	
	/**
	 * Called to link (allow referencing) the arc to the control points and vice-versa.
	 * <p>
	 * If possible, the arc/CPs are linked immediately, but because Avatar packets
	 * usually arrive before spawn packets, it will wait until spawn packets have
	 * arrived to actually perform linking.
	 * 
	 * @param arcId The arc which will be linked, specified by its Id.
	 * @param controlPointIds The control points which will be linked, specified by their Ids.
	 */
	public static void link(int arcId, int[] controlPointIds) { new ArcControlPointLinker(arcId, controlPointIds); }
	
	@SubscribeEvent
	public void onEntitySpawn(EntityJoinWorldEvent e) {
		Entity entity = e.entity;
		System.out.println("Joined " + entity);
		
		// Search for arc
		if (!didFindArc()) {
			if (entity instanceof EntityArc) {
				EntityArc arc = (EntityArc) entity;
				if (arc.getId() == arcId) {
					foundArc = arc;
					System.out.println("====Found the arc");
					return;
				}
			}
		}
		
		// Search for control points
		if (!didFindAllControlPoints()) {
			if (entity instanceof EntityControlPoint) {
				EntityControlPoint point = (EntityControlPoint) entity;
				for (int i = 0; i < controlPointIds.length; i++) {
					int id = controlPointIds[i];
					if (point.getId() == id) {
						foundControlPoints[i] = point;
						System.out.println("====Found another point");
						return;
					}
				}
			}
		}
		
		tryLink();
		
	}
	
	/**
	 * Checks if arc and control points are spawned. If they
	 * are, performs linking.
	 */
	private void tryLink() {
		if (didFindArc() && didFindAllControlPoints()) {
			
			System.out.println("====Linking");
			
			foundArc.syncControlPoints(foundControlPoints);
			
			for (EntityControlPoint point : foundControlPoints) {
				point.setArc(foundArc);
			}
			
			// Prevent memory leaks
			MinecraftForge.EVENT_BUS.unregister(this);
			
		}
	}
	
	private boolean didFindArc() {
		return foundArc != null;
	}
	
	private boolean didFindControlPoint(int index) {
		return foundControlPoints[index] != null;
	}
	
	private boolean didFindAllControlPoints() {
		for (EntityControlPoint point : foundControlPoints) {
			if (point == null) return false;
		}
		return true;
	}
	
}
