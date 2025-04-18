package com.auroramc.reports.database.databases;

import com.auroramc.reports.bungee.Main;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Redis {

    private static Redis instance;
    private final JedisPool pool;
    private final boolean isBungee;

    public Redis(boolean isBungee) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        this.pool = new JedisPool(poolConfig, "166.0.189.128", 26982);
        this.isBungee = isBungee;
    }

    public static Redis getInstance() {
        return instance;
    }

    public static void setupRedisConnection(boolean isBungee) {
        try {
            Jedis jedis = new Jedis("166.0.189.128", 26982);
            jedis.close();
        } catch (Exception e) {
            if (isBungee) {
                Main.getInstance().sendMessage("Ocorreu um erro enquanto tentamos conectar ao Redis!", 'c');
            } else {
                com.auroramc.reports.Main.getInstance().sendMessage("Ocorreu um erro enquanto tentamos conectar ao Redis!", 'c');
            }

            System.exit(0);
        }

        instance = new Redis(isBungee);
        if (isBungee) {
            Main.getInstance().sendMessage("Conexão efetuada com sucesso com o Redis!", '6');
        } else {
            com.auroramc.reports.Main.getInstance().sendMessage("Conexão efetuada com sucesso com o Redis!", '6');
        }
    }

    public Jedis createConnection() {
        Jedis connection = null;
        try {
            connection = this.pool.getResource();
            connection.auth("P@55w0rd");
            connection.getClient().setSoTimeout(500);
            return connection;
        } catch (Exception e) {
            if (isBungee()) {
                Main.getInstance().sendMessage("Ocorreu um erro enquanto tentávamos conectar ao Redis!");
            } else {
                com.auroramc.reports.Main.getInstance().sendMessage("Ocorreu um erro enquanto tentávamos conectar ao Redis!");
            }

            // Log detalhado do erro
            e.printStackTrace();
        } finally {
            if (connection != null) {
                closeConnection(connection);
            }
        }

        return connection;
    }

    public void closeConnection(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    public boolean isBungee() {
        return isBungee;
    }
}
