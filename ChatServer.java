/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.p6test;

/**
 *
 * @author jossu
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private static final int PORT = 8087;
    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Chat Server listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received from client: " + inputLine);
                    if (inputLine.equalsIgnoreCase("hola")) {
                        out.println("hola");
                    } else if (inputLine.toLowerCase().startsWith("enviar")) {
                        String message = inputLine.substring(7);
                        broadcast(message);
                    } else if (inputLine.equalsIgnoreCase("adios")) {
                        out.println("adios");
                        break;
                    }
                }

                clientSocket.close();
                clients.remove(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void broadcast(String message) {
            for (ClientHandler client : clients) {
                client.out.println(message);
            }
        }
    }
}
