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

    static int aileronCpt, aileronFo;
    static int elevatorCpt, elevatorFo;
    static int rudderCpt, rudderFo;
    static int tillerCpt, tillerFo;

    public static void main(String[] args) throws IOException {
        Client client = new Client("localhost", 10747);

        getControllers();

        initUI();

        try {
            while (true)
                pollControllers();
        } catch(Exception e) {System.exit(0);}
    }

    private static void getControllers() {
        controllers = new ArrayList<>(Arrays.asList(ControllerEnvironment
                .getDefaultEnvironment().getControllers()));
        for(int i = 0; i < controllers.size(); i++)
            if(shouldIgnore(controllers.get(i))) {
                controllers.remove(i);
                i--;
            }
    }

    private static void pollControllers() {
        for(Controller controller : controllers) {
            controller.poll();
            for(Component component : controller.getComponents())
                System.out.println(component.getName() + ": " + component.getPollData());
        }
    }

    private static void initUI() {
        int components = 0;
        for(Controller controller : controllers)
            for(Component component : controller.getComponents())
                components++;
        GridLayout grid = new GridLayout(components, 4, 10, 0);
        JPanel panel = new JPanel();
        panel.setLayout(grid);
        panel.setPreferredSize(new Dimension(800, components*30));
        JLabel label;
        int counter = 0;
        for(Controller controller : controllers) {
            for(Component component : controller.getComponents()) {
                label = new JLabel("  " + Integer.toString(counter) + ". " + controller.getName() + " - " +  component.getName());
                panel.add(label);
                counter++;
            }
        }

        JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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

    private static boolean shouldIgnore(Controller controller) {
        return controller.getName().toUpperCase().contains("KEYBOARD") ||
                controller.getName().toUpperCase().contains("MOUSE") ||
                controller.getName().toUpperCase().contains("RAZER");
    }

}
