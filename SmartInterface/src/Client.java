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
                // Empty incoming buffer
                receive();
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
