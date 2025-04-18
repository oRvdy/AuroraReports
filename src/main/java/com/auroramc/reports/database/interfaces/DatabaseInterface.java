package com.auroramc.reports.database.interfaces;

import com.auroramc.reports.database.DataBase;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseInterface<T extends DataBase> {
    void setupDataBase();
    Connection getConnection() throws ClassNotFoundException, SQLException;
    void createTable(String table);
    void closeConnection();
}
