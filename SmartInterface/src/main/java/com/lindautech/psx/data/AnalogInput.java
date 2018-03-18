package com.lindautech.psx.data;

import net.java.games.input.Component;

public class AnalogInput extends Input {
  private boolean isInverted;

  public AnalogInput(Component component) {
    super(component);
  }

  // TODO: Add listener on event change (maybe in parent class?)
  // TODO: Add toString / method for setting text in UI
  @Override
  public int pollData() {
    // TODO: Maybe move this to new Input common Input method
    // TODO: and make component private to Input
    int currentValue = Math.round(component.getPollData());
    return currentValue;
//    if (isInverted) {
////      return value * -1;
//    } else {
////      return value;
//    }
  }

  @Override
  public void addChangeListener() {

  }
}
