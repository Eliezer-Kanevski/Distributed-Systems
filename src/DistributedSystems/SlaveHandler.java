package DistributedSystems;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;

class SlaveHandler implements Runnable {
    private final Socket socket;
    private final BufferedReader in;
    private PrintWriter out;
    private final String slaveName;

    public SlaveHandler(Socket socket, BufferedReader in, PrintWriter out, String slaveName) {
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.slaveName = slaveName;
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                // process the incoming message from the slave
                System.out.println("\t\tReceived message from slave: " + message);
                // check if the message is a job submission and decrement the appropriate slavejobs variable.

                if (message.contains("completed job A")) {
                    Master.decrementJobMap("A", slaveName);
                } else if (message.contains("completed job B")) {
                    Master.decrementJobMap("B", slaveName);
                }
                String job;
                if (message.contains("completed job")) {
                    int startIndex = message.indexOf("completed job") + "completed job".length();
                    job = message.substring(startIndex).trim();
                    // Get client PrintWriter and send job completed message to client.
                    PrintWriter clientOut = Master.clientOutputMap.get(job.substring(2, 38));
                    clientOut.println(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendJobCompletedMessage(String slaveName, String job, PrintWriter out) {
        String message = "Slave " + slaveName + " completed job " + job;
        // send the message using the PrintWriter
        out.println(message);
    }
}
