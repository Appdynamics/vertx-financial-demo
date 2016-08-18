package com.appdynamics.reactivetrade.persist;

import java.sql.Connection;

/**
 * Created by trader on 7/14/14.
 */
public class ConnectionPool {

    private SqlConnection[] pool;
    private int nextAvailable;

    public ConnectionPool(String dbName) {
        pool = new SqlConnection[1];
        pool[0] = new SqlConnection(dbName);
        nextAvailable = 0;
    }

    public Connection get() {
        return get(true);
    }

    public SqlConnection get(boolean allowSlowness) {

        SqlConnection result = null;

        try {
            Thread.sleep(15);
        } catch (InterruptedException ie) {

        }

        synchronized (this) {
            while (nextAvailable < 0) {
                try {
                    wait();
                } catch (InterruptedException ie) {

                }
            }

            result = pool[nextAvailable--];
        }

        result.setAllowSlowness(allowSlowness);

        return result;
    }

    public void put(Connection c) {

        SqlConnection sqlc = (SqlConnection) c;

        // Assume nobody will every try to overstuff...
        synchronized (this) {
            pool[++nextAvailable] = sqlc;
            notifyAll();
        }
    }
}
