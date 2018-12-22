package net.marakaner.economysystem.money;

import net.marakaner.economysystem.sql.SQLManager;

import java.util.UUID;

public class MoneyManager {

    private SQLManager sqlManager;

    public MoneyManager(SQLManager sqlManager) {
        this.sqlManager = sqlManager;

        if(this.sqlManager.isConnected()) {
            sqlManager.executeUpdate("CREATE TABLE IF NOT EXISTS money_info(UUID VARCHAR(100), money INT(255), bank_money INT(255))");
        }
    }

}
