package com.lindautech.psx.data.input;

import net.java.games.input.Component;

import java.util.ArrayList;

/**
 * An object that serves as a source of data from a physical input, like an embedded controller.
 *
 * <p>Only children of this class should be instantiated.
 */
// TODO: Remove refreshing/listeners - they should be a part of Value classes.
public abstract class Input {
  private ArrayList<UpdateListener> listeners;
  Component component;
  private int value;

  Input(Component component) {
    listeners = new ArrayList<UpdateListener>();
    this.component = component;
  }

  abstract int pollData();
}
