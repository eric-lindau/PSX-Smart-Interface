package com.lindautech.psx.data.input;

import net.java.games.input.Component;

public class AnalogInput extends Input {
  private boolean isInverted;

  public AnalogInput(Component component) {
    super(component, 0);
    isInverted = false;
  }

  public int pollData() {
    int pollData = Math.round(component.getPollData());
    if (isInverted) {
      pollData *= -1;
    }
    return pollData;
  }

  public void setInverted(boolean isInverted) {
    this.isInverted = isInverted;
  }
}
