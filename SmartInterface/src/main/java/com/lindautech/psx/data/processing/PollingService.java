package com.lindautech.psx.data.processing;

/**
 * A service that polls data sources at a controlled rate on a distinct thread.
 */
public class PollingService extends Thread {
  /** The rate in Hz at which the polling service will poll sources for data. */
  private static final int POLL_RATE = 20; // Should preferably be a divisor of 1000

  /** Constructs a new polling service with no data sources. */
  public PollingService() {
  }

  @Override
  // TODO: Constantly poll, schedule every (1000 / POLL_RATE) to update network
  public void run() {
    try {
      sleep(1000 / POLL_RATE);
    } catch (InterruptedException e) {
      // TODO: Catch this properly
      throw new RuntimeException(e);
    }
  }
}
