package com.appdynamics.reactivetrade.persist;

import com.appdynamics.reactivetrade.util.JsonUtils;
import io.vertx.core.json.JsonObject;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by trader on 7/11/14.
 */
public class DBManager {

    private ConnectionPool pool;

    private static final Map<String, DBManager> INSTANCES = new HashMap<String, DBManager>();

    public static DBManager getInstance(String dbIdentifier) {
        DBManager result;

        synchronized (INSTANCES) {
            result = INSTANCES.get(dbIdentifier);
            if (result == null) {
                result = new DBManager(dbIdentifier);
                INSTANCES.put(dbIdentifier, result);
            }
        }

        return result;
    }

    private DBManager(String dbIdentifier) {
        pool = new ConnectionPool(dbIdentifier);
        (new ConnectionPoolHog(this)).start();
    }

    public boolean logTrade(JsonObject tradeInfo) {

        Connection c = null;

        try {
            Thread.sleep(15);
        } catch (InterruptedException ie) {

        }

        try {
            c = getConnection(false);
            Statement s = c.createStatement();
            s.execute("insert into trades values ('" + tradeInfo.getString(JsonUtils.SYMBOL) +
                    "', " + tradeInfo.getDouble(JsonUtils.PRICE) +
                    "', '" + tradeInfo.getDouble(JsonUtils.TSTAMP) +
                    "', '" + tradeInfo.getString(JsonUtils.OP) +
                    "');");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnConnection(c);
        }

        return true;
    }

    public boolean doLogin() {

        Connection c = null;

        try {
            Thread.sleep(15);
        } catch (InterruptedException ie) {

        }

        try {
            c = getConnection();
            Statement s = c.createStatement();
            s.execute("select user_id from users where username=:1 and password=:2");
            s.execute("update user_sessions set session_id=:1 where user_id=:2");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnConnection(c);
        }

        return true;
    }

    public boolean doLogout() {

        Connection c = null;

        try {
            Thread.sleep(15);
        } catch (InterruptedException ie) {

        }

        try {
            c = getConnection();
            Statement s = c.createStatement();
            s.execute("update user_sessions set session_id=null where user_id=:1");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnConnection(c);
        }

        return true;
    }

    public boolean doMicrotradeBuy() {

        Connection c = null;

        try {
            Thread.sleep(15);
        } catch (InterruptedException ie) {

        }

        try {
            c = getConnection();
            Statement s = c.createStatement();
            s.execute("insert into buy_orders values (:1, :2, :3)");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnConnection(c);
        }

        return true;
    }

    public boolean doMicrotradeSell() {

        Connection c = null;

        try {
            Thread.sleep(15);
        } catch (InterruptedException ie) {

        }

        try {
            c = getConnection();
            Statement s = c.createStatement();
            s.execute("insert into sell_orders values (:1, :2, :3)");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnConnection(c);
        }

        return true;
    }

    public boolean doStandardtradeBuy() {

        Connection c = null;

        try {
            Thread.sleep(15);
        } catch (InterruptedException ie) {

        }

        try {
            c = getConnection();
            Statement s = c.createStatement();
            s.execute("insert into buy_orders values (:1, :2, :3)");
            s = c.createStatement();
            s.execute("update clients set last_trade=$SYSDATE where client_id = :1");
            s = c.createStatement();
            s.execute("update clients set amt_held=:1 where client_id = :2 and ticker=:3");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnConnection(c);
        }

        return true;
    }

    public boolean doStandardtradeSell() {

        Connection c = null;

        try {
            Thread.sleep(15);
        } catch (InterruptedException ie) {

        }

        try {
            c = getConnection();
            Statement s = c.createStatement();
            s.execute("insert into sell_orders values (:1, :2)");
            s = c.createStatement();
            s.execute("update clients set last_trade=$SYSDATE where client_id = :1");
            s = c.createStatement();
            s.execute("update clients set amt_held=:1 where client_id = :2 and ticker=:3");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnConnection(c);
        }

        return true;
    }

    public boolean doPremierClientBuy() {

        Connection c = null;

        try {
            Thread.sleep(15);
        } catch (InterruptedException ie) {

        }

        try {
            c = getConnection();
            Statement s = c.createStatement();
            s.execute("insert into buy_orders values (:1, :2, :3)");
            s = c.createStatement();
            s.execute("update clients set last_trade=$SYSDATE where client_id = :1");
            s = c.createStatement();
            s.execute("update clients set amt_held=:1 where client_id = :2 and ticker=:3");
            s = c.createStatement();
            s.execute("update clients set commission_factor=:1 where client_id = :2");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnConnection(c);
        }

        return true;
    }

    public boolean doPremierClientSell() {

        Connection c = null;

        try {
            Thread.sleep(15);
        } catch (InterruptedException ie) {

        }

        try {
            c = getConnection();
            Statement s = c.createStatement();
            s.execute("insert into sell_orders values (:1, :2)");
            s = c.createStatement();
            s.execute("update clients set last_trade=$SYSDATE where client_id = :1");
            s = c.createStatement();
            s.execute("update clients set amt_held=:1 where client_id = :2 and ticker=:3");
            s = c.createStatement();
            s.execute("update clients set commission_factor=:1 where client_id = :2");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            returnConnection(c);
        }

        return true;
    }

    private Connection getConnection() {
        return getConnection(true);
    }

    private Connection getConnection(boolean allowSlowness) {
        return pool.get(allowSlowness);
    }

    private void returnConnection(Connection c) {
        ((SqlConnection) c).clear();
        pool.put(c);
    }

    private static class ConnectionPoolHog extends Thread {

        private DBManager victim;

        ConnectionPoolHog(DBManager toAnnoy) {
            this.victim = toAnnoy;
        }

        public void run() {
            while (true) {

                try {
                    Thread.sleep(3 * 60 * 1000);
                } catch (InterruptedException ie) {
                }

                Connection c = victim.getConnection();

                try {
                    Thread.sleep(8000);
                } catch (InterruptedException ie) {
                }

                victim.returnConnection(c);
            }
        }
    }
}
