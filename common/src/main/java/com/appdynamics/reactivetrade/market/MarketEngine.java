package com.appdynamics.reactivetrade.market;

import com.appdynamics.reactivetrade.util.MarketScorecard;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>A {@link com.appdynamics.reactivetrade.market.MarketEngine} is responsible for coordinating, batching and passing
 * MarketCommands into an actual market.  A {@link com.appdynamics.reactivetrade.market.MarketEngine}
 * is the resource that <i>runs</i> a market.  All MarketCommands
 * should be provided to a {@link com.appdynamics.reactivetrade.market.MarketEngine} for them to be processed by
 * a market.  A {@link com.appdynamics.reactivetrade.market.MarketEngine} is also responsible for
 * notifying the world of market state changes, as a result of processing
 * MarketCommands.</p>
 */
public class MarketEngine extends Thread {

	/**
	 * <p>The {@link Symbol} of the market this {@link com.appdynamics.reactivetrade.market.MarketEngine} is running.</p>
	 */
	private Symbol symbol;
	
	/**
	 * <p>The {@link java.util.Queue} of MarketCommands to be processed (in order)
	 * the next time the {@link com.appdynamics.reactivetrade.market.MarketEngine} and market are available to
	 * process MarketCommands.</p>
	 * 
	 * <p>By default {@link com.appdynamics.reactivetrade.market.MarketEngine}s work in cycles, effectively executing batches
	 * (entire queues) of MarketCommands one at a time.  This is extremely
	 * important as it allows increased concurrency and through-put when markets
	 * are busy.</p>
	 * 
	 * <p>While we could use a {@link java.util.concurrent.ConcurrentLinkedQueue} here, it provides little
	 * benefit as the contention on this data-structure is the 'head' of the queue and
	 * not the head and tail ({@link java.util.concurrent.ConcurrentLinkedQueue}s only provide benefit if
	 * your joining and leaving the queue concurrently).</p>
	 */
	private Queue<AcceptOrder> pendingMarketCommands;

	/**
	 * <p>A simple status flag to indicate if there are commands pending processing.</p>
	 */
	private boolean areCommandsPending;

	/**
	 * <p>The definition of the market for the {@link com.appdynamics.reactivetrade.market.MarketEngine}. This
	 * information is used to help create an appropriate market.</p>
	 * 
	 * <p>NOTE: This will be <code>null</code> until the {@link com.appdynamics.reactivetrade.market.MarketEngine}
	 * has been started.</p>
	 */
	private MarketDefinition marketDefinition;
	
	/**
	 * <p>The market instances that this {@link com.appdynamics.reactivetrade.market.MarketEngine} is running.</p>
	 * 
	 * <p>NOTE: These will be <code>null</code> until the {@link com.appdynamics.reactivetrade.market.MarketEngine}
	 * has started running.</p>
	 */
	private SimpleMarket market;
    private OrderHistory history;
    private MarketScorecard scorecard;
    private double spreadThreshold;

	/**
	 * <p>An {@link java.util.concurrent.ExecutorService} that provides the ability to
	 * for the {@link com.appdynamics.reactivetrade.market.MarketEngine} to asynchronously perform tasks
	 * (like updating and sending market updates) outside the market processing
	 * and matching cycle.</p>
	 */
	private ExecutorService backgroundService;
	
    /**
	 * <p>Constructs a {@link com.appdynamics.reactivetrade.market.MarketEngine} for the specified {@link Symbol}.  Does not
	 * start the {@link com.appdynamics.reactivetrade.market.MarketEngine} thread.</p>
	 * 
	 * @param symbol
	 */
	public MarketEngine(String pfx, Symbol symbol, int historySize, MarketScorecard scorecard, double spreadThreshold) {
		this.symbol = symbol;
		this.pendingMarketCommands = new LinkedList<AcceptOrder>();
		this.marketDefinition = null;
		this.market = null;
		this.areCommandsPending = false;
        this.history = new OrderHistory(pfx + symbol.toString(), historySize);
        this.history.start();
        this.scorecard = scorecard;
        this.spreadThreshold = spreadThreshold;
	}
	
	/**
	 * <p>The {@link Symbol} for market that the {@link com.appdynamics.reactivetrade.market.MarketEngine} is running.</p>
	 * 
	 * @return {@link Symbol}
	 */
	public Symbol getSymbol() {
		return symbol;
	}
	
