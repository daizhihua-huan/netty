package test;

import client.ClientThread;
import util.IntegerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class Test {
    public static void main(String[] args) {


        Thread t=new Thread(new ClientThread());
        t.run();
    }
}
