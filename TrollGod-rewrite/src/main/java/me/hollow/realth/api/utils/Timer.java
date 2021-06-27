package me.hollow.realth.api.utils;

public class Timer {

    private long current;

    public Timer() {
        this.current = System.currentTimeMillis();
    }

    public boolean passed(final long delay) {
        return System.currentTimeMillis() - this.current >= delay;
    }

    public void reset() {
        this.current = System.currentTimeMillis();
    }

    public long time() {
        return System.currentTimeMillis();
    }

    private long time;

    public boolean reach(final long time) {
        return time() >= time;
    }


    public boolean sleep(final long time) {
        if (time() >= time) {
            reset();
            return true;
        }
        return false;
    }
}
