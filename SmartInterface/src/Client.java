import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Handles socket creation, destruction, and data exchange between the PSX server and the client.
 *
 * @author Eric Lindau
 */
class Client extends Thread {

    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    /**
     * Client constructor.
     *
     * @param address the IPv4 address of the server
     * @param port the port of the server
     */
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
                input.readLine();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Kills the connection by closing all buffers and the socket.
     */
    void destroyConnection() {
        try {
            socket.close();
            output.close();
            input.close();
        } catch (Exception e) {
            System.exit(1);
        }

    }

    /**
     * Sends data to the PSX server.
     *
     * @param data the data to be sent
     */
    void send(String data) {
        if (!data.isEmpty())
            output.println(data);
    }

}
