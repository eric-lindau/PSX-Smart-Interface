package com.lindautech.psx.data.processing.values;

import com.lindautech.psx.data.processing.input.DigitalInput;

// TODO: Should have default value when none of inputs "active" (usually 0; covers 2-way switches and normal buttons)
public class ButtonValue extends AbstractValue {
  private String defaultValue;

  // TODO: Consider abstracting some state tracking away more
  private int currentState;

  public ButtonValue(DigitalInput[] inputs, String defaultValue) {
    super(inputs);
    this.defaultValue = defaultValue;
    this.currentState = -1;
  }

  @Override
  // String necessary for active values like "-1"
  public String processed() {
    if (currentState == -1) {
      return defaultValue;
    } else {
      return (String) inputs[currentState].processed();
    }
  }

  @Override
  public void update() {
    int newState = -1;
    for (int i = 0; i < inputs.length; i++) {
      if (((DigitalInput) inputs[i]).isActive()) {
        newState = i;
        break;
      }
    }
    // TODO: This would be the part to abstract away (currentState should always be single/array of primitives)
    if (currentState != newState) {
      currentState = newState;
      propagate();
    }
  }
}
