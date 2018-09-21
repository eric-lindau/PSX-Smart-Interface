package com.lindautech.psx.data.processing;

import com.lindautech.psx.data.input.Input;

public abstract class Value {
  private Input[] inputs;

  public Value(Input[] inputs) {
    this.inputs = inputs;
  }

  // TODO: Implement (return true if changed)
  // To detect if changed: Store previous value, find new value, compare & return
  // Updates should be detected here because multiple combinations of Input changes can lead to the same result (i.e. 1+2 and 2+1 = 3)
  abstract boolean update();
}
