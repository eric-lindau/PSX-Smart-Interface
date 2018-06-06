package com.lindautech.psx.data.processing;

import com.lindautech.psx.network.NetworkClient;

/**
 * A service that polls data sources at a controlled rate on a distinct thread.
 */
// TODO: Add support for checking if values have changed?
// TODO: *** INSTEAD: Let network (Value) portion handle checking changed values in order to avoid unnecessary checks
public class PollingService {
  // TODO: Here, use a NetworkClient to send data over wire, while in ui package, simply update based
  // TODO: ... on components

  /** Constructs a new polling service with no data sources. */
  public PollingService(Value[] values, NetworkClient client) {

  }

  // TODO: Constantly poll, but only update Values/network every (1000 / POLL_RATE) ms,
}
