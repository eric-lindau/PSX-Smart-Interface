package com.lindautech.psx.data.input;

import net.java.games.input.Component;

public class AnalogInput extends Input {
  private boolean isInverted;

  public AnalogInput(Component component) {
    super(component);
    isInverted = false;
  }

  // TODO: Add toString / method for setting text in UI
  @Override
  public int pollData() {
    int value = Math.round(component.getPollData() * 1000);
    if (isInverted) {
      return value * -1;
    } else {
      return value;
    }
  }

  public void setInverted(boolean isInverted) {
    this.isInverted = isInverted;
  }

  @Override
  public String toString() {
    // TODO: Implement this
    return super.toString();
  }
}
