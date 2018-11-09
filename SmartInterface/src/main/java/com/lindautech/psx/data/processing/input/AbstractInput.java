package com.lindautech.psx.data.processing.input;

import com.lindautech.psx.data.state.ChangePropagator;
import net.java.games.input.Component;

// TODO: To save these in between loads, consider controller type, number of components,
// TODO: ... all possible info retrievable from JInput... make it more dynamic than checking if
// TODO: ... entire list is exactly the same (this is unreliable).
public abstract class AbstractInput extends ChangePropagator implements Input {
  Component component;
  private int number;
  private Integer value;

  AbstractInput(Component component, int number) {
    this.component = component;
    this.number = number;
  }

  // TODO: Maybe convert to interface; use interface in Value(s)
  @Override
  public int hashCode() {
    return number;
  }

  @Override
  public String toString() {
    return Integer.toString(number) + ". " + component.getName();
  }
}
