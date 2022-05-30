package com.play.app.basics;

/**
 * subscribe to anything interesting happened to T
 */
public interface Listener<T> {
    public void hey(T target);
}
