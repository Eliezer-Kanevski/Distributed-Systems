package DistributedSystems;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SlaveAThread implements Runnable{
    protected static final int PORT_SLAVES_A = 1234;
    protected static ServerSocket slaveA_SSocket;
    protected static Socket slaveASocket;

    @Override
    public void run() {
        slaveASocket = null;
        try {
            slaveA_SSocket = new ServerSocket(PORT_SLAVES_A);
            slaveASocket = slaveA_SSocket.accept();
            System.out.println("Slave A connected: " + slaveASocket.getInetAddress().getHostAddress());
            BufferedReader inA = new BufferedReader(new InputStreamReader(slaveASocket.getInputStream()));
            PrintWriter outA = new PrintWriter(slaveASocket.getOutputStream(), true);
            String slaveName = "A";
            Master.slaveOutputMap.put(slaveName, outA);

            // create a new thread to handle communication with this slave
            SlaveHandler slaveHandler = new SlaveHandler(slaveASocket, inA, outA, "A");
            new Thread(slaveHandler).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}