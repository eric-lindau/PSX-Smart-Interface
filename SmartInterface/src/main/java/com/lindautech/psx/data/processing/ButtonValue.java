package com.lindautech.psx.data.processing;

import com.lindautech.psx.data.input.DigitalInput;

class ButtonValue extends AbstractValue {
  private DigitalInput button;

  ButtonValue(String name) {
    super(name);
  }

  @Override
  public void update() {
    if (button.isPushed()) {
      setCurrentData(new int[]{1});
    } else {
      setCurrentData(new int[]{0});
    }
  }

}
