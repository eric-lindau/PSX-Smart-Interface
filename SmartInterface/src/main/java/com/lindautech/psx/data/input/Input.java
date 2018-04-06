package com.lindautech.psx.data.input;

import net.java.games.input.Component;

import java.util.ArrayList;

/**
 * An object that serves as a source of data from a physical input, like an embedded controller.
 *
 * <p>Only children of this class should be instantiated.
 */
abstract class Input implements DataSource {
  private ArrayList<UpdateListener> listeners;
  Component component;
  private int value;

  Input(Component component) {
    listeners = new ArrayList<UpdateListener>();
    this.component = component;
  }

  @Override
  public void refresh() {
    int presentValue = pollData();
    if (value != presentValue) {
      value = presentValue;
      updateListeners();
    }
  }

  @Override
  public void addListener(UpdateListener listener) {
    listeners.add(listener);
  }

  private void updateListeners() {
    for (UpdateListener listener : listeners) {
      listener.update();
    }
  }

  abstract int pollData();
}
