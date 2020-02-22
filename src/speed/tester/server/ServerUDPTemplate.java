package speed.tester.server;

import static java.lang.Thread.sleep;

public class ServerUDPTemplate extends Server implements Runnable {

    public void terminateConnection () {
        ;
    }

    ServerUDPTemplate (int receivedDataSize) {

        this.receivedDataSize = receivedDataSize;
    }

    @Override
    public void run() {
        try {
            while (!isStopped) {
                sleep(10L);
                arrays.add(getBuffer(receivedDataSize));
                templateCounter += receivedDataSize;
                arrays.remove(0);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setIsStopped(boolean isStopped) {
        this.isStopped = isStopped;
    }

    public long getTemplateCounter() {
        return templateCounter;
    }
}


