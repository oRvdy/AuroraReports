package com.auroramc.reports.database;

import java.util.Arrays;

public enum DataTypes {

    MYSQL("MySQL");

    private final String name;

    DataTypes(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static DataTypes getFromString(String dataTypeString) {
        return Arrays.stream(DataTypes.values()).filter(dataTypes -> dataTypes.getName().equalsIgnoreCase(dataTypeString)).findFirst().orElse(null);
    }
}
