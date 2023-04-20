package DistributedSystems;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientThread implements Runnable{
    protected static final int PORT_CLIENT = 1236;
    protected static ServerSocket client_SSocket;
    protected static Socket clientSocket;

    @Override
    public void run(){
        clientSocket = null;
        try {
            client_SSocket = new ServerSocket(PORT_CLIENT);
            while (true) {
                try {
                    clientSocket = client_SSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                    Thread clientHandlerThread = new Thread(() -> {
                        BufferedReader in;
                        PrintWriter out;
                        try {
                            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                            out = new PrintWriter(clientSocket.getOutputStream(), true);
                            while (true) {
                                String request = in.readLine();
                                if (request != null) {
                                    if (request.contains("connected to Master")) {
                                        System.out.println(request);
                                        Master.clientOutputMap.put(request.substring(7, 43), out);
                                    }
                                    else {
                                        out.println("Master received job: " + request);
                                        // SendJobToSlave method calls the determineSlave method and then sends the job to the correct slave.
                                        Master.sendJobToSlave(request);
                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    clientHandlerThread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
