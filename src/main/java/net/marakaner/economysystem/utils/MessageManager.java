package net.marakaner.economysystem.utils;

import net.marakaner.economysystem.EconomySystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageManager {

    private Map<String, String> messages;
    private Map<String, List<String>> messageLists;
    private Map<String, Boolean> options;
    private Map<String, Integer> numbers;

    public MessageManager() {
        this.messages = new HashMap<>();
        this.messageLists = new HashMap<>();
        this.options = new HashMap<>();
        this.numbers = new HashMap<>();
    }

    public void registerMessages(FileConfiguration cfg, String... messages) {
        for(String all : messages) {
            try {
                this.messages.put(all, ChatColor.translateAlternateColorCodes('&', cfg.getString(all)));
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage("§8[§2EconomySystem§8] §cCould not load the message: " + all);
            }
        }
    }

    public void registerMessageLists(FileConfiguration cfg, String... lists) {
        for(String all : lists) {

            List<String> currentList = cfg.getStringList(all);

            currentList.replaceAll(message -> ChatColor.translateAlternateColorCodes('&', message));

            this.messageLists.put(all, currentList);
        }
    }

    public void registerOptions(FileConfiguration cfg, String... options) {
        for(String all : options) {

            Boolean current = cfg.getBoolean(all);

            this.options.put(all, current);
        }
    }

    public void registerNumbers(FileConfiguration cfg, String... numbers) {
        for(String all : numbers) {
            this.numbers.put(all, cfg.getInt(all));
        }
    }

    public String getMessage(String identify) {
        return this.messages.getOrDefault(identify, null);
    }

    public List<String> getMessageList(String identify) {
        return this.messageLists.getOrDefault(identify, null);
    }

    public boolean getOption(String identify) {
        return this.options.getOrDefault(identify, false);
    }

    public int getNumber(String identify) {
        return this.numbers.getOrDefault(identify, 0);
    }

}
