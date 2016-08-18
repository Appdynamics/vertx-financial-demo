package com.appdynamics.reactivetrade.market;

import java.util.Calendar;
import java.util.Set;
import java.util.TreeSet;

/**
 * Keep a rolling history of orders in chunks of a fixed (in minutes) size.
 *
 * Created by trader on 7/29/14.
 */
public class OrderHistory extends Thread {

    private final Set<Order>[] chunks;
    private int index = 0;

    private static final int CHUNKSIZE_MINUTES = 5;

    public OrderHistory(String name, int size) {
        super(name);
        // size is in 5-minute chunks, so size=1 means 5 minutes of history in one chunk.
        chunks = new TreeSet[size];
        for (int i = 0; i < size; ++i) {
            chunks[i] = new TreeSet<Order>();
        }
    }

    public void addAll(Set<Order> orders) {
        synchronized (chunks) {
            chunks[index].addAll(orders);
        }
    }

    public void run() {
        while (true) {
            sleepToInterval();
            purge();

            try {
                Thread.sleep(50);
            } catch (InterruptedException ie) {

            }
        }
    }

    private void sleepToInterval() {
        long now = System.currentTimeMillis();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(now);
        c.add(Calendar.MINUTE, CHUNKSIZE_MINUTES);

        long napEnd = c.getTimeInMillis();
        long sleepTime = (long) ((napEnd - now) * 0.9);
        while (now < napEnd && sleepTime > 50) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ie) {

            }

            now = System.currentTimeMillis();
            sleepTime = (long) ((napEnd - now) * 0.9);
        }
    }

    private void purge() {
        synchronized (chunks) {
            int nextIndex = getNext(index);
            chunks[nextIndex].clear();
            index = nextIndex;
        }
    }

    public Set<Order> getHistory() {
        Set<Order> result = new TreeSet<Order>();

        synchronized (chunks) {
            int stop = index;
            int current = index;
            do {
                result.addAll(chunks[current]);
                current = getNext(current);
            } while (current != stop);
        }

        return result;
    }

    private int getNext(int base) {
        int next = base + 1;
        if (next == chunks.length) {
            next = 0;
        }

        return next;
    }

    private int getPrevious(int base) {
        int prev = base - 1;
        if (prev < 0) {
            prev = chunks.length - 1;
        }

        return prev;
    }
}
