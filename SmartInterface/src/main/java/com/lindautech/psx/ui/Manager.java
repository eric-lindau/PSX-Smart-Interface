package com.lindautech.psx.ui;

import com.lindautech.psx.data.input.AnalogInput;
import com.lindautech.psx.data.input.DigitalInput;
import com.lindautech.psx.data.input.Input;
import com.lindautech.psx.data.input.InputOption;
import net.java.games.input.Component;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

/** Creates and links UI components to their respective data models. */
public class Manager implements Runnable {
  // Model - View relationship is made here
  private HashMap<Input, EntryPanel> inputs;
  // Manages combo boxes
  private HashMap<InputOption, EntryPanel> activeInputs;
  private PrimaryPanel primaryPanel;
  private int currentId;

  public Manager(PrimaryPanel primaryPanel, InputOption[] options) {
    activeInputs = new HashMap<InputOption, EntryPanel>();
    for (InputOption option : options) {
      activeInputs.put(option, null);
    }
    this.primaryPanel = primaryPanel;
    currentId = 0;
  }

  @Override
  public void run() {
    updateEntries();
  }

  /** Updates the presented value for each UI entry. */
  private void updateEntries() {
    for (Map.Entry<Input, EntryPanel> entry : inputs.entrySet()) {
      Input key = entry.getKey();
      EntryPanel value = entry.getValue();

      // If the input is known to be analog
      if (key instanceof AnalogInput) {
        // Use the literal numeric value
        int pollData = ((AnalogInput) key).pollData();
        value.setValueText(Integer.toString(pollData));
      // Otherwise, if it is known to be digital
      } else {
        if (((DigitalInput) key).isPushed()) {
          value.setValueText("Pushed");
        } else {
          value.setValueText("");
        }
      }
    }
  }

  /** Registers a new component. */
  public void registerComponent(Component component) {
    if (component.isAnalog()) {
      primaryPanel.add(registerAnalogEntry(component));
    } else {
      primaryPanel.add(registerDigitalEntry(component));
    }
  }

  // TODO: RegisterDigitalInput?
  private EntryPanel registerDigitalEntry(Component component) {
    EntryPanel panel = new EntryPanel(component.getName(), false);
    panel.addInvertedListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          // TODO: Invert Input
          int i = 0;
        }
      }
    });
    return panel;
  }

  // TODO: Perform Value swaps with Inputs
  private EntryPanel registerAnalogEntry(Component component) {
    EntryPanel panel = new EntryPanel(component.getName(), true);
    panel.addOptionListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED && !e.getItem().equals("None")) {
          // De-select item from the combo box (if it exists) that already has this item selected
          InputOption selectedOption = (InputOption) e.getItem();
          EntryPanel previousAssociatedEntry = activeInputs.get(selectedOption);
          if (previousAssociatedEntry != null) {
            previousAssociatedEntry.setOption("None");
          }
          // Swap proper Component in Value based on InputOption
        }
      }
    });
    return panel;
  }
}
