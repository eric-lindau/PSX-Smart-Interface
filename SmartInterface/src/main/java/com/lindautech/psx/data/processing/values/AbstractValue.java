package com.lindautech.psx.data.processing.values;

import com.lindautech.psx.data.processing.input.AbstractInput;
import com.lindautech.psx.data.state.ChangePropagator;

// TODO: Maybe convert to interface down the line? And include abstract class in order to set
// TODO: ... inputs / have common constructor
public abstract class AbstractValue extends ChangePropagator implements Value {
  AbstractInput[] inputs;

  AbstractValue(AbstractInput[] inputs) {
    this.inputs = inputs;
  }

  public abstract Object processed();
}
