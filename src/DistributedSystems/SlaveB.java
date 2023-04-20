package DistributedSystems;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SlaveB {
    private final String masterIp;
    private final int port;
    private Socket slaveSocket;
    private PrintWriter out;
    private BufferedReader in;
    private BlockingQueue<String> jobQueueB;

    public SlaveB(String masterIp, int port) {
        this.masterIp = masterIp;
        this.port = port;
        this.jobQueueB = new LinkedBlockingQueue<>();
    }

    public static void main(String[] args) {
        String masterIp = "localhost";
        int port = SlaveBThread.PORT_SLAVES_B;
        SlaveB slaveB = new SlaveB(masterIp, port);
        slaveB.start();
    }
    public void start() {
        try {
            slaveSocket = new Socket(masterIp, port);
            out = new PrintWriter(slaveSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(slaveSocket.getInputStream()));
            out.println("Slave B connected to Master " + slaveSocket.getInetAddress());
            System.out.println("Slave B connected to Master " + slaveSocket.getInetAddress());
            while (!Thread.interrupted()) {
                String inputLine = in.readLine();
                if (inputLine == null) {
                    break;
                }
                jobQueueB.offer(inputLine);
                out.println("Slave B received job from master: " + inputLine);
                completeTheJob(jobQueueB);
            }
        } catch (IOException e) {
            System.out.println("Error connecting to Master: " + e.getMessage());
        }
    }

    private void completeTheJob(BlockingQueue<String> jobQueueB) {
        try {
            while (true) {
                String job = jobQueueB.take();
                if (job.charAt(0) == 'B') {
                    Thread.sleep(Master.OPTIMAL_JOB_TIME); // Sleep for 2 seconds
                    SlaveHandler.sendJobCompletedMessage("B", job, out);
                    return;
                } else if (job.charAt(0) == 'A') {
                    Thread.sleep(Master.NON_OPTIMAL_JOB_TIME); // Sleep for 10 seconds
                    SlaveHandler.sendJobCompletedMessage("B", job, out);
                    return;
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Error processing job: " + e.getMessage());
        }
    }
}