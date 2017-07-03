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
import java.lang.reflect.Array;
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
 * @version 0.0
 */
public class SmartInterface {

    // TODO Cleanup + efficiency
    // TODO Minimize repeated use of get() calls (efficiency)

    private static Client client;

    /**
     * List containing all the necessary hardware controllers
     */
    private static ArrayList<Controller> controllers;
    // TODO Implement components to contain components in the order that they are added to UI
    private static ArrayList<Component> components;
    private static ArrayList<JLabel> valueLabels;

    // TODO Use these variables instead of too many local ones
    // TODO See if variables can be local
    private static Component[] currComponents;
    private static Component currComponent;
    private static JLabel currLabel;

    static Component aileronCpt, aileronFo;
    static Component elevatorCpt, elevatorFo;
    static Component rudderCpt, rudderFo;
    static Component tillerCpt, tillerFo;

    /**
     * Standard main method.
     */
    public static void main(String[] args) throws IOException {
        client = new Client("localhost", 10747);

        getControllers();

        initUI();

        try {
            while (true)
                update();
        } catch(Exception e) {System.exit(0);}
    }

    private static int getAnalogValue(Component component) {
        return Math.round(component.getPollData() * 999) % 1000;
    }

    /**
     * Updates all controller values and UI labels
     */
    private static void update() {
        for(Controller controller : controllers) {
            controller.poll();
            currComponents = controller.getComponents();
            for(int i = 0; i < currComponents.length; i++) {
                currComponent = currComponents[i];
                currLabel = valueLabels.get(i);
                if(!currComponent.isAnalog())
                    if(currComponent.getPollData() > 0)
                        currLabel.setText("Pushed");
                    else
                        currLabel.setText("");
                else
                    currLabel.setText(Integer.toString(getAnalogValue(
                            currComponent)));
            }
        }
    }

    /**
     * Retrieves all necessary active controllers connected to the system.
     * Keyboards and mice are ignored because they are useless to PSX.
     */
    private static void getControllers() {
        controllers = new ArrayList<>(Arrays.asList(ControllerEnvironment
                .getDefaultEnvironment().getControllers()));
        for(int i = 0; i < controllers.size(); i++)
            if(shouldIgnore(controllers.get(i))) {
                controllers.remove(i);
                i--;
            }
    }

    /**
     * Initializes the UI
     */
    private static void initUI() {
        components = new ArrayList<>();

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

        // TODO: Fix setting back to "None"
        ItemListener itemListener = new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent) {
                ComboBox combo = (ComboBox)itemEvent.getSource();
                System.out.println(combo.getIndex());
//                switch((String)itemEvent.getItem()) {
//                    case "Aileron (1)":
//                        aileronCpt = components.get(index);
//                        break;
//                    case "Aileron (2)":
//                        aileronFo = components.get(index);
//                        break;
//                }
            }
        };

        // Go through each controller and add its components to the UI
        for(Controller controller : controllers) {
            for(Component component : controller.getComponents()) {
                // Add component to master list of components
                components.add(component);
                // Label #1: Current number and name of component
                label = new JLabel("  " + Integer.toString(counter) + ". " +
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

        GridLayout grid = new GridLayout(components.size(), 3, 10, 0);
        panel.setLayout(grid);
        panel.setPreferredSize(new Dimension(780, components.size() * 30));

        //* JScrollPane init and config
        JScrollPane scrollPane = new JScrollPane(panel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // 569 used to offset width of bar itself
        scrollPane.setPreferredSize(new Dimension(800, 569));
        //*

        //* JFrame init and config
        JFrame frame = new JFrame("PSX Smart SmartInterface");
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

    /**
     * Determines whether or not a controller should be ignored - specifically,
     * if it is a keyboard or a mouse, thus being useless as a controller.
     *
     * @param controller the controller that should potentially be ignored.
     * @return Returns a boolean dictating whether or not the controller should be ignored.
     */
    private static boolean shouldIgnore(Controller controller) {
        return controller.getName().toUpperCase().contains("KEYBOARD") ||
                controller.getName().toUpperCase().contains("MOUSE") ||
                controller.getName().toUpperCase().contains("RAZER");
    }

}
