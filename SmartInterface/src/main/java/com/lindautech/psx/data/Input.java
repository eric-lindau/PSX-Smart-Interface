package com.lindautech.psx.data;

import net.java.games.input.Component;

/**
 * An object that serves as a source of data from a physical input, like an embedded controller.
 *
 * This class should only be instantiated by children.
 */
abstract class Input implements DataSource {
  // The protected modifier will be valid with full implementation
  protected Component component;
  protected int value;

  Input(Component component) {
    this.component = component;
  }

  @Override
  public void refresh() {

  }

  protected abstract int pollData();
}
