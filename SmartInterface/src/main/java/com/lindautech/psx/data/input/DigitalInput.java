package com.lindautech.psx.data.input;

import net.java.games.input.Component;

public class DigitalInput extends Input {
  // TODO: Fix constructor
  public DigitalInput(Component component) {
    super(component, 0);
  }

  // TODO: Consider changing this to boolean, removing abstraction
  // TODO: .. (Value should consider if multiple buttons are pushed and act accordingly)
  public boolean isPushed() {
    return component.getPollData() > 0;
  }
}
