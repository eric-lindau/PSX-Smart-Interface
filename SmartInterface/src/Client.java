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

    // Constructor
    Client(String address, int port) {
        try {
            this.socket = new Socket(address, port);
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ioe) {
            String error = "Error connecting to PSX! Please ensure that a PSX" +
                    " server is running and that port 10747 is unrestricted.";
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                    error, "PSX SmartInterface Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public void run() {
        try {
            while(true) {
                System.out.print("x");
                SmartInterface.aileron = SmartInterface.combineAnalog(SmartInterface.aileronCpt, SmartInterface.aileronFo);
                SmartInterface.elevator = SmartInterface.combineAnalog(SmartInterface.elevatorCpt, SmartInterface.elevatorFo);
                SmartInterface.rudder = SmartInterface.combineAnalog(SmartInterface.rudderCpt, SmartInterface.rudderFo);
                SmartInterface.tiller = SmartInterface.combineAnalog(SmartInterface.tillerCpt, SmartInterface.tillerFo);
                receive();
                if (SmartInterface.elevatorCpt != null || SmartInterface.elevatorFo != null ||
                        SmartInterface.aileronCpt != null || SmartInterface.aileronFo != null ||
                        SmartInterface.rudderCpt != null || SmartInterface.rudderFo != null)
                    send("Qs120=" + Integer.toString(SmartInterface.elevator) + ";" +
                            Integer.toString(SmartInterface.aileron) + ";" + Integer.toString(SmartInterface.rudder));
                // Update tiller
                if (SmartInterface.tillerCpt != null || SmartInterface.tillerFo != null)
                    send("Qh426=" + Integer.toString(SmartInterface.tiller));
                sleep(20);
            }
        } catch(Exception e) {

        }
    }

    // Kill connection
    void destroyConnection() {
        try {
            this.socket.close();
        } catch (Exception e) {
            System.out.println("Error detected while closing socket. Exiting!");
            System.exit(1);
        }

    }

    // Send data to PSX server
    void send(String data) {
        if(!data.isEmpty())
            this.output.println(data);
    }

    void receive() throws IOException {
        input.readLine();
    }

}
