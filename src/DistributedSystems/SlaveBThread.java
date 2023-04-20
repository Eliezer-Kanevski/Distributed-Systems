package DistributedSystems;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SlaveBThread implements Runnable{
    protected static final int PORT_SLAVES_B = 1235;
    protected static ServerSocket slaveB_SSocket;
    protected static Socket slaveBSocket;

    @Override
    public void run() {
        slaveBSocket = null;
        try {
            slaveB_SSocket = new ServerSocket(PORT_SLAVES_B);
            slaveBSocket = slaveB_SSocket.accept();
            System.out.println("Slave B connected: " + slaveBSocket.getInetAddress().getHostAddress());
            BufferedReader inB = new BufferedReader(new InputStreamReader(slaveBSocket.getInputStream()));
            PrintWriter outB = new PrintWriter(slaveBSocket.getOutputStream(), true);
            String slaveName = "B";// determine the name of the current slave
            Master.slaveOutputMap.put(slaveName, outB);

            // create a new thread to handle communication with this slave
            SlaveHandler slaveHandler = new SlaveHandler(slaveBSocket, inB, outB, "B");
            new Thread(slaveHandler).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}