package net.marakaner.economysystem.money;

import net.marakaner.economysystem.EconomySystem;

import java.util.UUID;
import java.util.function.Consumer;

public class Account {

    private int money;
    private BankAccount bankAccount;

    public Account(UUID uniqueId, Consumer<Account> finished) {
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

    public int getMoney() {
        return money;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }
}
