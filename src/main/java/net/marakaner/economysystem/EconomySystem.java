package net.marakaner.economysystem;

import net.marakaner.economysystem.commands.MoneyCommand;
import net.marakaner.economysystem.commands.TestCommand;
import net.marakaner.economysystem.listener.JoinListener;
import net.marakaner.economysystem.money.MoneyManager;
import net.marakaner.economysystem.sql.SQLManager;
import net.marakaner.economysystem.user.PlayerManager;
import net.marakaner.economysystem.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class EconomySystem extends JavaPlugin {

    private static EconomySystem instance;

    // Declaration of all Managers
    private SQLManager sqlManager;
    private PlayerManager playerManager;
    private MoneyManager moneyManager;
    private MessageManager messageManager;


    private String prefix;

    @Override
    public void onEnable() {
        instance = this;

        loadConfig();

        if(getConfig().getBoolean("MySQL.enabled")) {
            registerManager();

            registerListener();

            registerCommands();

            check();
        } else {
            Bukkit.getConsoleSender().sendMessage("§4EconomySystem §8-> §cYou need to enable the SQL to use the plugin");
        }
    }

    @Override
    public void onDisable() {

    }

    private void check() {
        if(Bukkit.getOnlinePlayers().size() > 0) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                getPlayerManager().initPlayer(player, finished -> {
                    if(!finished) {
                        player.kickPlayer(this.prefix + "§cA Error is happend. Please join again.");
                    }
                });
            });
        }
    }

    public void registerManager() {
        FileConfiguration cfg = getConfig();

        this.messageManager = new MessageManager();

        this.messageManager.registerOptions(cfg, "MySQL.enabled");

        this.messageManager.registerMessages(cfg, "Prefix",
                "Money.balance", "Money.balance_other", "Money.transfer", "Money.transfer_other", "Money.add", "Money.add_other", "Money.remove", "Money.remove_other", "Money.reset", "Money.reset_other", "Money.set", "Money.set_other",
                "Error.player_not_found", "Error.wrong_number", "Error.wrong_syntax", "Error.less_money", "Error.selfpay");
        this.prefix = this.messageManager.getMessage("Prefix") + " ";

        this.messageManager.registerMessageLists(cfg, "Money.help_map", "Money.help_map_admin");


        this.sqlManager = new SQLManager(cfg.getString("MySQL.host"),
                cfg.getString("MySQL.port"),
                cfg.getString("MySQL.database"),
                cfg.getString("MySQL.user"),
                cfg.getString("MySQL.password"));

        this.sqlManager.connect();

        this.playerManager = new PlayerManager(sqlManager);
        this.moneyManager = new MoneyManager(sqlManager);
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }

    private void registerListener() {
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
    }

    private void registerCommands() {
        this.getCommand("test").setExecutor(new TestCommand());
        this.getCommand("money").setExecutor(new MoneyCommand());
    }

    public static EconomySystem getInstance() {
        return instance;
    }

    public MoneyManager getMoneyManager() {
        return moneyManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public SQLManager getSqlManager() {
        return sqlManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public String getPrefix() {
        return prefix;
    }
}
