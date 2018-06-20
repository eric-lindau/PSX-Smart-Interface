package com.lindautech.psx.data.processing;

// TODO: Make all values event driven
abstract class AbstractValue implements Value {
  String name;
  private int[] currentData;

  AbstractValue(String name) {
    this.name = name;
  }

  int[] getCurrentData() {
    return currentData;
  }

  void setCurrentData(int[] newData) {
    boolean shouldUpdateListeners = false;

    if (newData.length == currentData.length) {
      for (int i = 0; i < newData.length; i++) {
        if (newData[i] != currentData[i]) {
          currentData[i] = newData[i];
          shouldUpdateListeners = true;
        }
      }
    } else {
      // TODO: Replace with custom exception
      throw new RuntimeException();
    }

    if (shouldUpdateListeners) {
      // TODO: Update listeners
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
