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
public class Client {

    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    /**
     * Client constructor.
     *
     * @param address The IPv4 address of the PSX server.
     * @param port The port on which the server is listening over the network.
     */
    Client(String address, int port) {
        try {
            this.socket = new Socket(address, port);
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ioe) {
            System.out.println("Invalid input or connection failed!");
        }
    }

    /**
     * Properly destroys the socket and connection.
     */
    public void destroyConnection() {
        try {
            this.socket.close();
        } catch (Exception e) {
            System.out.println("Error detected while closing socket. Exiting!");
            System.exit(1);
        }

    }

    public void send(String data) {
        if(!data.isEmpty())
            this.output.println(data);
    }

}
