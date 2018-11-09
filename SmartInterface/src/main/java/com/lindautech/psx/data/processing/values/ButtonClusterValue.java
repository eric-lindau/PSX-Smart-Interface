package com.lindautech.psx.data.processing.values;

import com.lindautech.psx.data.processing.input.DigitalInput;

public class ButtonClusterValue extends AbstractValue {
  private int[][] radioIndices;
  private int[] toggleIndices;
  private int[] normalIndices;

  private boolean[] currentState;
  private boolean[] toggleUnpressed;

  //TODO: ***** Assume single character isActive value for all inputs here
  // TODO: **** THROW ERROR when user tries to use multi-char value for button cluster (ONLY ALLOWED FOR ButtonValue)
//  private final Object[][] defaultRadioInputs;

  // TODO: Check if radio/toggle shouldn't be the same? Or make option for both at the same time
  public ButtonClusterValue(DigitalInput[] inputs, int[][] radioIndices, int[] toggleIndices) {
    super(inputs);
    // TODO: Verify # of indices <= # inputs
    // TODO: If not index or input, regular
    this.radioIndices = radioIndices;
    this.toggleIndices = toggleIndices;
    // TODO: Make sure can access radioIndices[0] and neither is null (implement error checking)
    // TODO: Process this more
    this.normalIndices = new int[inputs.length];
    this.toggleUnpressed = new boolean[toggleIndices.length];
//    if (radioIndices != null && radioIndices.length > 0 && radioIndices[0].length > 0) {
//      this.defaultRadioInputs = new Object[radioIndices.length][radioIndices[0].length];
//      for (int i = 0; i < defaultRadioInputs.length; i++) {
//        for (int j = 0; j < defaultRadioInputs[0].length; j++) {
//          defaultRadioInputs[i][j] = inputs[radioIndices[i][j]].processed();
//        }
//      }
//    } else {
//      defaultRadioInputs = new Object[][]{{}};
//    }
  }

  private void processRadio(int[] radioIndices, boolean[] toProcess) {
    // TODO: Check if < 1
    int currIndex = radioIndices[0];
    boolean noneActive = true;
    for (int index : radioIndices) {
      if (noneActive) {
        boolean activeAtIndex = ((DigitalInput) inputs[index]).isActive();
        toProcess[index] = activeAtIndex;
        noneActive = !activeAtIndex;
      } else {
        toProcess[index] = false;
      }
    }
  }

  @Override
  // Returns String always, since it's a cluster of many weird buttons
  public String processed() {
    return null;
//    char[] toProcess = new char[inputs.length];
//    // TODO: Check for characters that are still null once radio/toggle taken care of (toggle should be subclass of digital)
//    for (int[] radioIndexArray : radioIndices) {
//      processRadio(radioIndexArray, toProcess);
//    }
//    for (int toggleIndex : toggleIndices) {
//      DigitalInput currInput = (DigitalInput) inputs[toggleIndex];
//      if (!toggleUnpressed[toggleIndex] && !currInput.isActive()) {
//        toggleUnpressed[toggleIndex] = true;
//      }
//      if (toggleUnpressed[toggleIndex] && currInput.isActive()) {
//        toggleUnpressed[toggleIndex] = false;
//        toProcess[toggleIndex] = currInput.processed().charAt(0);
//      }
//    }
//    return new String(toProcess);
  }

  @Override
  public void update() {
    boolean[] newState = new boolean[currentState.length];
    for (int[] radioIndexArray : radioIndices) {
      processRadio(radioIndexArray, newState);
    }
    // TODO: Then process toggles (not necessary eventually)
    // TODO: Then process regulars
    boolean shouldPropagate = false;
    for (int i = 0; i < currentState.length; i++) {
      if (currentState[i] != newState[i]) {
        shouldPropagate = true;
        currentState[i] = newState[i];
      }
    }
    if (shouldPropagate) {
      propagate();
    }
  }
}
