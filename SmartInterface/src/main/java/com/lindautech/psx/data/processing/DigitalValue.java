package com.lindautech.psx.data.processing;

import com.lindautech.psx.data.input.DigitalInput;

class DigitalValue extends AbstractValue {
  private DigitalInput button;

  DigitalValue(String name) {
    super(name);
  }

  @Override
  public void update() {
    if (button.isActive()) {
      setCurrentData(1);
    } else {
      setCurrentData(0);
    }
  }

}
