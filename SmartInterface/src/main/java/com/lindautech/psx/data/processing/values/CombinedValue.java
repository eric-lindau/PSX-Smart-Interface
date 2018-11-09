package com.lindautech.psx.data.processing.values;

import com.lindautech.psx.data.processing.input.AbstractInput;

public class CombinedValue extends AbstractValue {
  private int lowerBound, upperBound;

  public CombinedValue(AbstractInput[] inputs, int lowerBound, int upperBound) {
    super(inputs);
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }

  @Override
  public Integer processed() {
    int accumulator = 0;
    for (AbstractInput input : inputs) {
      // TODO: Throw error if input not int (how to handle this?)
      accumulator += (Integer) input.processed();
    }
    if (accumulator < lowerBound) {
      return lowerBound;
    } else if (accumulator > upperBound) {
      return upperBound;
    } else {
      return accumulator;
    }
  }

  @Override
  public void update() {

  }
}
