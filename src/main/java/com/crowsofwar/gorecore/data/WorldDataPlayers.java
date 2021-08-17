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

package com.crowsofwar.gorecore.data;

import com.crowsofwar.gorecore.GoreCore;
import com.crowsofwar.gorecore.util.AccountUUIDs;
import com.crowsofwar.gorecore.util.GoreCoreNBTUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLLog;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * A world data class which comes equipped with the ability to save and load
 * player data.
 *
 * @param T The type of your player data
 * @author CrowsOfWar
 */
public abstract class WorldDataPlayers<T extends PlayerData> extends WorldData {

    private Map<UUID, PlayerData> players;

    public WorldDataPlayers(String key) {
        super(key);
        this.players = new HashMap<UUID, PlayerData>();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.players = GoreCoreNBTUtil.readMapFromNBT(nbt, PlayerData.MAP_USER, "PlayerData", new Object[]{},
                new Object[]{playerDataClass(), this});
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        GoreCoreNBTUtil.writeMapToNBT(nbt, players, PlayerData.MAP_USER, "PlayerData");
        return nbt;
    }

    /**
     * Gets the player data for that player, creating it if necessary.
     *
     * @param player The UUID of the player to get data for
     * @return Player data for that player
     */
    public T getPlayerData(UUID player) {
        Objects.requireNonNull(player, "Tried to create data for null player");
        Objects.requireNonNull(getWorld(), "Tried to create data for null world");
        T data = getPlayerDataWithoutCreate(player);
        if (data == null) {
            data = createNewPlayerData(player);
            players.put(player, data);
            if (data != null)
                data.setPlayerEntity(AccountUUIDs.findEntityFromUUID(getWorld(), player));
            saveChanges();
        }
        Objects.requireNonNull(data, String.format("Couldn't create data for player \"%s\" and world \"%s\"!", player, getWorld().provider.getDimensionType().getName()));
        return data;

    }

    /**
     * Gets the player data for the player. If the player data has not been
     * created, then this will return null.
     *
     * @param player The UUID of the player to get data for
     * @return Player data for the player, or null if it does not exist
     */
    public T getPlayerDataWithoutCreate(UUID player) {
        T data = (T) players.get(player);
        if (data != null && data.getPlayerEntity() == null) {
            data.setPlayerEntity(AccountUUIDs.findEntityFromUUID(getWorld(), player));
        }
        return data;
    }

    public abstract Class<T> playerDataClass();

    private T createNewPlayerData(UUID player) {
        try {

            EntityPlayer playerEntity = AccountUUIDs.findEntityFromUUID(getWorld(), player);
            if (playerEntity == null)
                GoreCore.LOGGER.warn("WARNING: playerEntity was null while creating new player data");
            PlayerData data = playerDataClass()
                    .getConstructor(DataSaver.class, UUID.class, EntityPlayer.class)
                    .newInstance(this, player, playerEntity);
            return (T) data;

        } catch (Exception e) {
            FMLLog.warning("GoreCore> Found an error when trying to make new player data!");
            e.printStackTrace();
            return null;
        }
    }

}
