import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Controller;
import net.java.games.input.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * An add-on for Aerowinx PSX that provides easy, smart configuration of hardware
 * components as they relate to PSX.
 *
 * https://github.com/eric-lindau/PSX-Smart-Interface
 *
 * @author Eric Lindau
 * @version 1.2.5
 */
class SmartInterface {

    private static boolean running = true;
    private static boolean minimizeOnStart;
    private static Client client;

    private static ArrayList<String> ignoredControllers = new ArrayList();

    // Master controller list
    private static ArrayList<Controller> controllers;
    // Master component list
    private static ArrayList<Component> components = new ArrayList();
    // Master component list for components that are to be saved in saved.cfg
    private static ArrayList<String> savedComponents = new ArrayList();
    // Master controller name label list
    private static ArrayList<JLabel> labels = new ArrayList();
    // Master value label list
    private static ArrayList<JLabel> valueLabels = new ArrayList();
    // Master list to tell if each component is inverted for saving purposes
    private static ArrayList<Boolean> inverted = new ArrayList();

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
    private static Component stabTrimUpCpt, stabTrimDnCpt;
    private static Component stabTrimUpFo, stabTrimDnFo;
    private static Component apDiscCapt, apDiscFo;
    private static Component lcpPttCpt, lcpPttFo;

    // Radar panel buttons/rotaries (Qs104, Qs105)
    private static Component tfrCpt;
    private static Component wxCpt, wxtCpt;
    private static Component mapCpt, gcCpt;
    private static Component auto, lr, test;
    private static Component tfrFo;
    private static Component wxFo, wxtFo;
    private static Component mapFo, gcFo;
    private static Component tiltCpt, gainCpt;
    private static Component tiltFo, gainFo;

    // Other rotaries (Qh273, Qh297, Qh87, Qh89, Qh98, Qh100, Qh101, Qh139, Qh140, Qh141)
    private static Component jettRemain;
    private static Component ldgAltTurn;
    private static Component lcpOutbdCpt;
    private static Component lcpNdCpt;
    private static Component lcpOutbdFo;
    private static Component lcpNdFo;
    private static Component lcpWxrCpt;
    private static Component lcpWxrFo;
    private static Component eicasBrtUpr;
    private static Component eicasBrtLwrInner;
    private static Component eicasBrtLwrOuter;
    //* END Stored components specified by user

    // Values used to detect if PSX variables have been changed and need to be updated
    private static Value fltControlsVal = new Value();
    private static Value tillersVal = new Value();
    private static Value toeBrakesVal = new Value();
    private static Value stabTrimCptVal = new Value();
    private static Value stabTrimFoVal = new Value();
    private static Value apDiscCaptVal = new Value();
    private static Value apDiscFoVal = new Value();
    private static Value lcpPttCptVal = new Value();
    private static Value lcpPttFoVal = new Value();
    private static Value rdrPanelVal = new Value();
    private static Value rdrPanelRotVal = new Value();
    private static Value jettRemainVal = new Value();
    private static Value ldgAltTurnVal = new Value();
    private static Value lcpOutbdCptVal = new Value();
    private static Value lcpNdCptVal = new Value();
    private static Value lcpOutbdFoVal = new Value();
    private static Value lcpNdFoVal = new Value();
    private static Value lcpWxrCptVal = new Value();
    private static Value lcpWxrFoVal = new Value();
    private static Value eicasBrtUprVal = new Value();
    private static Value eicasBrtLwrInnerVal = new Value();
    private static Value eicasBrtLwrOuterVal = new Value();

    // Buffers to be combined as String sent for radar panel button values (Qs104)
    private static char[] rdrStrCpt, rdrStrFo;
    private static char[] rdrStrMisc = {'A', 'R', 'E'};
    // Keeps track of radar panel unpressing for toggling functionality
    private static boolean[] unPressed = {false, false, false};

