package speed.tester.client;

import java.util.Arrays;

public abstract class Client implements Runnable {

    public String serverName;
    public int port;
    public int dataSize;
    public boolean nagle;
    public boolean isConnectionTerminated = false;
    private Integer number1 = 15;

    public byte[] getBuffer() {
        byte[] data = new byte[dataSize];
        Arrays.fill(data, number1.byteValue());
        return data;
    }
/*
    public int countCheckSum(byte[] input) {
        int checkSum = 0;
        for(byte b : input) {
            checkSum += b & 0xFF;
        }
        return checkSum;
    }


 */


    public abstract void terminateConnection();
}
