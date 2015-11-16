package com.operasoft.snowboard.util;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * This class implements a generic worker thread that can be used to dispatch 
 * events to several listeners in the order they were received. 
 * 
 * Manager classes supporting multiple listeners can use this class in two 
 * different ways for dispatching events to their listeners:
 * 
 * A) One worker for all listeners. With this approach, the manager allocates a 
 *    single worker instance to dispatch an event to all its listeners. Use this
 *    approach if you only have a few listeners and each of them process the 
 *    event very quickly or if you want the events to be processed in a given
 *    order by the various listeners.
 * 
 * B) One worker per listener. With this approach, the manager allocates a 
 *    worker instance for each listener registered with it. Use this approach if
 *    you need to dispatch the event to all your listeners in parallel and/or if
 *    some of them will take a long time to process the event.
 *    
 * @author Christian
 *
 * @param <S> The source class that triggered the event
 * @param <T> The event class being triggered
 */
public abstract class Worker<S, T> implements Runnable {

	private List<WorkerData> pendingTriggers = new ArrayList<WorkerData>();
	private boolean running = false;
	
	/**
	 * Starts the worker thread if it is not already running
	 */
	synchronized public void start() {
		if (running == false) {
			running = true;
			(new Thread(this)).start();
		}
	}
	
	/**
	 * Stops the worker thread if it is not already stopped.
	 */
	synchronized public void stop() {
		if (running) {
			running = false;
			notifyAll();
		}
	}
	
	/**
	 * Checks whether the worker thread is already running
	 * @return
	 */
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * 
	 * @param source
	 * @param trigger
	 */
	synchronized public void add(S source, T trigger) {
		pendingTriggers.add(new WorkerData(source, trigger));
		notifyAll();
	}
	
	synchronized private WorkerData retrieveNext() {
		while (running && pendingTriggers.isEmpty()) {
			try {wait();} catch (InterruptedException e) {}
		}
		
		if (!running) {
			return null;
		}
		
		return pendingTriggers.remove(0);
	}
	
	@Override
	public void run() {
		
		Log.d(this.getClass().getSimpleName(), "Started");
		try {
			while (running) {
				WorkerData data = retrieveNext();
				if (data != null) {
					process(data.source, data.trigger);
				}
			}
		} catch (Throwable t) {
			Log.e(this.getClass().getSimpleName(), "Caught throwable", t);
			running = false;
		}
		Log.d(this.getClass().getSimpleName(), "Stopped");
	}
	
	/**
	 * Process the next event/trigger scheduled.
	 * 
	 * @param source
	 * @param trigger
	 */
	abstract protected void process(S source, T trigger);

	private class WorkerData {
		public S source;
		public T trigger;
		
		public WorkerData(S source, T trigger) {
			this.source = source;
			this.trigger = trigger;
		}
	}
}
