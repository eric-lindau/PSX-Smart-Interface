package com.lindautech.psx.data.input;

import net.java.games.input.Component;

public class AnalogInput extends AbstractInput {
  private boolean isInverted;
  private int multiplier;

  public AnalogInput(Component component) {
    super(component, 0);
    isInverted = false;
    multiplier = 999;
  }

  public AnalogInput(Component component, int multiplier) {
    super(component, 0);
    isInverted = false;
    this.multiplier = multiplier;
  }

  public int pollData() {
    return Math.round(component.getPollData() * multiplier) * (isInverted ? -1 : 1);
  }

  public void setInverted(boolean isInverted) {
    this.isInverted = isInverted;
  }
}
