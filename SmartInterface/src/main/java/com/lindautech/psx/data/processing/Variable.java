package com.lindautech.psx.data.processing;

public class Variable {
  private String name;
  private Value[] values;

  public Variable(String name, Value[] values) {
    this.name = name;
    this.values = values;
  }

  public void update() {
    boolean shouldNotify = false;
    for (Value value : values) {
      if (value.update()) {
        shouldNotify = true;
      }
    }
    if (shouldNotify) {
      // TODO: Notify listeners
      shouldNotify = false;
    }
  }
}
