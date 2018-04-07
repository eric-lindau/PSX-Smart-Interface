package com.lindautech.psx.data;

public class ValueOption {
  private String name;

  public ValueOption(String name) {
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
}
