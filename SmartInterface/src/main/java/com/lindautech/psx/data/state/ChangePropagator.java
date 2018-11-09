package com.lindautech.psx.data.state;

import java.util.ArrayList;

public class ChangePropagator {
  private ArrayList<ChangeListener> listeners;

  public ChangePropagator() {
    this.listeners = new ArrayList<ChangeListener>();
  }

  public void propagate() {
    for (ChangeListener listener : listeners) {
      listener.update();
    }
  }

  public void addListener(ChangeListener listener) {
    listeners.add(listener);
  }
}
