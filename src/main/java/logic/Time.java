package logic;

import javafx.application.Platform;
import javafx.scene.control.Label;

import static java.lang.Thread.sleep;



public class Time implements Runnable {

    private int seconds = 0;
    private int minutes = 0;
    private int hours = 0;
    private Label timeLabel;
    private static boolean running = true;

    public Time(Label timeLabel) {
        this.timeLabel = timeLabel;
    }

    @Override
    public void run() {
        while (running) {
            try {

                Platform.runLater(() -> timeLabel.setText(this.toString()));
                sleep(1000);
                seconds++;
                if (seconds == 60) {
                    minutes++;
                    seconds = 0;
                }
                if (minutes == 60) {
                    hours++;
                    minutes = 0;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void stop() {
        running = false;
    }

    public void restart() {

        running = true;
        seconds = 0;
        minutes = 0;
        hours = 0;
    }

    private String toString(int value) {
        return (value > 9 ? String.valueOf(value) : "0" + value);
    }

    @Override
    public String toString() {
        return toString(hours) + ":" + toString(minutes) + ":" + toString(seconds);
    }
}
