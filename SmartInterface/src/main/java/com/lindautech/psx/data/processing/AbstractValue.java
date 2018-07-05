package com.lindautech.psx.data.processing;

// TODO: Make all values event driven
abstract class AbstractValue implements Value {
  String name;
  private int[] currentData;

  AbstractValue(String name) {
    this.name = name;
  }

  // TODO: Do something when inner if passes (boolean or separate check?)
  void setCurrentData(int[] newData) {
    if (newData.length == currentData.length) {
      for (int i = 0; i < newData.length; i++) {
        if (newData[i] != currentData[i]) {
          currentData[i] = newData[i];
        }
      }
    } else {
      // TODO: Replace with custom exception
      throw new RuntimeException();
    }
  }

  void setCurrentData(int newData) {
    if (currentData.length == 1) {
      if (currentData[0] != newData) {
        currentData[0] = newData;
      }
    } else {
      // TODO: Replace with custom exception
      throw new RuntimeException();
    }
  }

  @Override
  public String getCompiledData() {
    StringBuilder compiledData = new StringBuilder(name);
    for (int i = 0; i < currentData.length; i++) {
      compiledData.append(Integer.toString(currentData[i]));
      if (i + 1 < currentData.length) {
        compiledData.append(';');
      }
    }
    return compiledData.toString();
  }
}
