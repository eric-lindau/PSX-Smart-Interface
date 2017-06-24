import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Controller;
import net.java.games.input.Component;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Primary class that handles client creation, UI, calculations, and
 * server interaction.
 *
 * @author Eric Lindau
 * @version 1.0
 */
public class SmartInterface {

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
        Client client = new Client("localhost", 10747);

        // Get a list of all recognized system controllers
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
        for(int i=0; i<controllers.length; i++)
            // Print each controller's components
            printComponents(controllers[i]);

        // UI INITIALIZATION START
        JPanel panel = new JPanel();

        JFrame frame = new JFrame("PSX Smart SmartInterface");
        frame.setPreferredSize(new Dimension(800, 600));
        frame.setLocation(600, 100);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
        // UI INITIALIZATION END

        // TO BE REMOVED LATER
        System.exit(0);
    }

}
