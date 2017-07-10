import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Controller;
import net.java.games.input.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * PSX Smart Interface: This add-on allows a user to differentiate hardware
 * components as either "Captain" or "First Officer" controls, combining
 * inputs logically while interfacing with the main PSX server. This allows
 * physical setups without mechanically synchronized pilot and co-pilot
 * controls to operate logically.
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

    //* Stored components for use, as specified by the user
    // Flight controls
    static Component aileronCpt, aileronFo;
    static Component elevatorCpt, elevatorFo;
    static Component rudderCpt, rudderFo;

    // Tillers
    static Component tillerCpt, tillerFo;

    // Toe brakes
    static Component toeBrakeLCpt, toeBrakeRCpt;
    static Component toeBrakeLFo, toeBrakeRFo;

    // Misc buttons
    static Component stabTrimUpCpt, stabTrimDownCpt;
    static Component stabTrimUpFo, stabTrimDownFo;
    static Component apDisc;
    static Component lcpPttCpt, lcpPttFo;

    // Radar panel buttons
    static Component tfrCpt;
    static Component wxCpt, wxtCpt;
    static Component mapCpt, gcCpt;
    static Component auto, lr, test;
    static Component tfrFo;
    static Component wxFo, wxtFo;
    static Component mapFo, gcFo;
    //*

    // Standard main
    public static void main(String[] args) {
        // Connect to server
        client = new Client("localhost", 10747);
        client.start();

        getControllers();
        initUI();

        try {
            while (true)
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
    static boolean isPushed(Component component) {
        return component != null && component.getPollData() > 0;
    }

    // Gracefully calculate combined value of two analog inputs to prevent conflicts
    static int combineAnalog(Component first, Component second) {
        //* Start at 0, add both values, then check bounds
        int ret = 0;
        if (first != null)
            ret += getAnalogValue(first);
        if (second != null)
            ret += getAnalogValue(second);
        if (ret < -999)
            ret = -999;
        else if (ret > 999)
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

            // Delay to reduce CPU usage
            Thread.sleep(5);
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
        for (int i = 0; i < controllers.size(); i++)
            if (shouldIgnore(controllers.get(i))) {
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
        String[] dropBoxStrings = new String[]{"None",
                "Aileron Capt", "Aileron F/O",
                "Elevator Capt", "Elevator F/O",
                "Rudder Capt", "Rudder F/O",
                "Tiller Capt", "Tiller F/O",
                "Toe Brake Left Capt", "Toe Brake Right Capt",
                "Toe Brake Left F/O", "Toe Brake Right F/O",
                "Stab Trim UP Capt", "Stab Trim DN Capt",
                "Stab Trim UP F/O", "Stab Trim DN F/O",
                "AP Disc",
                "PTT Capt", "PTT F/O",
                "TFR Capt", "WX Capt", "WX+T Capt",
                "MAP Capt", "GC Capt",
                "AUTO", "L/R", "TEST",
                "TFR F/O", "WX F/O", "WX+T F/O",
                "MAP F/O", "GC F/O"
        };
        // Permanent ArrayList initialized so that labels can be constantly
        // referenced and updated
        valueLabels = new ArrayList<>();
        // Counter used to number component labels

        // Detect and react to JComboBox changes
        ItemListener itemListener = new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent) {
                // Only change component if new item selected, not unselected
                ComboBox combo = (ComboBox) itemEvent.getSource();
                int index = combo.getIndex();
                // Switch to determine what this component should be used for
                switch ((String) itemEvent.getItem()) {
                    case "None":
                        break;
                    case "Aileron Capt":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            aileronCpt = components.get(index);
                        else
                            aileronCpt = null;
                        break;
                    case "Aileron F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            aileronFo = components.get(index);
                        else
                            aileronFo = null;
                        break;
                    case "Elevator Capt":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            elevatorCpt = components.get(index);
                        else
                            elevatorCpt = null;
                        break;
                    case "Elevator F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            elevatorFo = components.get(index);
                        else
                            elevatorFo = null;
                        break;
                    case "Rudder Capt":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            rudderCpt = components.get(index);
                        else
                            rudderCpt = null;
                        break;
                    case "Rudder F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            rudderFo = components.get(index);
                        else
                            rudderFo = null;
                        break;
                    case "Tiller Capt":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            tillerCpt = components.get(index);
                        else
                            tillerCpt = null;
                        break;
                    case "Tiller F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            tillerFo = components.get(index);
                        else
                            tillerFo = null;
                        break;
                    case "Toe Brake Left Capt":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            toeBrakeLCpt = components.get(index);
                        else
                            toeBrakeLCpt = null;
                        break;
                    case "Toe Brake Right Capt":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            toeBrakeRCpt = components.get(index);
                        else
                            toeBrakeRCpt = null;
                        break;
                    case "Toe Brake Left F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            toeBrakeLFo = components.get(index);
                        else
                            toeBrakeLFo = null;
                        break;
                    case "Toe Brake Right F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            toeBrakeRFo = components.get(index);
                        else
                            toeBrakeRFo = null;
                        break;
                    case "Stab Trim UP Capt":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            stabTrimUpCpt = components.get(index);
                        else
                            stabTrimUpCpt = null;
                        break;
                    case "Stab Trim DN Capt":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            stabTrimDownCpt = components.get(index);
                        else
                            stabTrimDownCpt = null;
                        break;
                    case "Stab Trim UP F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            stabTrimUpFo = components.get(index);
                        else
                            stabTrimUpFo = null;
                        break;
                    case "Stab Trim DN F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            stabTrimDownFo = components.get(index);
                        else
                            stabTrimDownFo = null;
                        break;
                    case "AP Disc":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            apDisc = components.get(index);
                        else
                            apDisc = null;
                        break;
                    case "PTT Capt":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            lcpPttCpt = components.get(index);
                        else
                            lcpPttCpt = null;
                        break;
                    case "PTT F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            lcpPttFo = components.get(index);
                        else
                            lcpPttFo = null;
                        break;
                    case "TFR Capt":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            tfrCpt = components.get(index);
                        else
                            tfrCpt = null;
                        break;
                    case "WX Capt":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            wxCpt = components.get(index);
                        else
                            wxCpt = null;
                        break;
                    case "WX+T Capt":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            wxtCpt = components.get(index);
                        else
                            wxtCpt = null;
                        break;
                    case "MAP Capt":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            mapCpt = components.get(index);
                        else
                            mapCpt = null;
                        break;
                    case "GC Capt":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            gcCpt = components.get(index);
                        else
                            gcCpt = null;
                        break;
                    case "AUTO":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            auto = components.get(index);
                        else
                            auto = null;
                        break;
                    case "L/R":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            lr = components.get(index);
                        else
                            lr = null;
                        break;
                    case "TEST":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            test = components.get(index);
                        else
                            test = null;
                        break;
                    case "TFR F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            tfrFo = components.get(index);
                        else
                            tfrFo = null;
                        break;
                    case "WX F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            wxFo = components.get(index);
                        else
                            wxFo = null;
                        break;
                    case "WX+T F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            wxtFo = components.get(index);
                        else
                            wxtFo = null;
                        break;
                    case "MAP F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            mapFo = components.get(index);
                        else
                            mapFo = null;
                        break;
                    case "GC F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            gcFo = components.get(index);
                        else
                            gcFo = null;
                        break;
                    default:
                        JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                                "SWITCH ERROR IN CODE - NOTIFY PROGRAMMER");
                        break;
                }
            }
        };

        // Go through each controller and add its components to the UI
        for (Controller controller : controllers) {
            for (Component component : controller.getComponents()) {
                // Add component to master list of components
                components.add(component);

                // Label #1: Current number and name of component
                label = new JLabel("  " + Integer.toString(components.size() - 1) + ". " +
                        controller.getName() + " - " +  component.getName());
                panel.add(label);

                // Label #2: Current value of component
                label = new JLabel("", SwingConstants.CENTER);
                panel.add(label);
                valueLabels.add(label);

                // ComboBox: Select which components act for which operations
                comboBox = new ComboBox(dropBoxStrings, components.size() - 1);
                comboBox.setMaximumRowCount(20);
                comboBox.addItemListener(itemListener);
                panel.add(comboBox);
            }
        }

        //* Grid layout setup
        //TODO Add another column for "Neutral" (dead zone)
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
        JFrame frame = new JFrame("PSX SmartInterface v1.1");
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
                controller.getName().toUpperCase().contains("MOUSE");
    }

}
