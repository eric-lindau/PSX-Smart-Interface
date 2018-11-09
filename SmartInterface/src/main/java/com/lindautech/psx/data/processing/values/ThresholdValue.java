package com.lindautech.psx.data.processing.values;

import com.lindautech.psx.data.processing.input.AnalogInput;

// TODO: User defines thresholds; directly translated
public class ThresholdValue extends AbstractValue {
  private int lowerBound, upperBound;
  private int[] thresholds, values;

  // Thresholds/values MUST be sorted and match each other
  public ThresholdValue(AnalogInput[] inputs, int lowerBound, int upperBound,
                        int[] thresholds, int[] values) {
    super(inputs);
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    this.thresholds = thresholds;
    this.values = values;
  }

  @Override
  public Integer processed() {
    for (int i = 0; i < thresholds.length; i++) {
      if ((Integer) ((AnalogInput) inputs[0]).processed() <= thresholds[i]) {
        return values[i];
      }
    }
    // TODO: Determine if this is the right thing to do... Maybe verify first/last threshold values
    // TODO: ... == upper/lowerBound?
    return values[values.length - 1];
  }

  @Override
  public void update() {

  }
}
