package com.lindautech.psx.data.processing.input;

import net.java.games.input.Component;

// TODO: Detect changes from active/inactive; this is what will be polled and send change propagation up
// TODO: *** Eventually allow toggled option (much like inverted option for analogs) for each digital input
// TODO: ... ***  then remove toggle processing in ButtonCluster; only radio; this will be very useful for users
public class DigitalInput extends AbstractInput {
  // Examples: "-1/0/1", "w/W"
  private final String active, inactive;

  private volatile boolean currentState;

  // TODO: Fix constructor
  public DigitalInput(Component component, String active, String inactive) {
    super(component, 0);
    this.active = active;
    this.inactive = inactive;
    this.currentState = false;
    poll();
  }

  public boolean isActive() {
    return component.getPollData() > 0;
  }

  public String getActive() {
    return this.active;
  }

  public String getInactive() {
    return this.inactive;
  }

  @Override
  public void poll() {
    boolean newState = isActive();
    if (currentState != newState) {
      currentState = newState;
      propagate();
    }
  }

  @Override
  public synchronized String processed() {
    if (currentState) {
      return getActive();
    } else {
      return getInactive();
    }
  }
}
