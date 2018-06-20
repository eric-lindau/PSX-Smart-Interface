package com.lindautech.psx;

import com.lindautech.psx.data.input.InputOption;
import com.lindautech.psx.ui.Manager;
import com.lindautech.psx.ui.PrimaryPanel;
import org.junit.Test;
import org.junit.Assert;

import javax.swing.*;
import java.awt.*;

public class BasicUI {
  @Test
  public void start() {
    PrimaryPanel primaryPanel = new PrimaryPanel();
    Assert.assertNotNull(primaryPanel);
    InputOption[] inputOptions = new InputOption[]{new InputOption("Test")};
    Manager manager = new Manager(primaryPanel, inputOptions);
    JFrame window = new JFrame();
    window.setSize(new Dimension(800, 600));
    window.setResizable(false);
    window.getContentPane().add(primaryPanel);
    window.setVisible(true);
  }
}
