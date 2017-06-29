import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Controller;
import net.java.games.input.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Primary class that handles client creation, UI, calculations, and
 * server interaction.
 *
 * @author Eric Lindau
 * @version 0.0
 */
public class SmartInterface {

    private static ArrayList<Controller> controllers;
    private static ArrayList<JLabel> valueLabels;

    // TODO Use these variables instead of too many local ones
    private static Component[] currentComponents;
    private static Component currentComponent;

    static Component aileronCpt, aileronFo;
    static Component elevatorCpt, elevatorFo;
    static Component rudderCpt, rudderFo;
    static Component tillerCpt, tillerFo;

    /**
     * Standard main method.
     */
    public static void main(String[] args) throws IOException {
        Client client = new Client("localhost", 10747);

        getControllers();

        initUI();

        try {
            while (true)
                update();
        } catch(Exception e) {System.exit(0);}
    }

    /**
     * Updates all controller values and UI labels
     */
    private static void update() {
        // TODO Differentiate analog and non-analog inputs (pushed vs value)
        for(Controller controller : controllers) {
            controller.poll();
            currentComponents = controller.getComponents();
            for(int i = 0; i < currentComponents.length; i++) {
                currentComponent = currentComponents[i];
                if(!currentComponent.isAnalog())
                    if(currentComponent.getPollData() > 0)
                        valueLabels.get(i).setText("Pushed");
                    else
                        valueLabels.get(i).setText("");
                else
                    valueLabels.get(i).setText(Integer.toString(Math.round(currentComponent.getPollData() * 999) % 1000));
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
        valueLabels = new ArrayList<>();
        int components = 0;
        for(Controller controller : controllers)
            for(int i = 0; i < controller.getComponents().length; i++)
                components++;
        GridLayout grid = new GridLayout(components, 4, 10, 0);
        JPanel panel = new JPanel();
        panel.setLayout(grid);
        panel.setPreferredSize(new Dimension(780, components*30));
        JLabel label;
        String[] dropBoxStrings = {"Aileron (1)", "Aileron (2)",
                "Elevator (1)", "Elevator (2)", "Rudder (1)", "Rudder(2)",
                "Tiller (1)", "Tiller (2)"};
        JComboBox comboBox;
        int counter = 0;
        for(Controller controller : controllers) {
            for(Component component : controller.getComponents()) {
                label = new JLabel("  " + Integer.toString(counter) + ". " +
                        controller.getName() + " - " +  component.getName());
                panel.add(label);
                label = new JLabel("", SwingConstants.CENTER);
                panel.add(label);
                valueLabels.add(label);
                comboBox = new JComboBox(dropBoxStrings);
                panel.add(comboBox);
                counter++;
            }
        }

        JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(800, 569));

        JFrame frame = new JFrame("PSX Smart SmartInterface");
        frame.setPreferredSize(new Dimension(800, 600));
        frame.setLayout(new FlowLayout());
        frame.setResizable(false);
        frame.setLocation(600, 100);
        frame.getContentPane().add(scrollPane);
        frame.pack();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.setVisible(true);
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
