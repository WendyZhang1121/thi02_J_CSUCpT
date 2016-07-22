package thi02_J_CSUCpT;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//Declare class as final because its constructor throws an exception 

public final class ProcessStep implements Runnable {
	
	private static final Lock lock = new ReentrantLock();
	private static int time = 0;
	private final int step; // Perform operations when field time
							//reaches this value 
	
	private static final int MAX_STEPS = 5;
	private static final Condition[] conditions = new Condition[MAX_STEPS];

	public ProcessStep(int step) { 
		if (step <= MAX_STEPS) {
			this.step = step;
			conditions[step] = lock.newCondition(); 
			} else {
				throw new IllegalArgumentException("Too many threads"); }
	}
	
	@Override public void run() { 
		lock.lock();
		try {
			while (time != step) { 
				conditions[step].await();
			}
			// Perform operations
			time++;
			if (step + 1 < conditions.length) {
				conditions[step + 1].signal(); 
				}
		} catch (InterruptedException ie) { 
			Thread.currentThread().interrupt(); // Reset interrupted status
		} finally { 
			lock.unlock();
		}
	}
	
	public static void main(String[] args) { 
		for (int i = MAX_STEPS - 1; i >= 0; i--) {
			ProcessStep ps = new ProcessStep(i);
			new Thread(ps).start(); 
			}
	} 
	
}