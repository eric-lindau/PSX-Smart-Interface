package com.lindautech.psx.data.input;

import net.java.games.input.Component;

public class DigitalInput extends Input {
  // TODO: Fix constructor
  public DigitalInput(Component component) {
    super(component, 0);
  }

  public boolean isActive() {
    return component.getPollData() > 0;
  }
}
