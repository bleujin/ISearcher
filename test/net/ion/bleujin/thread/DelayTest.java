package net.ion.bleujin.thread;

import java.util.Random;
import java.util.concurrent.Delayed;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

public class DelayTest extends TestCase {
	public static long BILLION = 1000000000;

	static class SecondsDelayed implements Delayed {
		long trigger;
		String name;

		SecondsDelayed(String name, long i) {
			this.name = name;
			trigger = System.nanoTime() + (i * BILLION);
		}

		public int compareTo(Delayed d) {
			long i = trigger;
			long j = ((SecondsDelayed) d).trigger;
			int returnValue;
			if (i < j) {
				returnValue = -1;
			} else if (i > j) {
				returnValue = 1;
			} else {
				returnValue = 0;
			}
			return returnValue;
		}

		public boolean equals(Object other) {
			return ((SecondsDelayed) other).trigger == trigger;
		}

		public long getDelay(TimeUnit unit) {
			long n = trigger - System.nanoTime();
			return unit.convert(n, TimeUnit.NANOSECONDS);
		}

		public long getTriggerTime() {
			return trigger;
		}

		public String getName() {
			return name;
		}

		public String toString() {
			return name + " / " + String.valueOf(trigger);
		}
	}

	public void testRun() throws InterruptedException {
		Random random = new Random();
		DelayQueue<SecondsDelayed> queue = new DelayQueue<SecondsDelayed>();
		for (int i = 0; i < 10; i++) {
			int delay = random.nextInt(10);
			System.out.println("Delaying: " + delay + " for loop " + i);
			queue.add(new SecondsDelayed("loop " + i, delay));
		}
		
		long last = 0;
		for (int i = 0; i < 10; i++) {
			SecondsDelayed delay = queue.take();
			String name = delay.getName();
			long tt = delay.getTriggerTime();
			if (i != 0) {
				System.out.println("Delta: " + (tt - last) / (double) BILLION);
			}
			System.out.println(name + " / Trigger time: " + tt);
			last = tt;
		}
	}
}