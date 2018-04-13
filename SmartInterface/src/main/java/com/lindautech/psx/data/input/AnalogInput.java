package com.lindautech.psx.data.input;

import net.java.games.input.Component;

public class AnalogInput extends Input {
  private boolean isInverted;
  private int multiplier;

  public AnalogInput(Component component) {
    super(component);
    isInverted = false;
    multiplier = 999;
  }

  public AnalogInput(Component component, int multiplier) {
    super(component);
    isInverted = false;
    this.multiplier = multiplier;
  }

  // TODO: Add toString / method for setting text in UI
  @Override
  public int pollData() {
    return Math.round(component.getPollData() * multiplier) * (isInverted ? -1 : 1);
  }

  public void setInverted(boolean isInverted) {
    this.isInverted = isInverted;
  }

  @Override
  public String toString() {
    return Integer.toString(pollData());
  }
}
