package DistributedSystems;



import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class Master {
    protected static final int OPTIMAL_JOB_TIME = 2000; // 2 seconds
    protected static final int NON_OPTIMAL_JOB_TIME = 10000; // 10 seconds
    protected static Map<String, PrintWriter> slaveOutputMap = new HashMap<>();
    protected static Map<String, PrintWriter> clientOutputMap = new HashMap<>();
    private static int slaveAjobs = 0;
    private static int slaveBjobs = 0;


    public static void main(String[] args) {
        System.out.println("DistributedSystems.Master is waiting for slaves and clients to connect...");

        Thread slaveAThread = new Thread(new SlaveAThread());
        slaveAThread.start();
        Thread slaveBThread = new Thread(new SlaveBThread());
        slaveBThread.start();
        Thread clientThread = new Thread (new ClientThread());
        clientThread.start();

        try {
            slaveAThread.join();
            slaveBThread.join();
            clientThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Method that calculates which slave would complete the job quickest.
    public static String determineSlave(String job){
        String jobType = job.substring(0,1);
        int slaveAJobs = getSlaveAjobs();
        int slaveBJobs = getSlaveBjobs();

        if(slaveAJobs < 6 && slaveBJobs < 6){
            if(jobType.equals("A")){
                setSlaveAjobs(getSlaveAjobs() + 1);
            }
            else {
                setSlaveBjobs(getSlaveBjobs() + 1);
            }
            return jobType;
        }
        if(jobType.equals("A") && slaveAJobs - slaveBJobs > 5){
            setSlaveBjobs(getSlaveBjobs() + 5);
            return "B";
        } else if (jobType.equals("B") && slaveBJobs - slaveAJobs > 5) {
            setSlaveAjobs(getSlaveAjobs() + 5);
            return "A";
        } else if (jobType.equals("A")) {
            setSlaveAjobs(getSlaveAjobs() + 1);
        }
        else {
            setSlaveBjobs(getSlaveBjobs() + 1);
        }
        return jobType;
    }

    //send the job to the right slave by getting the correct PrintWriter from the map.
    protected static void sendJobToSlave(String inputLine) {
        String determinedSlave = determineSlave(inputLine);
        PrintWriter slaveOutput = slaveOutputMap.get(determinedSlave);
        slaveOutput.println(inputLine);
    }

    //Method to decrement the amount of time it takes to complete the job once a job is completed.
    //Time to complete jobs have been reduced to their rates so optimal jobs would be 1 and non-optimal would be worth 5.
    public static void decrementJobMap(String job, String slave){
        String jobType = job.substring(0,1);
        if(slave.equals(job)){
            if(slave.equals("A")){
                setSlaveAjobs(getSlaveAjobs() - 1);
            }
            else{
                setSlaveBjobs(getSlaveBjobs() - 1);
            }
        } else if (jobType.equals("A")) {
            setSlaveBjobs(getSlaveBjobs() - 5);
        } else if (jobType.equals("B")) {
            setSlaveAjobs(getSlaveAjobs() - 5);
        }
    }

    public static int getSlaveAjobs() {
        return slaveAjobs;
    }

    public static void setSlaveAjobs(int slaveAjobs) {
        Master.slaveAjobs = slaveAjobs;
    }
    public static int getSlaveBjobs() {
        return slaveBjobs;
    }

    public static void setSlaveBjobs(int slaveBjobs) {
        Master.slaveBjobs = slaveBjobs;
    }
}