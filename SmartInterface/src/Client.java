import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Handles socket creation and destruction as well calculating and exchanging
 * data with the PSX server.
 *
 * @author Eric Lindau
 */
class Client extends Thread {

    // Socket
    private Socket socket;
    // Input stream
    private BufferedReader input;
    // Output stream
    private PrintWriter output;

    private Value fltControls = new Value();
    private Value tillers = new Value();
    private Value toeBrakes = new Value();
    private Value stabTrimCpt = new Value();
    private Value stabTrimFo = new Value();
    private Value apDisc = new Value();
    private Value lcpPttCpt = new Value();
    private Value lcpPttFo = new Value();
    private Value rdrPanel = new Value();

    // Buffers to be combined as String sent for radar panel button values
    private char[] rdrStrCpt, rdrStrFo;
    private char[] rdrStrMisc = new char[3];

    // Constructor
    Client(String address, int port) {
        try {
            this.socket = new Socket(address, port);
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ioe) {
            String error = "Error connecting to PSX! Please ensure that a PSX server " +
                    "is running on localhost and that port 10747 is unrestricted.";
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                    error, "PSX SmartInterface Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public void run() {
        try {
            while (true) {
                receive();

                //* Update analog values
                // Flight controls: Elevator, aileron, rudder
                int aileron = Utils.combineAnalog(SmartInterface.aileronCpt,
                        SmartInterface.aileronFo);
                int elevator = Utils.combineAnalog(SmartInterface.elevatorCpt,
                        SmartInterface.elevatorFo);
                int rudder = Utils.combineAnalog(SmartInterface.rudderCpt,
                        SmartInterface.rudderFo);
                fltControls.setStr("Qs120=" + Integer.toString(elevator) + ";" +
                        Integer.toString(aileron) + ";" + Integer.toString(rudder));
                if (fltControls.hasChanged())
                    send(fltControls.getStr());

                // Tillers
                int tiller = Utils.combineAnalog(SmartInterface.tillerCpt,
                        SmartInterface.tillerFo);
                tillers.setStr("Qh426=" + Integer.toString(tiller));
                if (tillers.hasChanged())
                    send(tillers.getStr());

                // Toe brakes
                int toeBrakeL = Utils.combineAnalog(SmartInterface.toeBrakeLCpt,
                        SmartInterface.toeBrakeLFo);
                int toeBrakeR = Utils.combineAnalog(SmartInterface.toeBrakeRCpt,
                        SmartInterface.toeBrakeRFo);
                toeBrakes.setStr("Qs357=" + Integer.toString(toeBrakeL) + ";" + Integer.toString(toeBrakeR));
                if (toeBrakes.hasChanged())
                    send(toeBrakes.getStr());
                //*

                //* Update misc buttons
                // Stab trim (captain)
                if (Utils.isPushed(SmartInterface.stabTrimUpCpt))
                    stabTrimCpt.setStr("Qh398=1");
                else if (Utils.isPushed(SmartInterface.stabTrimDownCpt))
                    stabTrimCpt.setStr("Qh398=-1");
                else
                    stabTrimCpt.setStr("Qh398=0");
                if (stabTrimCpt.hasChanged())
                    send(stabTrimCpt.getStr());

                // Stab trim (first officer)
                if (Utils.isPushed(SmartInterface.stabTrimUpFo))
                    stabTrimFo.setStr("Qh399=1");
                else if (Utils.isPushed(SmartInterface.stabTrimDownFo))
                    stabTrimFo.setStr("Qh399=-1");
                else
                    stabTrimFo.setStr("Qh399=0");
                if (stabTrimFo.hasChanged())
                    send(stabTrimFo.getStr());

                // AP Disc
                if (Utils.isPushed(SmartInterface.apDisc))
                    apDisc.setStr("Qh400=1");
                else
                    apDisc.setStr("Qh400=0");
                if (apDisc.hasChanged())
                    send(apDisc.getStr());

                // PTT (captain)
                if (Utils.isPushed(SmartInterface.lcpPttCpt))
                    lcpPttCpt.setStr("Qh82=1");
                else
                    lcpPttCpt.setStr("Qh82=0");
                if (lcpPttCpt.hasChanged())
                    send(lcpPttCpt.getStr());

                // PTT (first officer)
                if (Utils.isPushed(SmartInterface.lcpPttFo))
                    lcpPttFo.setStr("Qh93=1");
                else
                    lcpPttFo.setStr("Qh93=0");
                if (lcpPttFo.hasChanged())
                    send(lcpPttFo.getStr());
                //*

                //* Update radar panel buttons
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

                // Concat and send
                String rdrPanelString = new String(rdrStrCpt) + new String(rdrStrMisc) + new String(rdrStrFo);
                rdrPanel.setStr(rdrPanelString);
                if (rdrPanel.hasChanged())
                    send("Qs104=" + rdrPanelString);
                //*

                // Delay to prevent network/buffer flooding
                sleep(100);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Kill connection
    void destroyConnection() {
        try {
            socket.close();
            output.close();
            input.close();
        } catch (Exception e) {
            System.exit(1);
        }

    }

    // Send data to PSX server
    void send(String data) {
        if (!data.isEmpty())
            output.println(data);
    }

    // Receive one line of data from PSX
    void receive() throws IOException {
        input.readLine();
    }

}
