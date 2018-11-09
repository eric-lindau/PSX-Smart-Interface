package com.lindautech.psx.data.processing.input;

import net.java.games.input.Component;

public class AnalogInput extends AbstractInput {
  private boolean isInverted;

  // TODO: Necessary to specify range so that 0.0->1.0 value can be properly scaled
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

  @Override
  public Object processed() {
    return null;
  }
}
