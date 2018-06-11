package com.lindautech.psx.data.input;

import net.java.games.input.Component;

public class DigitalInput extends AbstractInput {
  // TODO: Fix constructor
  public DigitalInput(Component component) {
    super(component, 0);
  }

  // TODO: Consider changing this to boolean, removing abstraction
  // TODO: .. (Value should consider if multiple buttons are pushed and act accordingly)
  @Override
  public int pollData() {
    return component.getPollData() > 0 ? 1 : 0;
  }

  @Override
  public String toString() {
    return pollData() == 1 ? "Pushed" : "";
  }
}
