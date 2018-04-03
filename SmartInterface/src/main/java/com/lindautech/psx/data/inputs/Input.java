package com.lindautech.psx.data.inputs;

import net.java.games.input.Component;

import java.util.ArrayList;

/**
 * An object that serves as a source of data from a physical input, like an embedded controller.
 *
 * This class should only be instantiated by children.
 */
abstract class Input implements DataSource {
  private ArrayList<UpdateListener> listeners;
  // The protected modifier will be valid with full implementation
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
