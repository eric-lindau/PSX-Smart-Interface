package com.lindautech.psx.ui;

import javax.swing.*;

public abstract class EntryPanel extends JPanel {
  private JLabel nameLabel;
  private JComboBox optionComboBox;
  private JLabel valueLabel;
  private final int id;

  EntryPanel(String name, int id) {
    nameLabel = new JLabel(name);
    optionComboBox = new JComboBox();
    valueLabel = new JLabel();
    this.id = id;
  }

  JComboBox getOptionComboBox() {
    return optionComboBox;
  }

  @Override
  public int hashCode() {
    return id;
  }
}
