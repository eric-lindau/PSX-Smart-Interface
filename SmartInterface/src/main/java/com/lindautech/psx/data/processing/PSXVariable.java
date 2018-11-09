package com.lindautech.psx.data.processing;

import com.lindautech.psx.data.processing.values.Value;

// TODO: Make RemoteVariable interface; PSX interface/protocol should be decoupled from entire program
// TODO: ... so that other endpoint software may be used in the future.
public class PSXVariable implements RemoteVariable {
  private String name;
  private Value[] values;
  private Object[] finalValues;
  private boolean shouldUpdate = false;

  public PSXVariable(String name, Value[] values) {
    this.name = name;
    this.values = values;
    this.finalValues = new String[values.length];
    for (int i = 0; i < finalValues.length; i++) {
      this.finalValues[i] = "";
    }
  }

  // To detect if changed: Store previous value, find new value, compare & return
  // Updates should be detected here because multiple combinations of AbstractInput changes can lead to the same result (i.e. 1+2 and 2+1 = 3)

  // This is better than checking if individual values changed because this checks right before
  // sending to server ... that is, we can change the standard in the future and have this still
  // work because the state sent to the server is all that should be checked for a change
  @Override
  public boolean shouldUpdate() {
    return this.shouldUpdate;
  }

  public void checkUpdate() {
    shouldUpdate = false;
    for (int i = 0; i < values.length; i++) {
      Object processed = values[i].processed();
      if (!processed.equals(finalValues[i])) {
        finalValues[i] = processed;
        shouldUpdate = true;
      }
    }
  }

  public synchronized String compiled() {
    StringBuilder buffer = new StringBuilder(name + '=');
    for (int i = 0; i < finalValues.length; i++) {
      buffer.append(finalValues[i]);
      if (i != finalValues.length) {
        buffer.append(';');
      }
    }
    return buffer.toString();
  }
}
