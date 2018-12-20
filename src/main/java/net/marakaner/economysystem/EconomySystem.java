package net.marakaner.economysystem;

import net.marakaner.economysystem.commands.TestCommand;
import net.marakaner.economysystem.listener.JoinListener;
import net.marakaner.economysystem.money.MoneyManager;
import net.marakaner.economysystem.sql.SQLManager;
import net.marakaner.economysystem.user.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class EconomySystem extends JavaPlugin {

    private static EconomySystem instance;

    // Declaration of all Managers
    private SQLManager sqlManager;
    private PlayerManager playerManager;
    private MoneyManager moneyManager;

    @Override
    public void onEnable() {
        instance = this;

        loadConfig();

        if(getConfig().getBoolean("MySQL.enabled")) {
            registerManager();

            registerListener();

            registerCommands();
        } else {
            Bukkit.getConsoleSender().sendMessage("§4EconomySystem §8-> §cYou need to enable the SQL to use the plugin");
        }
    }

    @Override
    public void onDisable() {

    }

    public void registerManager() {
        FileConfiguration cfg = getConfig();

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
}
