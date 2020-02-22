package speed.tester.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Server implements Runnable {

    int port;
    private Integer number1 = 15;
    int receivedDataSize;
    boolean isFirstMessage = true;
    public boolean isConnectionTerminated = false;

    long summaryDataSize;
    long currentTime;
    long transmissionTime;
    long startTime;
    double transmissionSpeed;

    List<byte[]> arrays = new ArrayList<byte[]>();
    boolean isStopped = true;
    long templateCounter = 0L;

    public byte[] getBuffer(int dataSize) {
        byte[] data = new byte[dataSize];
        Arrays.fill(data, number1.byteValue());
        return data;
    }
/*
    public static int checkSum(byte[] input) {
        int checkSum = 0;
        for(byte b : input) {
            checkSum += b & 0xFF;
        }
        return checkSum;
    }

 */

    public abstract void terminateConnection();

    public void dataCounter() {
        summaryDataSize += receivedDataSize;
        currentTime = System.currentTimeMillis();
        transmissionTime = currentTime - startTime;
        transmissionSpeed = (summaryDataSize / 1024.0D) / (transmissionTime / 1000.0D);
    }

    public void resetDataCounter() {
        summaryDataSize = 0L;
        receivedDataSize = 0;
        transmissionTime = 0L;
        transmissionSpeed = 0.0D;
        startTime = 0L;
        isFirstMessage = true;
    }
}
