package net.marakaner.economysystem.money;

import net.marakaner.economysystem.EconomySystem;
import net.marakaner.economysystem.user.EconomyPlayer;

import java.util.UUID;
import java.util.function.Consumer;

public class Account {

    private UUID uniqueId;
    private int money;
    private BankAccount bankAccount;

    public Account(UUID uniqueId, Consumer<Account> finished) {
        this.uniqueId = uniqueId;
        try {
            EconomySystem.getInstance().getSqlManager().executeQuery("SELECT * FROM money_info WHERE UUID = '" + uniqueId.toString() + "'", map -> {
                if (map.isEmpty()) {
                    EconomySystem.getInstance().getSqlManager().executeUpdate("INSERT INTO money_info(UUID,money,bank_money) VALUES ('" + uniqueId.toString() + "', '" + 1000 + "', '" + 0 + "')");
                    this.money = 1000;
                    bankAccount = new BankAccount(0);
                    finished.accept(this);
                } else {
                    this.money = (int) map.get("money");
                    this.bankAccount = new BankAccount((int) map.get("bank_money"));
                    finished.accept(this);
                }
            }, "money", "bank_money");
        } catch (Exception ex) {
            finished.accept(null);
        }
    }

    public void addMoney(int amount) {
        this.money = money + amount;
        safeData();
    }

    public void removeMoney(int amount) {
        if(this.money - amount < 0) {
            this.money = 0;
        } else {
            this.money = money - amount;
        }
        safeData();
    }

    public void setMoney(int amount) {
        this.money = amount;
        safeData();
    }

    public void safeData() {
        EconomySystem.getInstance().getSqlManager().executeUpdate("UPDATE money_info SET money = '" + this.money + "' WHERE UUID = '" + this.uniqueId.toString() + "'");
    }

    public void transfer(EconomyPlayer economyPlayer, int amount) {
        this.removeMoney(amount);
        economyPlayer.getAccount().addMoney(amount);
    }

    public int getMoney() {
        return money;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }
}
