/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.p6test;

/**
 *
 * @author jossu
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClientGUI {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8087;

    private JFrame frame;
    private JTextArea messageArea;
    private JTextField messageField;
    private JButton sendButton;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    public ChatClientGUI() {
        // Set up the GUI components
        frame = new JFrame("Chat Client");
        messageArea = new JTextArea(20, 40);
        messageField = new JTextField(30);
        sendButton = new JButton("Send");

        messageArea.setEditable(false);

        JPanel panel = new JPanel();
        panel.add(messageField);
        panel.add(sendButton);

        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);
        frame.pack();

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void connectToServer() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Start a thread to listen for server messages
            Thread listener = new Thread(new Listener());
            listener.start();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to connect to server.");
        }
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (message != null && !message.trim().isEmpty()) {
            out.println(message);
            messageField.setText("");

            if (message.equalsIgnoreCase("adios")) {
                closeConnection();
            }
        }
    }

    private void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    private void showError(String errorMessage) {
        JOptionPane.showMessageDialog(frame, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private class Listener implements Runnable {
        @Override
        public void run() {
            try {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    messageArea.append("Server: " + serverMessage + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ChatClientGUI client = new ChatClientGUI();
        client.connectToServer();
    }
}

