// tcp1cli.java
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class tcp1cli {
    private static final int TIMEOUT_SECONDS = 15;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Syntax: tcp1cli ip_server_address server_port_number");
            return;
        }

        String serverAddress = args[0];
        int serverPort = Integer.parseInt(args[1]);

        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<?> future = executor.submit(() -> connectToServer(serverAddress, serverPort));

            try {
                future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                System.err.println("Connection timed out. Exiting.");
                executor.shutdownNow();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void connectToServer(String serverAddress, int serverPort) {
        try (Socket socket = new Socket(serverAddress, serverPort);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream()) {

            System.out.println("Connected to server.");

            String userInputLine;
            while ((userInputLine = userInput.readLine()) != null) {
                if (userInputLine.equalsIgnoreCase("QUIT")) {
                    break;
                }

                out.write(createMessage(userInputLine).getBytes());
                out.flush();

                byte[] response = new byte[1024];
                int bytesRead = in.read(response);
                String responseData = new String(response, 0, bytesRead);
                System.out.println("Server response: " + responseData);
            }

            System.out.println("Connection closed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String createMessage(String operation) {
        String[] parts = operation.split(" ");
        int type = Integer.parseInt(parts[0]);
        int arg1 = Integer.parseInt(parts[1]);
        int arg2 = Integer.parseInt(parts[2]);

        return type + "," + arg1 + "," + arg2;
    }
}