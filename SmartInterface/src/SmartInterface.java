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

    private static Client client;

    // Master controller list
    private static ArrayList<Controller> controllers;
    // Master component list
    private static ArrayList<Component> components = new ArrayList<>();
    // Master component list for components that are to be saved in saved_config.txt
    private static ArrayList<String> savedComponents = new ArrayList<>();
    // Master label list
    private static ArrayList<JLabel> valueLabels = new ArrayList<>();

    // Memory efficiency
    private static Component currComponent;
    private static JLabel currLabel;

    //* START Stored components specified by user
    // Flight controls (Qs120)
    private static Component aileronCpt, aileronFo;
    private static Component elevatorCpt, elevatorFo;
    private static Component rudderCpt, rudderFo;

    // Tillers (Qh426)
    private static Component tillerCpt, tillerFo;

    // Toe brakes (Qs357)
    private static Component toeBrakeLCpt, toeBrakeRCpt;
    private static Component toeBrakeLFo, toeBrakeRFo;

    // Misc buttons (Qh398, Qh399, Qh400, Qh82, Qh93)
    private static Component stabTrimUpCpt, stabTrimDownCpt;
    private static Component stabTrimUpFo, stabTrimDownFo;
    private static Component apDisc;
    private static Component lcpPttCpt, lcpPttFo;

    // Radar panel buttons (Qs104)
    private static Component tfrCpt;
    private static Component wxCpt, wxtCpt;
    private static Component mapCpt, gcCpt;
    private static Component auto, lr, test;
    private static Component tfrFo;
    private static Component wxFo, wxtFo;
    private static Component mapFo, gcFo;
    //* END Stored components specified by user

    // Values used to detect if PSX variables have been changed and need to be updated
    private static Value fltControlsVal = new Value();
    private static Value tillersVal = new Value();
    private static Value toeBrakesVal = new Value();
    private static Value stabTrimCptVal = new Value();
    private static Value stabTrimFoVal = new Value();
    private static Value apDiscVal = new Value();
    private static Value lcpPttCptVal = new Value();
    private static Value lcpPttFoVal = new Value();
    private static Value rdrPanelVal = new Value();

    // Buffers to be combined as String sent for radar panel button values (Qs104)
    private static char[] rdrStrCpt, rdrStrFo;
    private static char[] rdrStrMisc = new char[3];

    public static void main(String[] args) {
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

    /**
     * Polls controllers, updates labels, and sends data to update the PSX server.
     */
    private static void update() {
        try {
            for (Controller controller : controllers)
                controller.poll();

            // Update labels on UI
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

            //* START Update analog values
            int aileron = Utils.combineAnalog(SmartInterface.aileronCpt,
                    SmartInterface.aileronFo);
            int elevator = Utils.combineAnalog(SmartInterface.elevatorCpt,
                    SmartInterface.elevatorFo);
            int rudder = Utils.combineAnalog(SmartInterface.rudderCpt,
                    SmartInterface.rudderFo);
            fltControlsVal.setStr("Qs120=" + Integer.toString(elevator) + ";" +
                    Integer.toString(aileron) + ";" + Integer.toString(rudder));
            if (fltControlsVal.hasChanged())
                client.send(fltControlsVal.getStr());

            int tiller = Utils.combineAnalog(SmartInterface.tillerCpt,
                    SmartInterface.tillerFo);
            tillersVal.setStr("Qh426=" + Integer.toString(tiller));
            if (tillersVal.hasChanged())
                client.send(tillersVal.getStr());

            int toeBrakeL = Utils.combineAnalog(SmartInterface.toeBrakeLCpt,
                    SmartInterface.toeBrakeLFo);
            int toeBrakeR = Utils.combineAnalog(SmartInterface.toeBrakeRCpt,
                    SmartInterface.toeBrakeRFo);
            toeBrakesVal.setStr("Qs357=" + Integer.toString(toeBrakeL) + ";" + Integer.toString(toeBrakeR));
            if (toeBrakesVal.hasChanged())
                client.send(toeBrakesVal.getStr());
            //* END Update analog values

            //* START Update misc buttons
            if (Utils.isPushed(SmartInterface.stabTrimUpCpt))
                stabTrimCptVal.setStr("Qh398=1");
            else if (Utils.isPushed(SmartInterface.stabTrimDownCpt))
                stabTrimCptVal.setStr("Qh398=-1");
            else
                stabTrimCptVal.setStr("Qh398=0");
            if (stabTrimCptVal.hasChanged())
                client.send(stabTrimCptVal.getStr());

            if (Utils.isPushed(SmartInterface.stabTrimUpFo))
                stabTrimFoVal.setStr("Qh399=1");
            else if (Utils.isPushed(SmartInterface.stabTrimDownFo))
                stabTrimFoVal.setStr("Qh399=-1");
            else
                stabTrimFoVal.setStr("Qh399=0");
            if (stabTrimFoVal.hasChanged())
                client.send(stabTrimFoVal.getStr());

            if (Utils.isPushed(SmartInterface.apDisc))
                apDiscVal.setStr("Qh400=1");
            else
                apDiscVal.setStr("Qh400=0");
            if (apDiscVal.hasChanged())
                client.send(apDiscVal.getStr());

            if (Utils.isPushed(lcpPttCpt))
                lcpPttCptVal.setStr("Qh82=1");
            else
                lcpPttCptVal.setStr("Qh82=0");
            if (lcpPttCptVal.hasChanged())
                client.send(lcpPttCptVal.getStr());

            if (Utils.isPushed(SmartInterface.lcpPttFo))
                lcpPttFoVal.setStr("Qh93=1");
            else
                lcpPttFoVal.setStr("Qh93=0");
            if (lcpPttFoVal.hasChanged())
                client.send(lcpPttFoVal.getStr());
            //* END Update misc buttons

            //* START Update radar panel buttons
            // Captain (left) row
            if (Utils.isPushed(SmartInterface.tfrCpt))
                rdrStrCpt = new char[]{'f', 'W', 'T', 'M', 'G'};
            else if (Utils.isPushed(SmartInterface.wxCpt))
                rdrStrCpt = new char[]{'F', 'w', 'T', 'M', 'G'};
            else if (Utils.isPushed(SmartInterface.wxtCpt))
                rdrStrCpt = new char[]{'F', 'W', 't', 'M', 'G'};
            else if (Utils.isPushed(SmartInterface.mapCpt))
                rdrStrCpt = new char[]{'F', 'W', 'T', 'm', 'G'};
            else if (rdrStrCpt == null)
                rdrStrCpt = new char[]{'F', 'W', 'T', 'M', 'G'};
            if (Utils.isPushed(SmartInterface.gcCpt))
                rdrStrCpt[4] = 'g';
            else
                rdrStrCpt[4] = 'G';

            // Middle (misc) row
            // TODO Make these toggle
            if (Utils.isPushed(SmartInterface.auto))
                rdrStrMisc[0] = 'a';
            else
                rdrStrMisc[0] = 'A';
            if (Utils.isPushed(SmartInterface.lr))
                rdrStrMisc[1] = 'r';
            else
                rdrStrMisc[1] = 'R';
            if (Utils.isPushed(SmartInterface.test))
                rdrStrMisc[2] = 'e';
            else
                rdrStrMisc[2] = 'E';

            // First officer (right) row
            if (Utils.isPushed(SmartInterface.tfrFo))
                rdrStrFo = new char[]{'f', 'W', 'T', 'M', 'G'};
            else if (Utils.isPushed(SmartInterface.wxFo))
                rdrStrFo = new char[]{'F', 'w', 'T', 'M', 'G'};
            else if (Utils.isPushed(SmartInterface.wxtFo))
                rdrStrFo = new char[]{'F', 'W', 't', 'M', 'G'};
            else if (Utils.isPushed(SmartInterface.mapFo))
                rdrStrFo = new char[]{'F', 'W', 'T', 'm', 'G'};
            else if (rdrStrFo == null)
                rdrStrFo = new char[]{'F', 'W', 'T', 'M', 'G'};
            if (Utils.isPushed(SmartInterface.gcFo))
                rdrStrFo[4] = 'g';
            else
                rdrStrFo[4] = 'G';

            // Concat char[] and send
            String rdrPanelString = new String(rdrStrCpt) + new String(rdrStrMisc) + new String(rdrStrFo);
            rdrPanelVal.setStr(rdrPanelString);
            if (rdrPanelVal.hasChanged())
                client.send("Qs104=" + rdrPanelString);
            //* END Update radar panel buttons

            Thread.sleep(50); // 20 Hz
        } catch(Exception e) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                    e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Gets preferred controllers, excluding mice and keyboards.
     */
    private static void getControllers() {
        // Copy controllers into ArrayList so they can be removed easily
        controllers = new ArrayList<>(Arrays.asList(ControllerEnvironment
                .getDefaultEnvironment().getControllers()));
        for (int i = 0; i < controllers.size(); i++)
            if (shouldIgnore(controllers.get(i))) {
                controllers.remove(i);
                i--;
            }
    }

    // TODO Implement completely
    private static Component modifySavedComponents(int index, boolean remove) {
        Component component = components.get(index);
        if (remove) {
            //savedComponents.remove(component);
            return null;
        } else {
            savedComponents.add(component.getIdentifier() + "`" + index + '`');
            return component;
        }
    }

    /**
     * Initializes the user interface.
     */
    private static void initUI() {

        // Detect and react to UI (JComboBox) changes
        ItemListener itemListener = new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent) {
                ComboBox combo = (ComboBox) itemEvent.getSource();
                int index = combo.getIndex();
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

        JPanel panel = new JPanel();
        ComboBox comboBox;
        JLabel label;
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

        // Go through each controller and add its components to the UI
        for (Controller controller : controllers) {
            for (Component component : controller.getComponents()) {
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

        // TODO Add another column for "Neutral" (dead zone)
        GridLayout grid = new GridLayout(components.size(), 3, 10, 0);
        panel.setLayout(grid);
        panel.setPreferredSize(new Dimension(780, components.size() * 30));

        JScrollPane scrollPane = new JScrollPane(panel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // 569 used to offset width of bar itself
        scrollPane.setPreferredSize(new Dimension(800, 569));

        JFrame frame = new JFrame("PSX SmartInterface v1.1: Pre-1.2 TEST 7");
        frame.setPreferredSize(new Dimension(800, 600));
        frame.setResizable(false);
        frame.getContentPane().add(scrollPane);
        frame.pack();
        // Exit program if the "X" is clicked
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                running = false;
            }
        });
        frame.setVisible(true);
    }

    /**
     * Determines if a controller should be ignored.
     * @param controller The controller to be observed.
     * @return A boolean that is true if the controller should be ignored and false if the controller should not be ignored.
     */
    private static boolean shouldIgnore(Controller controller) {
        return controller.getName().toUpperCase().contains("KEYBOARD") ||
                controller.getName().toUpperCase().contains("MOUSE");
    }

}
