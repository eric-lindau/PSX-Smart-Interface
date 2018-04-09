package com.lindautech.psx.data.input;

public class InputOption {
  private String name;

  public InputOption(String name) {
    this.name = name;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof InputOption) && (toString().equals(obj.toString()));
  }
}
