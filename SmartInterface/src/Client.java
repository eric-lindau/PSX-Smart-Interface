import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Handles socket creation and destruction as well exchanging data with the
 * PSX server.
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

    //* Buffers for combined analog values
    private int aileron, elevator, rudder;
    private int tiller;
    private int toeBrakeL, toeBrakeR;
    //*

    // Constructor
    Client(String address, int port) {
        try {
            this.socket = new Socket(address, port);
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ioe) {
            String error = "Error connecting to PSX! Please ensure that a PSX server " +
                    "is running and that port 10747 is unrestricted.";
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                    error, "PSX SmartInterface Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public void run() {
        try {
            while(true) {
                //* Update combined analog values
                aileron = SmartInterface.combineAnalog(SmartInterface.aileronCpt,
                        SmartInterface.aileronFo);
                elevator = SmartInterface.combineAnalog(SmartInterface.elevatorCpt,
                        SmartInterface.elevatorFo);
                rudder = SmartInterface.combineAnalog(SmartInterface.rudderCpt,
                        SmartInterface.rudderFo);

                tiller = SmartInterface.combineAnalog(SmartInterface.tillerCpt,
                        SmartInterface.tillerFo);

                toeBrakeL = SmartInterface.combineAnalog(SmartInterface.toeBrakeLCpt,
                        SmartInterface.toeBrakeLFo);
                toeBrakeR = SmartInterface.combineAnalog(SmartInterface.toeBrakeRCpt,
                        SmartInterface.toeBrakeRFo);
                //*

                // Receive to prevent PSX buffers filling
                receive();

                //* Update flight controls (PSX)
                if(SmartInterface.elevatorCpt != null || SmartInterface.elevatorFo != null ||
                        SmartInterface.aileronCpt != null || SmartInterface.aileronFo != null ||
                        SmartInterface.rudderCpt != null || SmartInterface.rudderFo != null)
                    send("Qs120=" + Integer.toString(elevator) + ";" + Integer.toString(aileron)
                            + ";" + Integer.toString(rudder));
                //*

                //* Update tillers (PSX)
                if(SmartInterface.tillerCpt != null || SmartInterface.tillerFo != null)
                    send("Qh426=" + Integer.toString(tiller));
                //*

                //* Update toe brakes (PSX)
                if(SmartInterface.toeBrakeLCpt != null || SmartInterface.toeBrakeRCpt != null ||
                        SmartInterface.toeBrakeLFo != null || SmartInterface.toeBrakeRFo != null)
                    send("Qs357=" + Integer.toString(toeBrakeL) + ";" + Integer.toString(toeBrakeR));
                //*

                // Delay to prevent network/buffer flooding
                sleep(20);
            }
        } catch(Exception e) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Kill connection
    void destroyConnection() {
        try {
            this.socket.close();
        } catch (Exception e) {
            System.exit(1);
        }

    }

    // Send data to PSX server
    void send(String data) {
        if(!data.isEmpty())
            this.output.println(data);
    }

    // Receive one line of data from PSX
    void receive() throws IOException {
        input.readLine();
    }

}
