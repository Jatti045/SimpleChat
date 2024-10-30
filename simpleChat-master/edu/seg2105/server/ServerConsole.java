package edu.seg2105.server;

import edu.seg2105.client.common.ChatIF;
import edu.seg2105.edu.server.backend.EchoServer;
import ocsf.server.ConnectionToClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class ServerConsole implements ChatIF {
    final public static int DEFAULT_PORT = 5555;

    EchoServer server;
    Scanner fromConsole;

    public ServerConsole(int port) {
        try {
            server = new EchoServer(port);
            fromConsole = new Scanner(System.in);
        } catch(Exception e) {
            System.out.println("ERROR - Failed to make server!");
        }
    }

    public void accept() {
        try {
            String message;

            while (true) {
                message = fromConsole.nextLine();
                if (message.startsWith("#")) {
                    handleServerCommand(message);
                } else {
                    server.sendToAllClients("SERVER MSG> " + message);
                    display(message);
                }
            }
        } catch (Exception ex) {
            System.out.println("ERROR - Failed to read from console!");
        }
    }

    private void handleServerCommand(String message) throws IOException {
        String[] command = message.split(" ");
        switch (command[0]) {
            case "#quit" -> {
                System.out.println("Server is shutting down.");
                System.exit(0);
            }
            case "#stop" -> server.stopListening();
            case "#close" -> server.close();
            case "#setport" -> {
                if (!server.isListening() && server.getNumberOfClients() == 0) {
                    int newPort = Integer.parseInt(message.substring(8).trim());
                    server.setPort(newPort);
                    System.out.println("Server port set to " + newPort);
                } else {
                    System.out.println("Server must be closed to change the port.");
                }
            }
            case "#start" -> {
                if (!server.isListening()) {
                    server.listen();
                    System.out.println("Server has started listening for new clients.");
                }
            }
            case "#getport" -> System.out.println("Current port: " + server.getPort());
            default -> System.out.println("Please enter a valid command.");
        }
    }


    @Override
    public void display(String message) {
        System.out.println("SERVER MSG> " + message);
    }

    public static void main(String[] args) {
        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            port = DEFAULT_PORT;
        }
        ServerConsole serverConsole = new ServerConsole(port);
        try {
            serverConsole.server.listen();
            serverConsole.accept();
        } catch (Exception e) {
            System.out.println("ERROR - Failed to listen for clients!");
        }
    }
}
