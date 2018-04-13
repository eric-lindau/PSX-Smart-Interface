package com.lindautech.psx.ui;

import javax.swing.*;

final class DigitalEntryPanel extends EntryPanel {
  private JCheckBox invertedCheckBox;

  public DigitalEntryPanel(String name, int id) {
    super(name, id);
    invertedCheckBox = new JCheckBox("Inverted");
  }

  public JCheckBox getInvertedCheckBox() {
    return invertedCheckBox;
  }
}
