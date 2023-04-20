package DistributedSystems;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SlaveA {
    private final String masterIp;
    private final int port;
    private Socket slaveSocket;
    private PrintWriter out;
    private BufferedReader in;
    private BlockingQueue<String> jobQueueA;

    public SlaveA(String masterIp, int port) {
        this.masterIp = masterIp;
        this.port = port;
        this.jobQueueA = new LinkedBlockingQueue<>();
    }

    public static void main(String[] args) {
        String masterIp = "localhost";
        int port = SlaveAThread.PORT_SLAVES_A;
        SlaveA slaveA = new SlaveA(masterIp, port);
        slaveA.start();
    }
    public void start() {
        try {
            slaveSocket = new Socket(masterIp, port);
            out = new PrintWriter(slaveSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(slaveSocket.getInputStream()));
            out.println("Slave A connected to Master " + slaveSocket.getInetAddress());
            System.out.println("Slave A connected to Master " + slaveSocket.getInetAddress());
            while (!Thread.interrupted()) {
                String inputLine = in.readLine();
                if (inputLine == null) {
                    break;
                }
                jobQueueA.offer(inputLine);
                out.println("Slave A received job from master: " + inputLine);
                completeTheJob(jobQueueA);
            }
        } catch (IOException e) {
            System.out.println("Error connecting to Master: " + e.getMessage());
        }
    }

    private void completeTheJob(BlockingQueue<String> jobQueueA) {
        try {
            while (true) {
                String job = jobQueueA.take();
                if (job.charAt(0) == 'A') {
                    Thread.sleep(Master.OPTIMAL_JOB_TIME); // Sleep for 2 seconds
                    SlaveHandler.sendJobCompletedMessage("A", job, out);
                    return;
                } else if (job.charAt(0) == 'B') {
                    Thread.sleep(Master.NON_OPTIMAL_JOB_TIME); // Sleep for 10 seconds
                    SlaveHandler.sendJobCompletedMessage("A", job, out);
                    return;
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Error processing job: " + e.getMessage());
        }
    }
}