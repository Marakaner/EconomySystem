package net.marakaner.economysystem.user;

import net.marakaner.economysystem.sql.SQLManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerManager {

    private SQLManager sqlManager;

    private Map<UUID, EconomyPlayer> cachedPlayers = new HashMap<>();

    public PlayerManager(SQLManager sqlManager) {
        this.sqlManager = sqlManager;

        if(sqlManager.isConnected()) {
            sqlManager.executeUpdate("CREATE TABLE IF NOT EXISTS player_info(UUID VARCHAR(36), playername VARCHAR(100))");
        }
    }

    public void initPlayer(Player player, Consumer<Boolean> finished) {
        try {
            sqlManager.executeQuery("SELECT * FROM player_info WHERE UUID = '" + player.getUniqueId().toString() + "'", map -> {
                if (map.isEmpty()) {
                    sqlManager.executeUpdate("INSERT INTO player_info (UUID,playername) VALUES ('" + player.getUniqueId().toString() + "', '" + player.getName() + "')");
                } else {
                    if (map.get("playername") != player.getName()) {
                        sqlManager.executeUpdate("UPDATE player_info SET playername = '" + player.getName() + "' WHERE UUID = '" + player.getUniqueId().toString() + "'");
                    }
                }

                new EconomyPlayer(player, finishedPlayer -> {
                    if(finishedPlayer != null) {
                        this.cachedPlayers.put(player.getUniqueId(), finishedPlayer);
                        finished.accept(true);
                    } else {
                        finished.accept(false);
                    }
                });
            }, "UUID", "playername");
        } catch (Exception ex) {
            finished.accept(false);
        }
    }

    public EconomyPlayer getEconomyPlayer(UUID uniqueId) {
        return this.cachedPlayers.containsKey(uniqueId) ? this.cachedPlayers.get(uniqueId) : null;
    }

    public void getPlayerName(UUID uniqueId, Consumer<String> playerName) {
        sqlManager.executeQuery("SELECT * FROM player_info WHERE UUID = '" + uniqueId.toString() + "'", map -> {
            if(map.isEmpty()) {
                playerName.accept(null);
            } else {
                playerName.accept((String) map.get("playername"));
            }
        }, "playername");
    }

    public void getUUID(String name, Consumer<UUID> uniqueId) {
        sqlManager.executeQuery("SELECT * FROM player_info WHERE playername = '" + name + "'", map -> {
            if(map.isEmpty()) {
                uniqueId.accept(null);
            } else {
                uniqueId.accept(UUID.fromString((String) map.get("UUID")));
            }
        }, "UUID");
    }

}
