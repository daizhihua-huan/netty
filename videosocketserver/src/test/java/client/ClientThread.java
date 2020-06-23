package client;

import java.io.UnsupportedEncodingException;

public class ClientThread implements Runnable {
    @Override
    public void run() {

        try {
                new ClientNetty("127.0.0.1", 8080).start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
