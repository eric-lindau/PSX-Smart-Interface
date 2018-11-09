package com.lindautech.psx.data.processing.values;

import com.lindautech.psx.data.processing.input.AbstractInput;

public class ExplicitValue extends AbstractValue {
  public ExplicitValue(AbstractInput[] inputs) {
    // TODO: Throw error if inputs.length != 1
    super(inputs);
  }

  // This returns an integer for clarity... Same for CombinedValue; adds integers, not random objects
  @Override
  public Integer processed() {
    // TODO: Ensure processed input is integer; otherwise, throw error
    return (Integer) inputs[0].processed();
  }

  @Override
  public void update() {

  }
}
