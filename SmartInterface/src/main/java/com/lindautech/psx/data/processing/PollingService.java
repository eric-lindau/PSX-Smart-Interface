package com.lindautech.psx.data.processing;

import com.lindautech.psx.data.input.DataSource;

import java.util.ArrayList;

/**
 * A service that polls data sources at a controlled rate on a distinct thread.
 */
public class PollingService extends Thread {
  /** The rate in Hz at which the polling service will poll sources for data. */
  private static final int POLL_RATE = 20; // Should preferably be a divisor of 1000

  private ArrayList<DataSource> dataSources;

  /** Constructs a new polling service with no data sources. */
  public PollingService() {
    dataSources = new ArrayList<DataSource>();
  }

  /** Registers a given data source with the polling service. */
  public void registerDataSource(DataSource dataSource) {
    dataSources.add(dataSource);
  }

  @Override
  public void run() {
    try {
      for (DataSource dataSource : dataSources) {
        dataSource.refresh();
      }
      sleep(1000 / POLL_RATE);
    } catch (InterruptedException e) {
      // TODO: Catch this properly
      throw new RuntimeException(e);
    }
  }
}
