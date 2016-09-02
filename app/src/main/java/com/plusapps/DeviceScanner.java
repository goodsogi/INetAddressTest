package com.plusapps;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by bagjeong-gyu on 2016. 8. 16..
 */
public class DeviceScanner {
    private static final int NB_THREADS = 10;
    private static final String LOG_TAG = "DeviceScanner";

    /**
     * 멀티쓰레드로 실행하는 데 이게 문제가 없나?? host 이름이 잘못 매칭되지 않나??
     * 하나의 연결되는 장치가 있으면 host 이름이 맞는지 테스트 필요!!
     * 이게 문제 없으면 속도가 빠르므로 멀티쓰레드를 사용해야 함
     */

    public void run() {
        Log.i(LOG_TAG, "Start scanning");

        ExecutorService executor = Executors.newFixedThreadPool(NB_THREADS);
        for(int dest=0; dest<255; dest++) {
            String host = "192.168.1." + dest;
            executor.execute(pingRunnable(host));
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log.i(LOG_TAG, "Waiting for executor to terminate...");
        executor.shutdown();
        try { executor.awaitTermination(60*1000, TimeUnit.MILLISECONDS); } catch (InterruptedException ignored) { }

        Log.i(LOG_TAG, "Scan finished");
    }


    /**
     * NetworkOnMainThreadException이 발생해서 쓰레드 사용
     */

    public void runWithoutThread() {

        Thread thread = new TestThread();
        thread.start();


    }

    private Runnable pingRunnable(final String host) {
        return new Runnable() {
            public void run() {
                Log.d(LOG_TAG, "Pinging " + host + "...");
                try {
                    InetAddress inet = InetAddress.getByName(host);
                    boolean reachable = inet.isReachable(1000);
                    Log.d(LOG_TAG, "=> Result: " + host + " " + (reachable ? "reachable" : "not reachable"));
                } catch (UnknownHostException e) {
                    Log.e(LOG_TAG, "Not found", e);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "IO Error", e);
                }
            }
        };
    }

    private class TestThread extends Thread{
        @Override
        public void run() {
            super.run();
            Log.i(LOG_TAG, "Start scanning");

            for(int dest=0; dest<255; dest++) {
                String host = "192.168.1." + dest;
                Log.d(LOG_TAG, "Pinging " + host + "...");
                try {
                    InetAddress inet = InetAddress.getByName(host);
                    boolean reachable = inet.isReachable(1000);
                    Log.d(LOG_TAG, "=> Result: " + host + " " + (reachable ? "reachable" : "not reachable"));
                } catch (UnknownHostException e) {
                    Log.e(LOG_TAG, "Not found", e);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "IO Error", e);
                }
            }



            Log.i(LOG_TAG, "Scan finished");
        }
    }
}
