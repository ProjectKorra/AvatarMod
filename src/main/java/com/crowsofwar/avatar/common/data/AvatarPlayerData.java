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

package com.crowsofwar.avatar.common.data;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.network.packets.PacketCPlayerData;
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
	
	public AvatarPlayerData(DataSaver dataSaver, UUID playerID, EntityPlayer player) {
		super(dataSaver, playerID, player);
		
		boolean isClient = !(player instanceof EntityPlayerMP);
		
		bendingData = new BendingData(this::save, this::saveAll);
		
		changed = new TreeSet<>();
		
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
			
			// Look at who is tracking this player, to avoid unnecessarily
			// sending packets to extra players
			EntityTracker tracker = ((WorldServer) player.world).getEntityTracker();
			
			List<EntityPlayer> nearbyPlayers = new ArrayList<>();
			nearbyPlayers.add(player);
			nearbyPlayers.addAll(tracker.getTrackingPlayers(player));
			
			// Find the correct range to send the packet to
			double rangeSq = 0;
			for (EntityPlayer p : nearbyPlayers) {
				if (p.getDistanceSqToEntity(player) > rangeSq) {
					rangeSq = p.getDistanceSqToEntity(player);
				}
			}
			double range = Math.sqrt(rangeSq) + 0.01;// +0.01 "just in case"
			
			AvatarMod.network.sendToAllAround(packet,
					new TargetPoint(player.dimension, player.posX, player.posY, player.posZ, range));

		}
		
	}

	public BendingData getData() {
		return bendingData;
	}


	@Override
	protected void saveChanges() {
		super.saveChanges();
		bendingData.updateMaxChi();
	}

	public static void initFetcher(PlayerDataFetcher<AvatarPlayerData> clientFetcher) {
		fetcher = new PlayerDataFetcherSided<>(clientFetcher,
				new PlayerDataFetcherServer<>(AvatarWorldData::getDataFromWorld));
	}
	
	public static PlayerDataFetcher<AvatarPlayerData> fetcher() {
		return fetcher;
	}

}
