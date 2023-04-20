package DistributedSystems;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.UUID;


public class Client {
    private static final String MASTER_IP = "localhost";
    protected static final int PORT_MASTER = ClientThread.PORT_CLIENT;
    protected String clientId;
    private Socket socketMaster;
    private PrintWriter out;
    private BufferedReader in;
    private static List<ClientHandler> clients = new ArrayList<>();


    public Client(){
        this.clientId = String.valueOf(UUID.fromString(UUID.randomUUID().toString()));
        System.out.println("Client ID = " + clientId);
    }
    public static void main(String[] args) {
        Client clientObj = new Client();
        clientObj.start();
    }

    public void start(){
        try {
            // connect to port in master
            socketMaster = new Socket(MASTER_IP, PORT_MASTER);
            // create input and output streams to send and receive messages
            in = new BufferedReader(new InputStreamReader(socketMaster.getInputStream()));
            out = new PrintWriter(socketMaster.getOutputStream(), true);
            System.out.println("Connected to the Master");
            out.println("Client " + clientId + " connected to Master");

            ClientHandler clientHandler = new ClientHandler(socketMaster, clients, out);
            new Thread(clientHandler).start();
            clients.add(clientHandler);

            Scanner scanner = new Scanner(System.in);
            label:
            while (true) {
                String jobType;
                String jobId;
                String jobSubmission;

                System.out.println("Enter job type (A or B) or exit to quit: ");
                jobType = scanner.nextLine();

                switch (jobType) {
                    case "A":
                    case "a":
                        jobType = "A";
                        break;
                    case "B":
                    case "b":
                        jobType = "B";
                        break;
                    case "exit":
                        System.out.println("Current client has exited the program.");
                        break label;
                    default:
                        System.out.println("Invalid entry...");
                        continue;
                }
                System.out.println("Enter job ID: ");
                jobId = scanner.nextLine();

                jobSubmission = jobType + "," + clientId + "!" + jobId;
                System.out.println("Client has received job from user " + jobId);

                //Send job to master.
                out.println(jobSubmission);
                out.flush();
                System.out.println("\t\tJob " + jobId + " of type " + jobType + " has been sent from Client to Master.");
            }
            scanner.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}