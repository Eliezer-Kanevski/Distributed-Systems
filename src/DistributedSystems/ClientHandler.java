package DistributedSystems;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    protected PrintWriter out;

    public ClientHandler(Socket socket, List<ClientHandler> clients, PrintWriter out) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = out;
    }

    @Override
    public void run() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        // process the incoming message from the master
                        // check if the message is related to job completion and inform the client
                        if (message.contains("completed job")) {
                            // Code to parse out a neater message for the client to see without the metadata sent from slave.
                            char jobType = message.charAt(22);
                            int index = message.indexOf("!");
                            String jobId = message.substring(index +1);
                            String completedJobMessage = message.substring(0,22)  + jobId + ", of type " + jobType;

                            System.out.println("\t\t>>>" + completedJobMessage + "<<<");
                        } else if (message.startsWith("Master received job:")) {
                            System.out.println("\t\tReceived message from master: " + message);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}