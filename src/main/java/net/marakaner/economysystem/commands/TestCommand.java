package net.marakaner.economysystem.commands;

import net.marakaner.economysystem.EconomySystem;
import net.marakaner.economysystem.user.EconomyPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        EconomyPlayer economyPlayer = EconomySystem.getInstance().getPlayerManager().getEconomyPlayer(player.getUniqueId());

        player.sendMessage("§c" + economyPlayer.getAccount().getMoney());
        player.sendMessage("§c" + economyPlayer.getAccount().getBankAccount().getMoney());
        return true;
    }
}
