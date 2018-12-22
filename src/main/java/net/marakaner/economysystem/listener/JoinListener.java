package net.marakaner.economysystem.listener;

import net.marakaner.economysystem.EconomySystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        EconomySystem.getInstance().getPlayerManager().initPlayer(event.getPlayer(), finished -> {
        });
    }

}
