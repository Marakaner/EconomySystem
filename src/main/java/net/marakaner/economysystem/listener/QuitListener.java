package net.marakaner.economysystem.listener;

import net.marakaner.economysystem.EconomySystem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        EconomySystem.getInstance().getPlayerManager().unregisterPlayer(event.getPlayer());
    }

}
