package net.marakaner.economysystem.user;

import net.marakaner.economysystem.money.Account;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

public class EconomyPlayer {

    private UUID uniqueId;
    private String playerName;

    private Account account;

    public EconomyPlayer(Player player, Consumer<EconomyPlayer> playerConsumer) {
        this.uniqueId = player.getUniqueId();
        this.playerName = player.getName();

        this.account = new Account(this.uniqueId, finishedAccount -> {
            if(finishedAccount != null) {
                this.account = finishedAccount;
                playerConsumer.accept(this);
            } else {
                playerConsumer.accept(null);
            }
        });
    }

    public EconomyPlayer(UUID uniqueId, String playerName, Consumer<EconomyPlayer> playerConsumer) {
        this.uniqueId = uniqueId;
        this.playerName = playerName;

        this.account = new Account(this.uniqueId, finishedAccount -> {
            if(finishedAccount != null) {
                this.account = finishedAccount;
                playerConsumer.accept(this);
            } else {
                playerConsumer.accept(null);
            }
        });
    }

    public String getPlayerName() {
        return playerName;
    }

    public Account getAccount() {
        return account;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }
}
