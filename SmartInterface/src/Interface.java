import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Controller;
import net.java.games.input.Component;

import javax.swing.*;
import java.io.IOException;

/**
 *
 */
public class Interface {

    static int aileronCpt, aileronFo;
    static int elevatorCpt, elevatorFo;
    static int rudderCpt, rudderFo;
    static int tillerCpt, tillerFo;

    private static void printComponents(Controller controller) {
        System.out.println(controller.getName().toUpperCase());
        Component[] components = controller.getComponents();
        for(int j=0; j < components.length; j++)
            System.out.println("Component " + j + ": " + components[j].getName());
    }

    public static void main(String[] args) throws IOException {
        // Get a list of all recognized system controllers
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
        for(int i=0; i<controllers.length; i++)
            // Print each controller's components
            printComponents(controllers[i]);

        // UI INITIALIZATION START
        JPanel panel = new JPanel();

        JFrame frame = new JFrame("PSX Smart Interface");
        frame.setSize(800, 600);
        frame.setLocation(600, 100);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
        // UI INITIALIZATION END

        System.exit(0);
    }

}
