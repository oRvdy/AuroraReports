package com.auroramc.reports.database.databases;

import com.auroramc.reports.bungee.Main;
import com.auroramc.reports.database.DataBase;
import com.auroramc.reports.database.interfaces.DatabaseInterface;
import com.auroramc.reports.simple.JSONObject;
import com.auroramc.reports.simple.parser.JSONParser;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MySQL extends DataBase implements DatabaseInterface<MySQL> {

    private final Main main;
    public Connection connection;

    public MySQL(Main main) {
        this.main = main;
    }

    @Override
    public void setupDataBase() {
        try {
            String host = main.getConfig("config").getString("database.mysql.host");
            String port = main.getConfig("config").getString("database.mysql.porta");
            String nome = main.getConfig("config").getString("database.mysql.nome");
            String usuario = main.getConfig("config").getString("database.mysql.usuario");
            String senha = main.getConfig("config").getString("database.mysql.senha");

            setupConnection(host, port, nome, usuario, senha);
            main.sendMessage("A conexão com o MySQL foi executada com sucesso!", '6');
            this.connection.close();
            this.connection = null;
            closeConnection();
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            System.exit(0);
            main.sendMessage("A conexão com o MySQL não foi bem sucedida!", 'c');
        }
    }

    @Override
    public Connection getConnection() {
        if (this.connection == null) {
            String host = main.getConfig("config").getString("database.mysql.host");
            String port = main.getConfig("config").getString("database.mysql.porta");
            String nome = main.getConfig("config").getString("database.mysql.nome");
            String usuario = main.getConfig("config").getString("database.mysql.usuario");
            String senha = main.getConfig("config").getString("database.mysql.senha");
            try {
                setupConnection(host, port, nome, usuario, senha);
            } catch (Exception ex) {
                ex.printStackTrace();
                closeConnection();
                System.exit(0);
            }
        }
        return connection;
    }

    public void executeSQL(String SQLExecute) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = getConnection().prepareStatement(SQLExecute);
            preparedStatement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closeConnection();
            try {
                if (preparedStatement != null) preparedStatement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void createTable(String table) {
        try {
            Statement statement = getConnection().createStatement();
            statement.execute(table);
            statement.close();
            closeConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void closeConnection() {
        try {
            if (this.connection != null) {
                this.connection.close();
                this.connection = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setupConnection(String host, String port, String nome, String usuario, String senha) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + nome, usuario, senha);
    }

    public Main getMain() {
        return main;
    }

    public void createDefaultTables() {
        createTable("CREATE TABLE IF NOT EXISTS ProfileReports (`UUID` VARCHAR (36) NOT NULL, `REPORTER` VARCHAR(64), `DATE` VARCHAR(64), `REASON` VARCHAR(32), `LASTVIEWER` VARCHAR(36), `TOTAL` LONG)");
    }

    public void updateStatusPlayer(String player, String table, String column, String value) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = getConnection().prepareStatement("UPDATE " + table + " SET " + column.toUpperCase(Locale.ROOT) + " = '" + value + "' WHERE UUID = '" + player.toLowerCase(Locale.ROOT) + "'");
            preparedStatement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateStatusPlayer(String player, String table, String column, Integer value) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = getConnection().prepareStatement("UPDATE " + table + " SET " + column.toUpperCase(Locale.ROOT) + " = '" + value + "' WHERE UUID = '" + player.toLowerCase(Locale.ROOT) + "'");
            preparedStatement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateStatusPlayer(String player, String table, String column, Long value) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = getConnection().prepareStatement("UPDATE " + table + " SET " + column.toUpperCase(Locale.ROOT) + " = '" + value + "' WHERE UUID = '" + player.toLowerCase(Locale.ROOT) + "'");
            preparedStatement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteColumn(String table, String column, String value) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = getConnection().prepareStatement("DELETE FROM " + table + " WHERE " + column + " = '" + value.toLowerCase(Locale.ROOT) + "'");
            preparedStatement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getStatusForPlayerString(String player, String coluna, String table) {
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = getConnection().createStatement();
            rs = statement.executeQuery("SELECT * FROM " + table + " WHERE UUID = '" + player.toLowerCase(Locale.ROOT) + "'");
            String result = "";
            while (rs.next()) {
                result = rs.getString(coluna);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
                if (rs != null) rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public JSONObject getStatusForPlayerObject(String player, String coluna, String table) {
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = getConnection().createStatement();
            rs = statement.executeQuery("SELECT * FROM " + table + " WHERE UUID = '" + player.toLowerCase(Locale.ROOT) + "'");
            JSONObject result = null;
            while (rs.next()) {
                JSONParser parser = new JSONParser();
                result = (JSONObject) parser.parse(rs.getString(coluna));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
                if (rs != null) rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public Integer getStatusForPlayerInterger(String player, String coluna, String table) {
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = getConnection().createStatement();
            rs = statement.executeQuery("SELECT * FROM " + table + " WHERE UUID = '" + player.toLowerCase(Locale.ROOT) + "'");
            int result = 0;
            while (rs.next()) {
                result = rs.getInt(coluna);
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
                if (rs != null) rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public Long getStatusForPlayerLong(String player, String coluna, String table) {
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = getConnection().createStatement();
            rs = statement.executeQuery("SELECT * FROM " + table + " WHERE UUID = '" + player.toLowerCase(Locale.ROOT) + "'");
            long result = 0;
            while (rs.next()) {
                result = rs.getLong(coluna);
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
                if (rs != null) rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public void addStatusDefaultPlayer(UUID player, String table) {
        PreparedStatement preparedStatement = null;
        try {
            if (table.equals("ProfileReports")) {
                preparedStatement = getConnection().prepareStatement("INSERT INTO " + table + " values(?,?,?,?,?,?)");
                preparedStatement.setString(1, player.toString());
                preparedStatement.setString(2, "");
                preparedStatement.setString(3, "");
                preparedStatement.setString(4, "");
                preparedStatement.setString(5, "");
                preparedStatement.setLong(6, 0L);
            }

            if (preparedStatement != null) preparedStatement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean conteinsPlayer(UUID player, String table) {
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = getConnection().createStatement();
            rs = statement.executeQuery("SELECT * FROM " + table);
            while (rs.next()) {
                if (rs.getString("UUID").equals(player.toString())) {
                    statement.close();
                    rs.close();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
                if (rs != null) rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public List<UUID> loadAccountReports(String table) {
        List<UUID> UUIDS = new ArrayList<>();
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = getConnection().createStatement();
            rs = statement.executeQuery("SELECT * FROM " + table);
            while (rs.next()) {
                UUIDS.add(UUID.fromString(rs.getString("UUID")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
                if (rs != null) rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return UUIDS;
    }
}
