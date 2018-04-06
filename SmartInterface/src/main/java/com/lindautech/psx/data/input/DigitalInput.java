package com.lindautech.psx.data.input;

import net.java.games.input.Component;

public class DigitalInput extends Input {
  public DigitalInput(Component component) {
    super(component);
  }

  @Override
  public int pollData() {
    return 0;
  }
}
