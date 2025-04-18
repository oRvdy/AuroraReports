package com.auroramc.reports.database;

import com.auroramc.reports.bungee.Main;
import com.auroramc.reports.database.databases.MySQL;
import com.auroramc.reports.database.databases.Redis;
import com.auroramc.reports.database.interfaces.DatabaseInterface;

public abstract class DataBase {

    private static DatabaseInterface<? extends DataBase> databse;

    public static void setupDataBases(DataTypes dataTypes, Main main) {
        if (dataTypes.equals(DataTypes.MYSQL)) {
            MySQL mySQL = new MySQL(main);
            mySQL.setupDataBase();
            mySQL.createDefaultTables();
            databse = mySQL;
        }

        Redis.setupRedisConnection(true);
        main.sendMessage("Você escolheu a database do tipo: " + dataTypes.getName(), '6');
    }

    @SuppressWarnings("unchecked")
    public static <T extends DataBase> T getDatabase(Class<T> databaseClass) {
        return databse != null && databaseClass.isAssignableFrom(databse.getClass()) ? (T) databse : null;
    }
}
