package com.lindautech.psx;

import com.lindautech.psx.data.processing.ValuePollingService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * An add-on for Aerowinx PSX that provides easy, smart configuration of hardware
 * components as they relate to PSX.
 *
 * https://github.com/eric-lindau/PSX-Smart-Interface
 *
 * @author Eric Lindau
 * @version 2.0.0
 */
class SmartInterface {
  private static final int NETWORK_POLL_RATE = 20;

  public static void main(String[] args) {
    ValuePollingService poller = new ValuePollingService(null, null);
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleAtFixedRate(poller, 0, 1000 / NETWORK_POLL_RATE,
        TimeUnit.MILLISECONDS);
  }
}