package com.lindautech.psx.data.input;

import net.java.games.input.Component;

public abstract class AbstractInput implements Input {
  Component component;
  int number;
  private int value;

  AbstractInput(Component component, int number) {
    this.component = component;
    this.number = number;
  }

  @Override
  public int hashCode() {
    return number;
  }

  @Override
  public abstract int pollData();
}