	/**
	 * <p>The {@link MarketDefinition} of the market that the {@link com.appdynamics.reactivetrade.market.MarketEngine} is running.</p>
	 * 
	 * @return <code>null</code> if the {@link com.appdynamics.reactivetrade.market.MarketEngine} hasn't started running.
	 */	
	public MarketDefinition getMarketDefinition() {
		return marketDefinition;
	}

	/**
	 * <p>Adds the specified {@link MarketCommand} to the {@link com.appdynamics.reactivetrade.market.MarketEngine} queue
	 * of commands to process on the next cycle.</p>
	 *
	 * @param marketCommand
	 */
	public void queueCommand(AcceptOrder marketCommand) {
		assert getSymbol().equals(marketCommand.getSymbol());

		//we need complete access to the engine to add new commands
		synchronized (this) {
			//queue the command
			pendingMarketCommands.offer(marketCommand);

			//notify the engine that a new command has arrived, if and only if it doesn't already know!
			//this saves on unnecessary thread notifications and context switching
			if (!areCommandsPending) {
				areCommandsPending = true;
				notify();
			}
		}
	}

	/**
	 * <p>Starts the {@link com.appdynamics.reactivetrade.market.MarketEngine} for the specified {@link MarketDefinition}.</p>
	 * 
	 * @param marketDefinition
	 */
	public void start(MarketDefinition marketDefinition) {
		assert symbol.equals(marketDefinition.getSymbol());
		
		this.marketDefinition = marketDefinition;
		start();
	}

    /**
     * <p>This is where is all happens, including resolving the market
     * for the {@link com.appdynamics.reactivetrade.market.MarketEngine} and processing {@link MarketCommand}s
     * in cycles.</p>
     */
    @Override
    public void run() {
        //we should have started this using the start(marketDefinition) method
        assert marketDefinition != null;

        //set the name of the thread (this is helpful when tracing!)
        setName(String.format("MarketEngine for %s", getSymbol()));

        //kick off the engine's background executor service
        backgroundService = Executors.newCachedThreadPool();

        market = new SimpleMarket(symbol, scorecard, spreadThreshold);
        System.out.printf("MarketEngine for %s started\n", getSymbol());

        try {
            //the processing loop for the engine
            while(!isStopping()) {

                //process pending market commands, in batches at a time
                synchronized (this) {
                    while (!areCommandsPending && !isStopping()) {
                        wait();
                    }

                    if (!isStopping()) {
                        //as we're about to process the current commands in the queue, we now consider
                        //that there aren't any commands pending
                        areCommandsPending = false;


                        //have the market process the commands (do matching etc)
                        AcceptOrder marketCommand = pendingMarketCommands.poll();
                        handleCommand(marketCommand);
                    }
                }
            }

        } catch (InterruptedException ie) {
            // TODO Auto-generated catch block
            ie.printStackTrace();

        } finally {
            //shutdown the background executor service immediately - no point to doing anything further now
            backgroundService.shutdownNow();
        }

        System.out.printf("MarketEngine for %s stopped\n", getSymbol());
    }

    private boolean isStopping() {
        return false;
    }

    private void handleCommand(AcceptOrder marketCommand) {

        Set<Fill> fillsToCreate = new HashSet<Fill>();
        Set<Fill> fillsToClose = new HashSet<Fill>();
        Set<Order> ordersToClose = new HashSet<Order>();

        Set<MarketOrder> results = market.processFillOrder(marketCommand.getOrder());
        for (MarketOrder mktOrder : results) {
            if (mktOrder.isFilled()) {
                fillsToClose.addAll(mktOrder.getFills());
                ordersToClose.add(mktOrder.getOrder());
            }
        }

        if (fillsToCreate != null && !fillsToCreate.isEmpty()) {
            createFills(fillsToCreate);
        }

        if (fillsToClose != null && !fillsToClose.isEmpty()) {
            closeFills(fillsToClose);
        }

        if (ordersToClose != null && !ordersToClose.isEmpty()) {
            closeOrders(ordersToClose);
        }
    }

    private void createFills(Set<Fill> fillsToCreate) {
        // stub, nothing to do
    }

    private void closeFills(Set<Fill> fillsToClose) {
        // stub, nothing to do
    }

    private void closeOrders(Set<Order> ordersToClose) {
        history.addAll(ordersToClose);
    }

    public Set<Fill> getFills() {
        return market.getFills();
    }

    public Set<Order> getClosedOrders() {
        return history.getHistory();
    }

    public Set<Order> getOpenOrders() {
        return market.getOpenOrders();
    }
}
