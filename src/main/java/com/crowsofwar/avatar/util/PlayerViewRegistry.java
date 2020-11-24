package com.crowsofwar.avatar.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerViewRegistry {
    private static Map<UUID, Integer> player_view = new HashMap<UUID, Integer>();

    public static void setPlayerViewInRegistry(UUID uuid, int mode) {
        if (player_view.containsKey(uuid)) {
            player_view.replace(uuid, mode);
        } else {
            player_view.put(uuid, mode);
        }
    }

    //-1 is an error, 0 is first person, 1 and 2 are 3rd person.
    public static int getPlayerViewMode(UUID uuid) {
        return player_view.getOrDefault(uuid, -1);
    }
}