    public static void main(String[] args) {
        initConfig();
        initControllers();
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

    /**
     * Polls controllers, updates labels, and sends data to update the PSX server.
     */
    private static void update() {
        try {
            for (Controller controller : controllers)
                controller.poll();

            // Update labels on UI
            for (int i = 0; i < components.size(); i++) {
                JLabel currLabel = valueLabels.get(i);
                Component currComponent = components.get(i);
                if (!currComponent.isAnalog())
                    if (Utils.isPushed(currComponent))
                        currLabel.setText("Pushed");
                    else
                        currLabel.setText("");
                else
                    currLabel.setText(Integer.toString(Utils.getAnalogValue(currComponent)));
            }

            //* START Update analog values
            if (aileronCpt != null || aileronFo != null ||
                    elevatorCpt != null || elevatorFo != null ||
                    rudderCpt != null || rudderFo != null) {
                int aileron = Utils.combineAnalog(aileronCpt, aileronFo);
                int elevator = Utils.combineAnalog(elevatorCpt, elevatorFo);
                int rudder = Utils.combineAnalog(rudderCpt, rudderFo);
                aileron = Utils.deadzone(aileron, 50);
                elevator = Utils.deadzone(elevator, 50);
                rudder = Utils.deadzone(rudder, 50);
                fltControlsVal.setStr("Qs120=" + Integer.toString(elevator) + ";" +
                        Integer.toString(aileron) + ";" + Integer.toString(rudder));
                if (fltControlsVal.hasChanged())
                    client.send(fltControlsVal.getStr());
            }

            if (tillerCpt != null || tillerFo != null) {
                int tiller = Utils.combineAnalog(tillerCpt, tillerFo);
                tiller = Utils.deadzone(tiller, 50);
                tillersVal.setStr("Qh426=" + Integer.toString(tiller));
                if (tillersVal.hasChanged())
                    client.send(tillersVal.getStr());
            }

            if (toeBrakeLCpt != null || toeBrakeRCpt != null ||
                    toeBrakeLFo != null || toeBrakeRFo != null) {
                int toeBrakeL = Utils.combineAnalog(toeBrakeLCpt, toeBrakeLFo) + 1;
                int toeBrakeR = Utils.combineAnalog(toeBrakeRCpt, toeBrakeRFo) + 1;
                toeBrakeL = Utils.deadzone(toeBrakeL, 100);
                toeBrakeR = Utils.deadzone(toeBrakeR, 100);
                toeBrakesVal.setStr("Qs357=" + Integer.toString(toeBrakeL) + ";" + Integer.toString(toeBrakeR));
                if (toeBrakesVal.hasChanged())
                    client.send(toeBrakesVal.getStr());
            }
            //* END Update analog values

            //* START Update misc buttons
            if (stabTrimUpCpt != null || stabTrimDnCpt != null) {
                if (Utils.isPushed(stabTrimUpCpt))
                    stabTrimCptVal.setStr("Qh398=1");
                else if (Utils.isPushed(stabTrimDnCpt))
                    stabTrimCptVal.setStr("Qh398=-1");
                else
                    stabTrimCptVal.setStr("Qh398=0");
                if (stabTrimCptVal.hasChanged())
                    client.send(stabTrimCptVal.getStr());
            }

            if (stabTrimUpFo != null || stabTrimDnFo != null) {
                if (Utils.isPushed(stabTrimUpFo))
                    stabTrimFoVal.setStr("Qh399=1");
                else if (Utils.isPushed(stabTrimDnFo))
                    stabTrimFoVal.setStr("Qh399=-1");
                else
                    stabTrimFoVal.setStr("Qh399=0");
                if (stabTrimFoVal.hasChanged())
                    client.send(stabTrimFoVal.getStr());
            }

            if (apDiscCapt != null) {
                if (Utils.isPushed(apDiscCapt))
                    apDiscCaptVal.setStr("Qh400=1");
                else
                    apDiscCaptVal.setStr("Qh400=0");
                if (apDiscCaptVal.hasChanged())
                    client.send(apDiscCaptVal.getStr());
            }

            if (apDiscFo != null) {
                if (Utils.isPushed(apDiscFo))
                    apDiscFoVal.setStr("Qh400=1");
                else
                    apDiscFoVal.setStr("Qh400=0");
                if (apDiscFoVal.hasChanged())
                    client.send(apDiscFoVal.getStr());
            }

            if (lcpPttCpt != null) {
                if (Utils.isPushed(lcpPttCpt))
                    lcpPttCptVal.setStr("Qh82=1");
                else
                    lcpPttCptVal.setStr("Qh82=0");
                if (lcpPttCptVal.hasChanged())
                    client.send(lcpPttCptVal.getStr());
            }

            if (lcpPttFo != null) {
                if (Utils.isPushed(lcpPttFo))
                    lcpPttFoVal.setStr("Qh93=1");
                else
                    lcpPttFoVal.setStr("Qh93=0");
                if (lcpPttFoVal.hasChanged())
                    client.send(lcpPttFoVal.getStr());
            }
            //* END Update misc buttons

            //* START Update radar panel buttons/rotaries
            // Captain (left) row
            if (tfrCpt != null || wxCpt != null || wxtCpt != null || mapCpt != null || gcCpt != null ||
                    auto != null || lr != null || test != null ||
                    tfrFo != null || wxFo != null || wxtFo != null || mapFo != null || gcFo != null) {
                if (Utils.isPushed(tfrCpt))
                    rdrStrCpt = new char[]{'f', 'W', 'T', 'M', 'G'};
                else if (Utils.isPushed(wxCpt))
                    rdrStrCpt = new char[]{'F', 'w', 'T', 'M', 'G'};
                else if (Utils.isPushed(wxtCpt))
                    rdrStrCpt = new char[]{'F', 'W', 't', 'M', 'G'};
                else if (Utils.isPushed(mapCpt))
                    rdrStrCpt = new char[]{'F', 'W', 'T', 'm', 'G'};
                else if (rdrStrCpt == null)
                    rdrStrCpt = new char[]{'F', 'W', 'T', 'M', 'G'};
                if (Utils.isPushed(gcCpt))
                    rdrStrCpt[4] = 'g';
                else
                    rdrStrCpt[4] = 'G';

                // Middle (misc) row
                // Toggled radar panel buttons
                if (!unPressed[0] && !Utils.isPushed(auto))
                    unPressed[0] = true;
                if (!unPressed[1] && !Utils.isPushed(lr))
                    unPressed[1] = true;
                if (!unPressed[2] && !Utils.isPushed(test))
                    unPressed[2] = true;

                if (unPressed[0] && Utils.isPushed(auto)) {
                    if (rdrStrMisc[0] == 'a')
                        rdrStrMisc[0] = 'A';
                    else
                        rdrStrMisc[0] = 'a';
                    unPressed[0] = false;
                }

                if (unPressed[1] && Utils.isPushed(lr)) {
                    if (rdrStrMisc[1] == 'r')
                        rdrStrMisc[1] = 'R';
                    else
                        rdrStrMisc[1] = 'r';
                    unPressed[1] = false;
                }

                if (unPressed[2] && Utils.isPushed(test)) {
                    if (rdrStrMisc[2] == 'e')
                        rdrStrMisc[2] = 'E';
                    else
                        rdrStrMisc[2] = 'e';
                    unPressed[2] = false;
                }

                // First officer (right) row
                if (Utils.isPushed(tfrFo))
                    rdrStrFo = new char[]{'f', 'W', 'T', 'M', 'G'};
                else if (Utils.isPushed(wxFo))
                    rdrStrFo = new char[]{'F', 'w', 'T', 'M', 'G'};
                else if (Utils.isPushed(wxtFo))
                    rdrStrFo = new char[]{'F', 'W', 't', 'M', 'G'};
                else if (Utils.isPushed(mapFo))
                    rdrStrFo = new char[]{'F', 'W', 'T', 'm', 'G'};
                else if (rdrStrFo == null)
                    rdrStrFo = new char[]{'F', 'W', 'T', 'M', 'G'};
                if (Utils.isPushed(gcFo))
                    rdrStrFo[4] = 'g';
                else
                    rdrStrFo[4] = 'G';

                // Concat char[] and send Qs104
                String rdrPanelString = new String(rdrStrCpt) + new String(rdrStrMisc) + new String(rdrStrFo);
                rdrPanelVal.setStr("Qs104=" + rdrPanelString);
                if (rdrPanelVal.hasChanged())
                    client.send(rdrPanelVal.getStr());
            }

            // Rotaries
            if (tiltCpt != null || gainCpt != null || tiltFo != null || gainFo != null) {
                int tiltCptInt = Utils.getAnalogValue(tiltCpt, 2356, true) + 2356;
                int gainCptInt = Utils.getGainValue(gainCpt);
                int tiltFoInt = Utils.getAnalogValue(tiltFo, 2356, true) + 2356;
                int gainFoInt = Utils.getGainValue(gainFo);
                rdrPanelRotVal.setStr("Qs105=" + Integer.toString(tiltCptInt) + ";" + Integer.toString(gainCptInt) +
                        ";" + Integer.toString(tiltFoInt) + ";" + Integer.toString(gainFoInt));
                if (rdrPanelRotVal.hasChanged())
                    client.send(rdrPanelRotVal.getStr());
            }
            //* END Update radar panel buttons/rotaries

            //* START Misc rotaries
            if (jettRemain != null) {
                int jettRemainInt = Utils.getAnalogValue(jettRemain, 62830, false);
                jettRemainVal.setStr("Qh273=" + Integer.toString(jettRemainInt));
                if (jettRemainVal.hasChanged())
                    client.send(jettRemainVal.getStr());
            }

            if (ldgAltTurn != null) {
                int ldgAltTurnInt = Utils.getAnalogValue(ldgAltTurn, 62830, false);
                ldgAltTurnVal.setStr("Qh297=" + Integer.toString(ldgAltTurnInt));
                if (ldgAltTurnVal.hasChanged())
                    client.send(ldgAltTurnVal.getStr());
            }

            if (lcpOutbdCpt != null) {
                int lcpOutbdCptInt = Utils.getAnalogValue(lcpOutbdCpt, 4713, false);
                lcpOutbdCptVal.setStr("Qh87=" + Integer.toString(lcpOutbdCptInt));
                if (lcpOutbdCptVal.hasChanged())
                    client.send(lcpOutbdCptVal.getStr());
            }

            if (lcpNdCpt != null) {
                int lcpNdCptInt = Utils.getAnalogValue(lcpNdCpt, 4713, false);
                lcpNdCptVal.setStr("Qh89=" + Integer.toString(lcpNdCptInt));
                if (lcpNdCptVal.hasChanged())
                    client.send(lcpNdCptVal.getStr());
            }

            if (lcpOutbdFo != null) {
                int lcpOutbdFoInt = Utils.getAnalogValue(lcpOutbdFo, 4713, false);
                lcpOutbdFoVal.setStr("Qh98=" + Integer.toString(lcpOutbdFoInt));
                if (lcpOutbdFoVal.hasChanged())
                    client.send(lcpOutbdFoVal.getStr());
            }

            if (lcpNdFo != null) {
                int lcpNdFoInt = Utils.getAnalogValue(lcpNdFo, 4713, false);
                lcpNdFoVal.setStr("Qh100=" + Integer.toString(lcpNdFoInt));
                if (lcpNdFoVal.hasChanged())
                    client.send(lcpNdFoVal.getStr());
            }

            if (lcpWxrCpt != null) {
                int lcpWxrCptInt = Utils.getAnalogValue(lcpWxrCpt, 4713, false);
                lcpWxrCptVal.setStr("Qh88=" + Integer.toString(lcpWxrCptInt));
                if (lcpWxrCptVal.hasChanged())
                    client.send(lcpWxrCptVal.getStr());
            }

            if (lcpWxrFo != null) {
                int lcpWxrFoInt = Utils.getAnalogValue(lcpWxrFo, 4713, false);
                lcpWxrFoVal.setStr("Qh99=" + Integer.toString(lcpWxrFoInt));
                if (lcpWxrFoVal.hasChanged())
                    client.send(lcpWxrFoVal.getStr());
            }

            if (eicasBrtUpr != null) {
                int eicasBrtUprInt = Utils.getAnalogValue(eicasBrtUpr, 4713, false);
                eicasBrtUprVal.setStr("Qh139=" + Integer.toString(eicasBrtUprInt));
                if (eicasBrtUprVal.hasChanged())
                    client.send(eicasBrtUprVal.getStr());
            }

            if (eicasBrtLwrInner != null) {
                int eicasBrtLwrInnerInt = Utils.getAnalogValue(eicasBrtLwrInner, 4713, false);
                eicasBrtLwrInnerVal.setStr("Qh140=" + Integer.toString(eicasBrtLwrInnerInt));
                if (eicasBrtLwrInnerVal.hasChanged())
                    client.send(eicasBrtLwrInnerVal.getStr());
            }

            if (eicasBrtLwrOuter != null) {
                int eicasBrtLwrOuterInt = Utils.getAnalogValue(eicasBrtLwrOuter, 4713, false);
                eicasBrtLwrOuterVal.setStr("Qh141=" + Integer.toString(eicasBrtLwrOuterInt));
                if (eicasBrtLwrOuterVal.hasChanged())
                    client.send(eicasBrtLwrOuterVal.getStr());
            }
            //* END Misc rotaries

            Thread.sleep(50); // 20 Hz
        } catch(Exception e) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Initializes the user interface.
     */
    private static void initUI() {

        // Detect and react to UI changes
        ItemListener checkListener = new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent) {
                CheckBox box = (CheckBox) itemEvent.getSource();
                int index = box.getIndex();
                if (box.isSelected()) {
                    inverted.set(index, true);
                    Utils.inverted.add(components.get(index));
                } else {
                    inverted.set(index, false);
                    Utils.inverted.remove(components.get(index));
                }
            }
        };
        ItemListener comboListener = new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent) {
                String item = (String) itemEvent.getItem();
                ComboBox combo = (ComboBox) itemEvent.getSource();
                int index = combo.getIndex();
                if (item.equals("Aileron - Capt"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        aileronCpt = modifySavedComponents(index, false, item);
                    else
                        aileronCpt = modifySavedComponents(index, true, item);
                else if (item.equals("Aileron - F/O"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        aileronFo = modifySavedComponents(index, false, item);
                    else
                        aileronFo = modifySavedComponents(index, true, item);
                else if (item.equals("Elevator - Capt"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        elevatorCpt = modifySavedComponents(index, false, item);
                    else
                        elevatorCpt = modifySavedComponents(index, true, item);
                else if (item.equals("Elevator - F/O"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        elevatorFo = modifySavedComponents(index, false, item);
                    else
                        elevatorFo = modifySavedComponents(index, true, item);
                else if (item.equals("Rudder - Capt"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        rudderCpt = modifySavedComponents(index, false, item);
                    else
                        rudderCpt = modifySavedComponents(index, true, item);
                else if (item.equals("Rudder - F/O"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        rudderFo = modifySavedComponents(index, false, item);
                    else
                        rudderFo = modifySavedComponents(index, true, item);
                else if (item.equals("Tiller - Capt"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        tillerCpt = modifySavedComponents(index, false, item);
                    else
                        tillerCpt = modifySavedComponents(index, true, item);
                else if (item.equals("Tiller - F/O"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        tillerFo = modifySavedComponents(index, false, item);
                    else
                        tillerFo = modifySavedComponents(index, true, item);
                else if (item.equals("Toe Brake Left - Capt"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        toeBrakeLCpt = modifySavedComponents(index, false, item);
                    else
                        toeBrakeLCpt = modifySavedComponents(index, true, item);
                else if (item.equals("Toe Brake Right - Capt"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        toeBrakeRCpt = modifySavedComponents(index, false, item);
                    else
                        toeBrakeRCpt = modifySavedComponents(index, true, item);
                else if (item.equals("Toe Brake Left - F/O"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        toeBrakeLFo = modifySavedComponents(index, false, item);
                    else
                        toeBrakeLFo = modifySavedComponents(index, true, item);
                else if (item.equals("Toe Brake Right - F/O"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        toeBrakeRFo = modifySavedComponents(index, false, item);
                    else
                        toeBrakeRFo = modifySavedComponents(index, true, item);
                else if (item.equals("Stab Trim UP - Capt"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        stabTrimUpCpt = modifySavedComponents(index, false, item);
                    else
                        stabTrimUpCpt = modifySavedComponents(index, true, item);
                else if (item.equals("Stab Trim DN - Capt"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        stabTrimDnCpt = modifySavedComponents(index, false, item);
                    else
                        stabTrimDnCpt = modifySavedComponents(index, true, item);
                else if (item.equals("Stab Trim UP - F/O"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        stabTrimUpFo = modifySavedComponents(index, false, item);
                    else
                        stabTrimUpFo = modifySavedComponents(index, true, item);
                else if (item.equals("Stab Trim DN - F/O"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        stabTrimDnFo = modifySavedComponents(index, false, item);
                    else
                        stabTrimDnFo = modifySavedComponents(index, true, item);
                else if (item.equals("AP Disc - Capt"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        apDiscCapt = modifySavedComponents(index, false, item);
                    else
                        apDiscCapt = modifySavedComponents(index, true, item);
                else if (item.equals("AP Disc - F/O"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        apDiscFo = modifySavedComponents(index, false, item);
                    else
                        apDiscFo = modifySavedComponents(index, true, item);
                else if (item.equals("PTT - Capt"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        lcpPttCpt = modifySavedComponents(index, false, item);
                    else
                        lcpPttCpt = modifySavedComponents(index, true, item);
                else if (item.equals("PTT - F/O"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        lcpPttFo = modifySavedComponents(index, false, item);
                    else
                        lcpPttFo = modifySavedComponents(index, true, item);
                else if (item.equals("TFR - Capt"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        tfrCpt = modifySavedComponents(index, false, item);
                    else
                        tfrCpt = modifySavedComponents(index, true, item);
                else if (item.equals("WX - Capt"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        wxCpt = modifySavedComponents(index, false, item);
                    else
                        wxCpt = modifySavedComponents(index, true, item);
                else if (item.equals("WX+T - Capt"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        wxtCpt = modifySavedComponents(index, false, item);
                    else
                        wxtCpt = modifySavedComponents(index, true, item);
                else if (item.equals("MAP - Capt"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        mapCpt = modifySavedComponents(index, false, item);
                    else
                        mapCpt = modifySavedComponents(index, true, item);
                else if (item.equals("GC - Capt"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        gcCpt = modifySavedComponents(index, false, item);
                    else
                        gcCpt = modifySavedComponents(index, true, item);
                else if (item.equals("AUTO"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        auto = modifySavedComponents(index, false, item);
                    else
                        auto = modifySavedComponents(index, true, item);
                else if (item.equals("L/R"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        lr = modifySavedComponents(index, false, item);
                    else
                        lr = modifySavedComponents(index, true, item);
                else if (item.equals("TEST"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        test = modifySavedComponents(index, false, item);
                    else
                        test = modifySavedComponents(index, true, item);
                else if (item.equals("TFR - F/O"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        tfrFo = modifySavedComponents(index, false, item);
                    else
                        tfrFo = modifySavedComponents(index, true, item);
                else if (item.equals("WX - F/O"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        wxFo = modifySavedComponents(index, false, item);
                    else
                        wxFo = modifySavedComponents(index, true, item);
                else if (item.equals("WX+T - F/O"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        wxtFo = modifySavedComponents(index, false, item);
                    else
                        wxtFo = modifySavedComponents(index, true, item);
                else if (item.equals("MAP - F/O"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        mapFo = modifySavedComponents(index, false, item);
                    else
                        mapFo = modifySavedComponents(index, true, item);
                else if (item.equals("GC - F/O"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        gcFo = modifySavedComponents(index, false, item);
                    else
                        gcFo = modifySavedComponents(index, true, item);
                else if (item.equals("Tilt - Capt"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        tiltCpt = modifySavedComponents(index, false, item);
                    else
                        tiltCpt = modifySavedComponents(index, true, item);
                else if (item.equals("Gain - Capt"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        gainCpt = modifySavedComponents(index, false, item);
                    else
                        gainCpt = modifySavedComponents(index, true, item);
                else if (item.equals("Tilt - F/O"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        tiltFo = modifySavedComponents(index, false, item);
                    else
                        tiltFo = modifySavedComponents(index, true, item);
                else if (item.equals("Gain - F/O"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        gainFo = modifySavedComponents(index, false, item);
                    else
                        gainFo = modifySavedComponents(index, true, item);
                else if (item.equals("Jettison Fuel-to-Remain"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        jettRemain = modifySavedComponents(index, false, item);
                    else
                        jettRemain = modifySavedComponents(index, true, item);
                else if (item.equals("Landing Altitude"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        ldgAltTurn = modifySavedComponents(index, false, item);
                    else
                        ldgAltTurn = modifySavedComponents(index, true, item);
                else if (item.equals("LCP Outbd - Capt"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        lcpOutbdCpt = modifySavedComponents(index, false, item);
                    else
                        lcpOutbdFo = modifySavedComponents(index, true, item);
                else if (item.equals("LCP ND - Capt"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        lcpNdCpt = modifySavedComponents(index, false, item);
                    else
                        lcpNdCpt = modifySavedComponents(index, true, item);
                else if (item.equals("LCP Outbd - F/O"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        lcpOutbdFo = modifySavedComponents(index, false, item);
                    else
                        lcpOutbdFo = modifySavedComponents(index, true, item);
                else if (item.equals("LCP ND - F/O"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        lcpNdFo = modifySavedComponents(index, false, item);
                    else
                        lcpNdFo = modifySavedComponents(index, true, item);
                else if (item.equals("LCP WXR - Capt"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        lcpWxrCpt = modifySavedComponents(index, false, item);
                    else
                        lcpWxrCpt = modifySavedComponents(index, true, item);
                else if (item.equals("LCP WXR - F/O"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        lcpWxrFo = modifySavedComponents(index, false, item);
                    else
                        lcpWxrFo = modifySavedComponents(index, false, item);
                else if (item.equals("EICAS BRT - Upr"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        eicasBrtUpr = modifySavedComponents(index, false, item);
                    else
                        eicasBrtUpr = modifySavedComponents(index, true, item);
                else if (item.equals("EICAS BRT - Lwr Inner"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        eicasBrtLwrInner = modifySavedComponents(index, false, item);
                    else
                        eicasBrtLwrInner = modifySavedComponents(index, true, item);
                else if (item.equals("EICAS BRT - Lwr Outer"))
                    if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                        eicasBrtLwrOuter = modifySavedComponents(index, false, item);
                    else
                        eicasBrtLwrOuter = modifySavedComponents(index, true, item);
                else if (!item.equals("None"))
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                            "SWITCH ERROR IN CODE - NOTIFY PROGRAMMER");
            }
        };

        JPanel panel = new JPanel();
        String[] dropBoxStrings = new String[]{"None",
                "Aileron - Capt", "Aileron - F/O",
                "Elevator - Capt", "Elevator - F/O",
                "Rudder - Capt", "Rudder - F/O",
                "Tiller - Capt", "Tiller - F/O",
                "Toe Brake Left - Capt", "Toe Brake Right - Capt",
                "Toe Brake Left - F/O", "Toe Brake Right - F/O",
                "Stab Trim UP - Capt", "Stab Trim DN - Capt",
                "Stab Trim UP - F/O", "Stab Trim DN - F/O",
                "AP Disc - Capt", "AP Disc - F/O",
                "PTT - Capt", "PTT - F/O",
                "TFR - Capt", "WX - Capt", "WX+T - Capt",
                "MAP - Capt", "GC - Capt",
                "AUTO", "L/R", "TEST",
                "TFR - F/O", "WX - F/O", "WX+T - F/O",
                "MAP - F/O", "GC - F/O",
                "Tilt - Capt", "Gain - Capt",
                "Tilt - F/O", "Gain - F/O",
                "Jettison Fuel-to-Remain",
                "Landing Altitude",
                "LCP Outbd - Capt", "LCP ND - Capt",
                "LCP Outbd - F/O", "LCP ND - F/O",
                "LCP WXR - Capt", "LCP WXR - F/O",
                "EICAS BRT - Upr",
                "EICAS BRT - Lwr Inner",
                "EICAS BRT - Lwr Outer"
        };

        // Get all the components saved from last time to load into current program
        String[] savedStrs = loadSavedComponents();
        String[] splitStrs;
        // Go through each controller and add its components to the UI
        JLabel label;
        JLabel valueLabel;
        CheckBox checkBox;
        JLabel nullLabel;
        ComboBox comboBox;
        for (Controller controller : controllers) {
            for (Component component : controller.getComponents()) {
                components.add(component);

                // Label #1: Current number and name of component
                label = new JLabel("  " + Integer.toString(components.size() - 1) + ". " +
                        controller.getName() + " - " +  component.getName());
                panel.add(label);
                labels.add(label);

                // Label #2: Current value of component
                valueLabel = new JLabel("", SwingConstants.CENTER);
                panel.add(valueLabel);
                valueLabels.add(valueLabel);

                // Checkbox: For analog input inversion
                checkBox = new CheckBox("Inverted", components.size() - 1);
                nullLabel = new JLabel("");
                if (component.isAnalog()) {
                    panel.add(checkBox);
                    checkBox.addItemListener(checkListener);
                } else
                    panel.add(nullLabel);
                inverted.add(false);

                // ComboBox: Select which components act for which operations
                comboBox = new ComboBox(dropBoxStrings, components.size() - 1);
                comboBox.setMaximumRowCount(30);
                comboBox.addItemListener(comboListener);
                panel.add(comboBox);

                // Configure saved components (combobox, checkbox)
                if (savedStrs != null) {
                    for (String line : savedStrs) {
                        splitStrs = line.split("`");
                        if (splitStrs.length != 3) {
                            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "saved.cfg incorrect!",
                                    "Saved Configuration Error", JOptionPane.ERROR_MESSAGE);
                            System.exit(1);
                        }
                        if (splitStrs[0].equals(label.getText())) {
                            comboBox.setSelectedItem(splitStrs[1]);
                            if (component.isAnalog())
                                if (Integer.parseInt(splitStrs[2]) == 1)
                                    checkBox.setSelected(true);
                        }
                    }
                }
            }
        }

        if (components.size() == 0)
            panel.add(new JLabel("No hardware components detected. They are either unplugged or ignored in " +
                    "'general.cfg'."));
        else {
            GridLayout grid = new GridLayout(components.size(), 4, 0, 0);
            panel.setLayout(grid);
            panel.setPreferredSize(new Dimension(780, components.size() * 30));
        }

        JScrollPane scrollPane = new JScrollPane(panel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // 569 used to offset width of bar itself
        scrollPane.setPreferredSize(new Dimension(800, 569));

        JFrame frame = new JFrame("PSX SmartInterface v1.2.5");
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

        if (minimizeOnStart)
            frame.setState(Frame.ICONIFIED);
    }

    /**
     * Loads the previously saved components from the saved config file for persistent use.
     *
     * @return an array of the lines of saved components
     */
    private static String[] loadSavedComponents() {
        try {
            BufferedReader input = new BufferedReader(new FileReader("saved.cfg"));
            ArrayList<String> strings = new ArrayList();
            String line;
            while ((line = input.readLine()) != null)
                strings.add(line);
            input.close();

            if (!strings.isEmpty())
                return strings.toArray(new String[strings.size()]);
            else
                return null;
        } catch (FileNotFoundException fnfe) {
            // Unnecessary to do anything because file will be created on first close
            return null;
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), ioe.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Modifies the list of saved components to be added to saved config file for persistent use.
     *
     * @param index the index of the item in the UI
     * @param remove true if the component should not be saved and false if it should
     * @param item the name of the control in PSX
     * @return the component that is to be saved
     */
    private static Component modifySavedComponents(int index, boolean remove, String item) {
        Component component = components.get(index);
        String reference = labels.get(index).getText();
        if (remove) {
            savedComponents.remove(reference + "`" + item);
            return null;
        } else {
            savedComponents.add(reference + "`" + item);
            return component;
        }
    }

    /**
     * Gets preferred controllers.
     */
    private static void initControllers() {
        // Copy controllers into ArrayList so they can be removed easily
        controllers = new ArrayList(Arrays.asList(ControllerEnvironment
                .getDefaultEnvironment().getControllers()));
        for (int i = 0; i < controllers.size(); i++)
            if (shouldIgnore(controllers.get(i))) {
                controllers.remove(i);
                i--;
            }
    }

    /**
     * Initializes settings based on the general config file, creating one if none is found.
     */
    private static void initConfig() {
        try {
            //* START IP/port config
            BufferedReader input = new BufferedReader((new FileReader("general.cfg")));
            input.readLine();
            input.readLine();
            input.readLine();

            String line = input.readLine();
            if (line == null || line.isEmpty()) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "general.cfg incorrect!",
                        "Network Configuration Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }

            String[] addr = line.split(":");
            if (addr.length != 2) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "general.cfg incorrect!",
                        "Network Configuration Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            String host = addr[0];
            String port = addr[1];

            client = new Client(host, Integer.parseInt(port));
            client.start();
            //* END IP/port config

            //* START Minimize UI config
            input.readLine();
            input.readLine();

            line = input.readLine();
            if (line == null || line.isEmpty()) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "general.cfg incorrect!",
                        "General Configuration Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }

            int minimize = Integer.parseInt(line);
            minimizeOnStart = minimize == 1;
            //* END Minimize UI config

            //* START Ignored controllers config
            input.readLine();

            while((line = input.readLine()) != null) {
                ignoredControllers.add(line);
            }
            //* START Ignored controllers config

            input.close();
        } catch (FileNotFoundException fnfe) {
            try {
                File file = new File("general.cfg");
                FileWriter output = new FileWriter(file, false);
                output.write("### Please do not remove any lines of this cfg; simply edit them.\n");
                output.write("## Network config\n");
                output.write("# Host/port of PSX server\n");
                output.write("localhost:10747\n");
                output.write("## General config\n");
                output.write("# Minimize window on add-on startup\n");
                output.write("0\n");
                output.write("# Ignored controller names (one per line)\n");
                output.write("Mouse\n");
                output.write("Keyboard\n");
                output.close();

                initConfig();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), e.getMessage(),
                        "Configuration Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), e.getMessage(),
                    "Configuration Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Determines if a controller should be ignored.
     *
     * @param controller the controller to be observed
     * @return true if the controller should be ignored or false if the controller should not be ignored.
     */
    private static boolean shouldIgnore(Controller controller) {
        String name = controller.getName().toUpperCase();
        for (String ignored : ignoredControllers)
            if (name.contains(ignored.toUpperCase()))
                return true;
        return false;
    }

    /**
     * Stops client and stream operations.
     *
     * @throws UnsupportedEncodingException if encoding unsupported
     * @throws FileNotFoundException if file not found
     */
    private static void stop() throws IOException {
        // Write current components to config file (saving them)
        File file = new File("saved.cfg");
        FileWriter output = new FileWriter(file, false);
        for (String line : savedComponents) {
            int index = Integer.parseInt(line.substring(2, line.indexOf('.')));
            int invert = inverted.get(index) ? 1 : 0;
            output.write(line + "`" + invert + "\n");
        }
        output.close();

        client.destroyConnection();
        System.exit(0);
    }

}
