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
import java.net.Socket;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8087;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to chat server");

            // Start a thread to listen for server messages
            Thread listener = new Thread(new Listener(in));
            listener.start();

            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                if (userInput.equalsIgnoreCase("adios")) {
                    break;
                }
            }

            System.out.println("Disconnected from chat server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Listener implements Runnable {
        private BufferedReader in;

        public Listener(BufferedReader in) {
            this.in = in;
        }

        @Override
        public void run() {
            String serverMessage;
            try {
                while ((serverMessage = in.readLine()) != null) {
                    System.out.println("Server: " + serverMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
