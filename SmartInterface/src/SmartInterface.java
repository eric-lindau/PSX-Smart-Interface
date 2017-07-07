import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Controller;
import net.java.games.input.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * PSX Smart Interface: This add-on allows a user to differentiate hardware
 * components as either "Captain" or "First Officer" controls, combining
 * inputs logically while interfacing with the main PSX server. This allows
 * physical setups without mechanically synchronized pilot and co-pilot
 * controls to operate logically.
 * This class handles all parts of the program, including interfacing, UI, and
 * calculations.
 *
 * @author Eric Lindau
 * @version 1.1
 */
class SmartInterface {

    // PSX network client
    private static Client client;

    // Stores usable controllers
    private static ArrayList<Controller> controllers;
    // Stores all components from usable controllers for polling
    private static ArrayList<Component> components = new ArrayList<>();
    // Stores all labels for modification
    private static ArrayList<JLabel> valueLabels;

    // Stores current working component for memory efficiency
    private static Component currComponent;
    // Stores current working label for memory efficiency
    private static JLabel currLabel;

    //* Stored components for calculations, as specified by the user
    public static Component aileronCpt, aileronFo;
    public static Component elevatorCpt, elevatorFo;
    public static Component rudderCpt, rudderFo;
    public static Component tillerCpt, tillerFo;
    //*

    // Standard main
    public static void main(String[] args) {
        // Connect to server
        client = new Client("localhost", 10747);
        client.start();

        getControllers();
        initUI();

        try {
            while(true)
                update();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Inflate value to working range (-999:999) and mod to prevent over 1000
    private static int getAnalogValue(Component component) {
        return Math.round(component.getPollData() * 999) % 1000;
    }

    // Check if non-analog component is "pushed" (buttons)
    private static boolean isPushed(Component component) {
        return component.getPollData() > 0;
    }

    // Gracefully calculate combined value of two analog inputs to prevent
    // "fighting"
    public static int combineAnalog(Component first, Component second) {
        //* Start at 0, add both values, then check bounds
        int ret = 0;
        if(first != null)
            ret += getAnalogValue(first);
        if(second != null)
            ret += getAnalogValue(second);
        if(ret < -999)
            ret = -999;
        else if(ret > 999)
            ret = 999;
        //*

        return ret;
    }

    // Constantly poll controllers, update labels, update PSX server
    private static void update() {
        try {
            //* Poll controllers
            for (Controller controller : controllers)
                controller.poll();
            //*

            //* Update labels
            for (int i = 0; i < components.size(); i++) {
                currLabel = valueLabels.get(i);
                currComponent = components.get(i);
                if (!currComponent.isAnalog())
                    if (isPushed(currComponent))
                        currLabel.setText("Pushed");
                    else
                        currLabel.setText("");
                else
                    currLabel.setText(Integer.toString(getAnalogValue(currComponent)));
            }
            //*

            // Delay to prevent too much CPU usage
            // TODO See if this delay can be greater
            Thread.sleep(1);
        } catch(Exception e) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                    e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Get usable controllers (no keyboards or mice preferably)
    private static void getControllers() {
        // Copy controllers into ArrayList so they can be removed easily
        controllers = new ArrayList<>(Arrays.asList(ControllerEnvironment.getDefaultEnvironment()
                .getControllers()));
        for(int i = 0; i < controllers.size(); i++)
            if(shouldIgnore(controllers.get(i))) {
                controllers.remove(i);
                i--;
            }
    }

    // Initialize user interface in grid layout
    private static void initUI() {
        //* JPanel init and config
        JPanel panel = new JPanel();
        //*

        //* Looped addition of each component to UI
        // JLabel/JComboBox declarations used to temporarily/efficiently hold
        // swing components as they are added to the panel
        ComboBox comboBox;
        JLabel label;
        // Permanent array initialized to be used for options in each JComboBox
        String[] dropBoxStrings = {"None", "Aileron (1)", "Aileron (2)",
                "Elevator (1)", "Elevator (2)", "Rudder (1)", "Rudder(2)",
                "Tiller (1)", "Tiller (2)"};
        // Permanent ArrayList initialized so that labels can be constantly
        // referenced and updated
        valueLabels = new ArrayList<>();
        // Counter used to number component labels
        int counter = 0;

        //* Detect and react to JComboBox changes
        // TODO: Fix setting back to "None"
        ItemListener itemListener = new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent) {
                // Only change component if new item selected, not unselected
                if(itemEvent.getStateChange() == ItemEvent.SELECTED) {
                    ComboBox combo = (ComboBox) itemEvent.getSource();
                    int index = combo.getIndex();
                    // Switch to determine what this component should be used for
                    switch ((String) itemEvent.getItem()) {
                        case "Aileron (1)":
                            aileronCpt = components.get(index);
                            break;
                        case "Aileron (2)":
                            aileronFo = components.get(index);
                            break;
                        case "Elevator (1)":
                            elevatorCpt = components.get(index);
                            break;
                        case "Elevator (2)":
                            elevatorFo = components.get(index);
                            break;
                        case "Rudder (1)":
                            rudderCpt = components.get(index);
                            break;
                        case "Rudder(2)":
                            rudderFo = components.get(index);
                            break;
                        case "Tiller (1)":
                            tillerCpt = components.get(index);
                            break;
                        case "Tiller(2)":
                            tillerFo = components.get(index);
                            break;
                    }
                }
            }
        };
        //*

        //* Go through each controller and add its components to the UI
        for(Controller controller : controllers) {
            for(Component component : controller.getComponents()) {
                // Add component to master list of components
                components.add(component);

                // Label #1: Current number and name of component
                label = new JLabel("  " + Integer.toString(components.size() - 1) + ". " +
                        controller.getName() + " - " +  component.getName());
                panel.add(label);
                counter++;

                // Label #2: Current value of component
                label = new JLabel("", SwingConstants.CENTER);
                panel.add(label);
                valueLabels.add(label);

                // ComboBox: Select which components act for which operations
                comboBox = new ComboBox(dropBoxStrings, components.size() - 1);
                comboBox.addItemListener(itemListener);
                panel.add(comboBox);
            }
        }
        //*

        //* Grid layout setup
        GridLayout grid = new GridLayout(components.size(), 3, 10, 0);
        panel.setLayout(grid);
        panel.setPreferredSize(new Dimension(780, components.size() * 30));
        //*

        //* JScrollPane init and config
        JScrollPane scrollPane = new JScrollPane(panel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // 569 used to offset width of bar itself
        scrollPane.setPreferredSize(new Dimension(800, 569));
        //*

        //* JFrame init and config
        JFrame frame = new JFrame("PSX SmartInterface");
        frame.setPreferredSize(new Dimension(800, 600));
        frame.setResizable(false);
        frame.getContentPane().add(scrollPane);
        // Pack to ensure preferred sizes are used
        frame.pack();
        // Exit program if the "X" is clicked
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
                client.destroyConnection();
            }
        });
        frame.setVisible(true);
        //*
    }

    // Determine if controller should be ignored (otherwise it is usable)
    private static boolean shouldIgnore(Controller controller) {
        return controller.getName().toUpperCase().contains("KEYBOARD") ||
                controller.getName().toUpperCase().contains("MOUSE") ||
                controller.getName().toUpperCase().contains("RAZER");
    }

}
