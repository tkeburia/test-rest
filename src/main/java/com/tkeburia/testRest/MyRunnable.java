package com.tkeburia.testRest;

public abstract class MyRunnable<T> implements Runnable {
    protected final T t;

    protected MyRunnable(T t) {
        this.t = t;
    }

    public void run() { }
}