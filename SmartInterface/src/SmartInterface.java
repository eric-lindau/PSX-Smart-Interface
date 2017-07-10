import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Controller;
import net.java.games.input.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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

    private static boolean running = true;

    // PSX network client
    private static Client client;

    // Stores usable controllers
    private static ArrayList<Controller> controllers;
    // Stores all components from usable controllers for polling
    private static ArrayList<Component> components = new ArrayList<>();
    private static ArrayList<String> savedComponents = new ArrayList<>();
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
            while (running)
                update();
            stop();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void stop() throws UnsupportedEncodingException, FileNotFoundException {
        client.destroyConnection();

        PrintWriter output = new PrintWriter("saved_config.txt", "UTF-8");

        output.close();
        System.exit(0);
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
                    if (Utils.isPushed(currComponent))
                        currLabel.setText("Pushed");
                    else
                        currLabel.setText("");
                else
                    currLabel.setText(Integer.toString(Utils.getAnalogValue(currComponent)));
            }
            //*

            // Delay to reduce CPU usage
            Thread.sleep(50);
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

    private static Component modifySavedComponents(int index, boolean remove) {
        Component component = components.get(index);
        if (remove) {
            savedComponents.remove(component);
            return null;
        } else {
            savedComponents.add(component.getIdentifier() + "`" + index + '`');
            return component;
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
                            aileronCpt = modifySavedComponents(index, false);
                        else
                            aileronCpt = modifySavedComponents(index, true);
                        break;
                    case "Aileron F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            aileronFo = modifySavedComponents(index, false);
                        else
                            aileronFo = modifySavedComponents(index, true);
                        break;
                    case "Elevator Capt":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            elevatorCpt = modifySavedComponents(index, false);
                        else
                            elevatorCpt = modifySavedComponents(index, true);
                        break;
                    case "Elevator F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            elevatorFo = modifySavedComponents(index, false);
                        else
                            elevatorFo = modifySavedComponents(index, true);
                        break;
                    case "Rudder Capt":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            rudderCpt = modifySavedComponents(index, false);
                        else
                            rudderCpt = modifySavedComponents(index, true);
                        break;
                    case "Rudder F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            rudderFo = modifySavedComponents(index, false);
                        else
                            rudderFo = modifySavedComponents(index, true);
                        break;
                    case "Tiller Capt":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            tillerCpt = modifySavedComponents(index, false);
                        else
                            tillerCpt = modifySavedComponents(index, true);
                        break;
                    case "Tiller F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            tillerFo = modifySavedComponents(index, false);
                        else
                            tillerFo = modifySavedComponents(index, true);
                        break;
                    case "Toe Brake Left Capt":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            toeBrakeLCpt = modifySavedComponents(index, false);
                        else
                            toeBrakeLCpt = modifySavedComponents(index, true);
                        break;
                    case "Toe Brake Right Capt":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            toeBrakeRCpt = modifySavedComponents(index, false);
                        else
                            toeBrakeRCpt = modifySavedComponents(index, true);
                        break;
                    case "Toe Brake Left F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            toeBrakeLFo = modifySavedComponents(index, false);
                        else
                            toeBrakeLFo = modifySavedComponents(index, true);
                        break;
                    case "Toe Brake Right F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            toeBrakeRFo = modifySavedComponents(index, false);
                        else
                            toeBrakeRFo = modifySavedComponents(index, true);
                        break;
                    case "Stab Trim UP Capt":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            stabTrimUpCpt = modifySavedComponents(index, false);
                        else
                            stabTrimUpCpt = modifySavedComponents(index, true);
                        break;
                    case "Stab Trim DN Capt":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            stabTrimDownCpt = modifySavedComponents(index, false);
                        else
                            stabTrimDownCpt = modifySavedComponents(index, true);
                        break;
                    case "Stab Trim UP F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            stabTrimUpFo = modifySavedComponents(index, false);
                        else
                            stabTrimUpFo = modifySavedComponents(index, true);
                        break;
                    case "Stab Trim DN F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            stabTrimDownFo = modifySavedComponents(index, false);
                        else
                            stabTrimDownFo = modifySavedComponents(index, true);
                        break;
                    case "AP Disc":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            apDisc = modifySavedComponents(index, false);
                        else
                            apDisc = modifySavedComponents(index, true);
                        break;
                    case "PTT Capt":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            lcpPttCpt = modifySavedComponents(index, false);
                        else
                            lcpPttCpt = modifySavedComponents(index, true);
                        break;
                    case "PTT F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            lcpPttFo = modifySavedComponents(index, false);
                        else
                            lcpPttFo = modifySavedComponents(index, true);
                        break;
                    case "TFR Capt":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            tfrCpt = modifySavedComponents(index, false);
                        else
                            tfrCpt = modifySavedComponents(index, true);
                        break;
                    case "WX Capt":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            wxCpt = modifySavedComponents(index, false);
                        else
                            wxCpt = modifySavedComponents(index, true);
                        break;
                    case "WX+T Capt":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            wxtCpt = modifySavedComponents(index, false);
                        else
                            wxtCpt = modifySavedComponents(index, true);
                        break;
                    case "MAP Capt":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            mapCpt = modifySavedComponents(index, false);
                        else
                            mapCpt = modifySavedComponents(index, true);
                        break;
                    case "GC Capt":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            gcCpt = modifySavedComponents(index, false);
                        else
                            gcCpt = modifySavedComponents(index, true);
                        break;
                    case "AUTO":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            auto = modifySavedComponents(index, false);
                        else
                            auto = modifySavedComponents(index, true);
                        break;
                    case "L/R":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            lr = modifySavedComponents(index, false);
                        else
                            lr = modifySavedComponents(index, true);
                        break;
                    case "TEST":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            test = modifySavedComponents(index, false);
                        else
                            test = modifySavedComponents(index, true);
                        break;
                    case "TFR F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            tfrFo = modifySavedComponents(index, false);
                        else
                            tfrFo = modifySavedComponents(index, true);
                        break;
                    case "WX F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            wxFo = modifySavedComponents(index, false);
                        else
                            wxFo = modifySavedComponents(index, true);
                        break;
                    case "WX+T F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            wxtFo = modifySavedComponents(index, false);
                        else
                            wxtFo = modifySavedComponents(index, true);
                        break;
                    case "MAP F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            mapFo = modifySavedComponents(index, false);
                        else
                            mapFo = modifySavedComponents(index, true);
                        break;
                    case "GC F/O":
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                            gcFo = modifySavedComponents(index, false);
                        else
                            gcFo = modifySavedComponents(index, true);
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
        JFrame frame = new JFrame("PSX SmartInterface v1.1: Pre-1.2 TEST 5");
        frame.setPreferredSize(new Dimension(800, 600));
        frame.setResizable(false);
        frame.getContentPane().add(scrollPane);
        // Pack to ensure preferred sizes are used
        frame.pack();
        // Exit program if the "X" is clicked
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                running = false;
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
