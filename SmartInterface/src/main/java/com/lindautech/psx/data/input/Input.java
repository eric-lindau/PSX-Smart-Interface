package com.lindautech.psx.data.input;

import net.java.games.input.Component;

import java.util.ArrayList;

/**
 * An object that serves as a source of data from a physical input, like an embedded controller.
 *
 * <p>Only children of this class should be instantiated.
 */
abstract class Input {
  Component component;
  private int value;

  Input(Component component) {
    this.component = component;
  }

  abstract int pollData();
}
