/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.util.data;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.network.packets.PacketCPlayerData;
import com.crowsofwar.gorecore.data.*;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

import java.util.*;

public class AvatarPlayerData extends PlayerData {

	private static PlayerDataFetcher<AvatarPlayerData> fetcher;

	private final BendingData bendingData;

	/**
	 * Changed DataCategories since last sent a packet
	 */
	private SortedSet<DataCategory> changed;

	/**
	 * For use by server thread only. This represents the ticksExisted value (Entity#ticksExisted)
	 * when the last information packet <strong>only about chi</strong> to client was sent.
	 * <p>
	 * Used to limit the amount of only-chi-related packets that are sent, since that is most
	 * of the packets that try to be sent
	 */
	private int lastChiPacketTime;

	public AvatarPlayerData(DataSaver dataSaver, UUID playerID, EntityPlayer player) {
		super(dataSaver, playerID, player);
		lastChiPacketTime = -1;

		boolean isClient = !(player instanceof EntityPlayerMP);

		bendingData = new BendingData(this::save, this::saveAll);

		changed = new TreeSet<>();

	}

	public static void initFetcher(PlayerDataFetcher<AvatarPlayerData> clientFetcher) {
		fetcher = new PlayerDataFetcherSided<>(clientFetcher,
				new PlayerDataFetcherServer<>(AvatarWorldData::getDataFromWorld));
	}

	public static PlayerDataFetcher<AvatarPlayerData> fetcher() {
		return fetcher;
	}

	@Override
	protected void readPlayerDataFromNBT(NBTTagCompound nbt) {
		bendingData.readFromNbt(nbt);
	}

	@Override
	protected void writePlayerDataToNBT(NBTTagCompound nbt) {
		bendingData.writeToNbt(nbt);
	}

	public void save(DataCategory category) {
		changed.add(category);
		sendPacket();
		saveChanges();
	}

	public void saveAll() {
		changed.addAll(Arrays.asList(DataCategory.values()));
		sendPacket();
		saveChanges();
	}

	private void sendPacket() {

		PacketCPlayerData packet = new PacketCPlayerData(bendingData, playerID, changed);
		EntityPlayer player = this.getPlayerEntity();
		if (player != null && !player.world.isRemote) {

			// Enforce limits for chi-only packets
			if (!doesChiLimitPass(player)) {
				return;
			}

			// Look at who is tracking this player, to avoid unnecessarily
			// sending packets to extra players
			EntityTracker tracker = ((WorldServer) player.world).getEntityTracker();

			List<EntityPlayer> nearbyPlayers = new ArrayList<>();
			nearbyPlayers.add(player);
			nearbyPlayers.addAll(tracker.getTrackingPlayers(player));

			// Find the correct range to send the packet to
			double rangeSq = 0;
			for (EntityPlayer p : nearbyPlayers) {
				if (p.getDistanceSq(player) > rangeSq) {
					rangeSq = p.getDistanceSq(player);
				}
			}
			double range = Math.sqrt(rangeSq) + 0.01;// +0.01 "just in case"

			TargetPoint targetPoint = new TargetPoint(player.dimension, player.posX, player.posY, player.posZ, range);
			//FMLLog.info("Target Point: " + targetPoint);
			AvatarMod.network.sendToAllAround(packet, targetPoint);

			changed.clear();

		}

	}

	public BendingData getData() {
		return bendingData;
	}

	/**
	 * To be called when sending an update packet. Performs rate limiting for chi only packets
	 * (i.e., where the only value sent is the chi). They aren't as important as other data and
	 * clog up the network.
	 *
	 * @return Whether the rate limit has approved the chi packet, or the packet wasn't about chi
	 * anyways
	 */
	private boolean doesChiLimitPass(EntityPlayer player) {

		if (changed.size() == 1 && changed.first() == DataCategory.CHI) {

			// Don't send chi-only packets more than once a second
			if (player.ticksExisted - lastChiPacketTime < 20 && lastChiPacketTime != -1) {
				return false;
			}
			lastChiPacketTime = player.ticksExisted;

		}

		return true;

	}

	@Override
	protected void saveChanges() {
		super.saveChanges();
		bendingData.updateMaxChi();
	}

}
