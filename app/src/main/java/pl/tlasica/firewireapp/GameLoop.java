package pl.tlasica.firewireapp;


import android.util.Log;

public class GameLoop implements Runnable {

    private boolean running = true;
    private final String TAG = "GAMELOOP";

    @Override
    public void run() {
        Log.d(TAG, "GameLoop started");
        while (running) {
            processInput();
            doPhysics();
            drawGraphics();
            waitForNextFrame();
        }
    }

    private void waitForNextFrame() {
        long millis = 1000;
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void drawGraphics() {
    }

    private void doPhysics() {

    }

    private void processInput() {

    }

    public void pleaseStop() {
        Log.i(TAG, "Requested GameLoop to stop");
        running = false;
    }
}
