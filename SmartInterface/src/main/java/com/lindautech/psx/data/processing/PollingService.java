package com.lindautech.psx.data.processing;

import com.lindautech.psx.network.NetworkClient;

/**
 * A service that polls data sources at a controlled rate on a distinct thread.
 */
// TODO: Add support for checking if values have changed?
// TODO: *** INSTEAD: Let network (Value) portion handle checking changed values in order to avoid unnecessary checks
public class PollingService implements Runnable {
  // TODO: Here, use a NetworkClient to send data over wire, while in ui package, simply update based
  // TODO: ... on components
  private Variable[] values;
  private NetworkClient client;

  // TODO: *** Separate network part out, add event listeners

  /** Constructs a new polling service with no data sources. */
  public PollingService(Variable[] values, NetworkClient client) {
    this.values = values;
    this.client = client;
  }

  @Override
  public void run() {
    for (Variable value : values) {
//      client.sendData(value.getCompiledData());
    }
  }
}
