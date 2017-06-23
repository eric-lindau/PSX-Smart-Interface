import java.net.Socket;
import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 10747);
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
        output.println("Qh389=6");
        String answer;
        try {
            while (true) {
                answer = input.readLine();
                if (answer.charAt(1) == 's')
                    System.out.println(answer);
            }
        } catch (Exception e) {

        }

        socket.close();
    }

}
