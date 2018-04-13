package com.lindautech.psx.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;

class EntryPanel extends JPanel implements Entry {
  private JLabel nameLabel;
  private JLabel valueLabel;
  private JCheckBox invertedCheckBox;
  private JComboBox optionComboBox;

  EntryPanel(String name, boolean invertible) {
    setLayout(new GridLayout(1, 4, 0, 0));
    nameLabel = new JLabel(name);
    if (invertible) {
      invertedCheckBox = new JCheckBox("Inverted");
    }
    valueLabel = new JLabel();
    optionComboBox = new JComboBox();
    addComponents();
  }

  private void addComponents() {
    add(nameLabel);
    if (invertedCheckBox != null) {
      add(invertedCheckBox);
    } else {
      add(new JLabel(""));
    }
    add(valueLabel);
    add(optionComboBox);
  }

  void addInvertedListener(ItemListener listener) {
    invertedCheckBox.addItemListener(listener);
  }

  void addOptionListener(ItemListener listener) {
    optionComboBox.addItemListener(listener);
  }

  void setOption(String option) {
    optionComboBox.setSelectedItem(option);
  }

  @Override
  public void setValueText(String text) {
    valueLabel.setText(text);
  }
}
