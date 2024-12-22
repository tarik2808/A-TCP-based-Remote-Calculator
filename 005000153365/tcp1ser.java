// tcp1ser.java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class tcp1ser {
    private static long accumulator = 0;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Syntax: tcp1ser port_number");
            return;
        }

        int port = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started. Waiting for client...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected.");

                handleClient(clientSocket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {

            String clientInput;
            while ((clientInput = in.readLine()) != null) {
                String[] parts = clientInput.split(",");
                int type = Integer.parseInt(parts[0]);
                int arg1 = Integer.parseInt(parts[1]);
                int arg2 = Integer.parseInt(parts[2]);

                long result = 0;
                switch (type) {
                    case 1:
                        result = arg1 + arg2;
                        break;
                    case 2:
                        result = arg1 - arg2;
                        break;
                    case 3:
                        result = arg1 * arg2;
                        break;
                    case 4:
                        result = arg1 / arg2;
                        break;
                    case 5:
                        result = arg1 % arg2;
                        break;
                    case 6:
                        result = factorial(arg1);
                        break;
                    default:
                        System.out.println("Invalid operation type.");
                }

                accumulator += result;

                out.write(createMessage(16, accumulator).getBytes());
                out.flush();
            }

            clientSocket.close();
            System.out.println("Client disconnected.");
        } catch (IOException e) {
            System.out.println("Client connection terminated abruptly.");
        }
    }

    private static String createMessage(int type, long value) {
        return type + "," + value;
    }

    private static long factorial(int n) {
        if (n == 0)
            return 1;
        else
            return n *factorial(n - 1);
        }
    }