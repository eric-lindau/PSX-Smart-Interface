package com.lindautech.psx.data.processing;

import com.lindautech.psx.network.NetworkClient;

/**
 * A service that polls data sources at a controlled rate on a distinct thread.
 */
// TODO: Add support for checking if variables have changed?
// TODO: *** INSTEAD: Let network (Value) portion handle checking changed variables in order to avoid unnecessary checks
public class PollingService implements Runnable {
  // TODO: Here, use a NetworkClient to send data over wire, while in ui package, simply update based
  // TODO: ... on components
  private PSXVariable[] variables;
  private NetworkClient client;

  // TODO: *** Separate network part out, add event listeners

  /** Constructs a new polling service with given data (value) sources and network client. */
  public PollingService(PSXVariable[] values, NetworkClient client) {
    this.variables = values;
    this.client = client;
  }

  // TODO: Should run at 10Hz/20Hz
  // TODO: Another should run faster for UI and actually update... separate from this?
  @Override
  public void run() {
    for (PSXVariable variable : variables) {
      // Shouldn't be listener based ... operating at different rates!
      if (variable.shouldUpdate()) {
        client.sendData(variable.compiled());
      }
    }
  }
}
