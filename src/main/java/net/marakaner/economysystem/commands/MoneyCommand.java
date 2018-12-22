package net.marakaner.economysystem.commands;

import net.marakaner.economysystem.EconomySystem;
import net.marakaner.economysystem.user.EconomyPlayer;
import net.marakaner.economysystem.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {


        if(sender instanceof ConsoleCommandSender) {

        } else if(sender instanceof Player) {
            Player player = (Player) sender;

            if(player.hasPermission("money.admin")) {
                if (args.length == 0) {
                    EconomyPlayer economyPlayer = EconomySystem.getInstance().getPlayerManager().getEconomyPlayer(player.getUniqueId());
                    if (economyPlayer == null) {
                        return false;
                    }

                    player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Money.balance")
                            .replaceAll("%MONEY%", String.valueOf(economyPlayer.getAccount().getMoney())));
                } else if(args.length == 1) {
                    if(args[0].equalsIgnoreCase("help")) {
                        String prefix = EconomySystem.getInstance().getPrefix();
                        for(String current : EconomySystem.getInstance().getMessageManager().getMessageList("Money.help_map_admin")) {
                            player.sendMessage(prefix + current);
                        }
                    } else {

                        EconomyPlayer economyPlayer = EconomySystem.getInstance().getPlayerManager().getEconomyPlayer(args[0]);

                        if(economyPlayer != null) {
                            player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Money.balance_other")
                                    .replaceAll("%PLAYER%", economyPlayer.getPlayerName())
                                    .replaceAll("%MONEY%", String.valueOf(economyPlayer.getAccount().getMoney())));
                        } else {
                            EconomySystem.getInstance().getPlayerManager().getOfflinePlayer(args[0], offlinePlayer -> {
                                if(offlinePlayer != null) {
                                    player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Money.balance_other")
                                            .replaceAll("%PLAYER%", offlinePlayer.getPlayerName())
                                            .replaceAll("%MONEY%", String.valueOf(offlinePlayer.getAccount().getMoney())));
                                } else {
                                    player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Error.player_not_found")
                                            .replaceAll("%PLAYER%", args[0]));
                                }
                            });
                        }

                    }
                } else if(args.length == 2) {
                    if(args[0].equalsIgnoreCase("reset")) {

                        EconomyPlayer target = EconomySystem.getInstance().getPlayerManager().getEconomyPlayer(args[1]);

                        if(target != null) {
                            target.getAccount().setMoney(0);

                            player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Money.reset")
                                    .replaceAll("%PLAYER%", target.getPlayerName()));

                            Bukkit.getPlayer(target.getUniqueId()).sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Money.reset_other"));
                        } else {
                            EconomySystem.getInstance().getPlayerManager().getOfflinePlayer(args[1], economyPlayer -> {
                                economyPlayer.getAccount().setMoney(0);

                                player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Money.reset")
                                        .replaceAll("%PLAYER%", target.getPlayerName()));
                            });
                        }

                    } else {
                        player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Error.wrong_syntax")
                                .replaceAll("%COMMAND%", "money"));
                    }
                } else if(args.length == 3) {
                    if(args[0].equalsIgnoreCase("transfer")) {

                        if(player.getName().equalsIgnoreCase(args[1])) {
                            player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Error.selfpay"));
                            return false;
                        }

                        int amount;

                        try {
                            amount = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Error.wrong_number"));
                            return false;
                        }

                        if(amount <= 0) {
                            player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Error.wrong_number"));
                            return false;
                        }

                        EconomyPlayer economyPlayer = EconomySystem.getInstance().getPlayerManager().getEconomyPlayer(player.getUniqueId());

                        if(economyPlayer == null) return false;

                        if(economyPlayer.getAccount().getMoney() < amount) {
                            player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Error.less_money")
                                    .replaceAll("%MONEY%", String.valueOf(economyPlayer.getAccount().getMoney())));
                            return false;
                        }

                        EconomyPlayer target = EconomySystem.getInstance().getPlayerManager().getEconomyPlayer(args[1]);

                        if(target != null) {

                            target.getAccount().transfer(target, amount);

                            player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Money.transfer")
                                    .replaceAll("%PLAYER%", target.getPlayerName())
                                    .replaceAll("%MONEY%", String.valueOf(amount))
                                    .replaceAll("%BALANCE%", String.valueOf(target.getAccount().getMoney())));

                            Bukkit.getPlayer(target.getUniqueId()).sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Money.transfer_other")
                                    .replaceAll("%PLAYER%", player.getName())
                                    .replaceAll("%MONEY%", String.valueOf(amount)));
                        } else {
                            EconomySystem.getInstance().getPlayerManager().getOfflinePlayer(args[1], economyTarget -> {
                                economyTarget.getAccount().addMoney(amount);

                                player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Money.transfer")
                                        .replaceAll("%PLAYER%", economyTarget.getPlayerName())
                                        .replaceAll("%MONEY%", String.valueOf(amount))
                                        .replaceAll("%BALANCE%", String.valueOf(economyTarget.getAccount().getMoney())));
                            });
                        }

                    } else if(args[0].equalsIgnoreCase("add")) {

                        int amount;

                        try {
                            amount = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Error.wrong_number"));
                            return false;
                        }

                        if(amount <= 0) {
                            player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Error.wrong_number"));
                            return false;
                        }

                        EconomyPlayer target = EconomySystem.getInstance().getPlayerManager().getEconomyPlayer(args[1]);

                        if(target != null) {
                            target.getAccount().addMoney(amount);

                            player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Money.add")
                                    .replaceAll("%PLAYER%", target.getPlayerName())
                                    .replaceAll("%MONEY%", String.valueOf(amount))
                                    .replaceAll("%BALANCE%", String.valueOf(target.getAccount().getMoney())));

                            Bukkit.getPlayer(target.getUniqueId()).sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Money.add_other")
                                    .replaceAll("%MONEY%", String.valueOf(amount)));
                        } else {
                            EconomySystem.getInstance().getPlayerManager().getOfflinePlayer(args[1], economyPlayer -> {
                                economyPlayer.getAccount().addMoney(amount);

                                player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Money.add")
                                        .replaceAll("%PLAYER%", economyPlayer.getPlayerName())
                                        .replaceAll("%MONEY%", String.valueOf(amount))
                                        .replaceAll("%BALANCE%", String.valueOf(economyPlayer.getAccount().getMoney())));
                            });
                        }
                    } else if(args[0].equalsIgnoreCase("remove")) {

                        int amount;

                        try {
                            amount = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Error.wrong_number"));
                            return false;
                        }

                        if(amount <= 0) {
                            player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Error.wrong_number"));
                            return false;
                        }

                        EconomyPlayer target = EconomySystem.getInstance().getPlayerManager().getEconomyPlayer(args[1]);

                        if(target != null) {
                            target.getAccount().removeMoney(amount);

                            player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Money.remove")
                                    .replaceAll("%PLAYER%", target.getPlayerName())
                                    .replaceAll("%MONEY%", String.valueOf(amount))
                                    .replaceAll("%BALANCE%", String.valueOf(target.getAccount().getMoney())));

                            Bukkit.getPlayer(target.getUniqueId()).sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Money.remove_other")
                                    .replaceAll("%MONEY%", String.valueOf(amount)));
                        } else {
                            EconomySystem.getInstance().getPlayerManager().getOfflinePlayer(args[1], economyPlayer -> {
                                economyPlayer.getAccount().removeMoney(amount);

                                player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Money.remove")
                                        .replaceAll("%PLAYER%", economyPlayer.getPlayerName())
                                        .replaceAll("%MONEY%", String.valueOf(amount))
                                        .replaceAll("%BALANCE%", String.valueOf(economyPlayer.getAccount().getMoney())));
                            });
                        }
                    } else if(args[0].equalsIgnoreCase("set")) {

                        int amount;

                        try {
                            amount = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Error.wrong_number"));
                            return false;
                        }

                        if(amount <= 0) {
                            player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Error.wrong_number"));
                            return false;
                        }

                        EconomyPlayer target = EconomySystem.getInstance().getPlayerManager().getEconomyPlayer(args[1]);

                        if(target != null) {
                            target.getAccount().setMoney(amount);

                            player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Money.set")
                                    .replaceAll("%PLAYER%", target.getPlayerName())
                                    .replaceAll("%MONEY%", String.valueOf(amount)));

                            Bukkit.getPlayer(target.getUniqueId()).sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Money.set_other")
                                    .replaceAll("%MONEY%", String.valueOf(amount)));
                        } else {
                            EconomySystem.getInstance().getPlayerManager().getOfflinePlayer(args[1], economyPlayer -> {
                                economyPlayer.getAccount().setMoney(amount);

                                player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Money.set")
                                        .replaceAll("%PLAYER%", economyPlayer.getPlayerName())
                                        .replaceAll("%MONEY%", String.valueOf(amount)));
                            });
                        }

                    } else {
                        player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Error.wrong_syntax")
                                .replaceAll("%COMMAND%", "money"));
                    }
                } else {
                    player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Error.wrong_syntax")
                            .replaceAll("%COMMAND%", "money"));
                }
            } else {
                if(args.length == 0) {
                    EconomyPlayer economyPlayer = EconomySystem.getInstance().getPlayerManager().getEconomyPlayer(player.getUniqueId());
                    if(economyPlayer == null) {
                        return false;
                    }

                    player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Money.balance")
                            .replaceAll("%MONEY%", String.valueOf(economyPlayer.getAccount().getMoney())));
                } else if(args.length == 1) {
                    if(args[0].equalsIgnoreCase("help")) {
                        String prefix = EconomySystem.getInstance().getPrefix();
                        for(String current : EconomySystem.getInstance().getMessageManager().getMessageList("Money.help_map")) {
                            player.sendMessage(prefix + current);
                        }
                    } else {
                        player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Error.wrong_syntax")
                                .replaceAll("%COMMAND%", "money"));
                    }
                } else if(args.length == 2) {
                    if(args[0].equalsIgnoreCase("transfer")) {
                        if(player.getName().equalsIgnoreCase(args[1])) {
                            player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Error.selfpay"));
                            return false;
                        }

                        int amount;

                        try {
                            amount = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Error.wrong_number"));
                            return false;
                        }

                        if(amount <= 0) {
                            player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Error.wrong_number"));
                            return false;
                        }

                        EconomyPlayer economyPlayer = EconomySystem.getInstance().getPlayerManager().getEconomyPlayer(player.getUniqueId());

                        if(economyPlayer == null) return false;

                        if(economyPlayer.getAccount().getMoney() < amount) {
                            player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Error.less_money")
                                    .replaceAll("%MONEY%", String.valueOf(economyPlayer.getAccount().getMoney())));
                            return false;
                        }

                        EconomyPlayer target = EconomySystem.getInstance().getPlayerManager().getEconomyPlayer(args[1]);

                        if(target != null) {

                            target.getAccount().transfer(target, amount);

                            player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Money.transfer")
                                    .replaceAll("%PLAYER%", target.getPlayerName())
                                    .replaceAll("%MONEY%", String.valueOf(amount))
                                    .replaceAll("%BALANCE%", String.valueOf(target.getAccount().getMoney())));

                            Bukkit.getPlayer(target.getUniqueId()).sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Money.transfer_other")
                                    .replaceAll("%PLAYER%", player.getName())
                                    .replaceAll("%MONEY%", String.valueOf(amount)));
                        } else {
                            EconomySystem.getInstance().getPlayerManager().getOfflinePlayer(args[1], economyTarget -> {
                                economyTarget.getAccount().addMoney(amount);

                                player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Money.transfer")
                                        .replaceAll("%PLAYER%", economyTarget.getPlayerName())
                                        .replaceAll("%MONEY%", String.valueOf(amount))
                                        .replaceAll("%BALANCE%", String.valueOf(economyTarget.getAccount().getMoney())));
                            });
                        }

                    } else {
                        player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Error.wrong_syntax")
                                .replaceAll("%COMMAND%", "money"));
                    }
                } else {
                    player.sendMessage(EconomySystem.getInstance().getPrefix() + EconomySystem.getInstance().getMessageManager().getMessage("Error.wrong_syntax")
                            .replaceAll("%COMMAND%", "money"));
                }
            }
        } else {
            sender.sendMessage("Only the console and a player can execute this command.");
        }

        return true;
    }
}
