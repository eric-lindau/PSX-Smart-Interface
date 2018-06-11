package com.lindautech.psx.data.input;

import net.java.games.input.Component;

public abstract class Input {
  Component component;
  int number;
  private int value;

  Input(Component component, int number) {
    this.component = component;
    this.number = number;
  }

  @Override
  public int hashCode() {
    return number;
  }

  @Override
  public String toString() {
    return Integer.toString(number) + ". " + component.getName();
  }
}
