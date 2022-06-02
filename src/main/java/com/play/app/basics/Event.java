package com.play.app.basics;

import java.util.*;

import lombok.RequiredArgsConstructor;

/**
 * Convinient class to manages a Subject T that can be listened to
 */
@RequiredArgsConstructor
public class Event<T> {

    private final List<Listener<T>> listeners = new ArrayList<>();
    private final T owner; // TODO, bad. could deliver at run time

    final public void addListener(Listener<T> listener) {
        listeners.add(listener);
    }

    final public void removeListener(Listener<T> listener) {
        listeners.remove(listener);
    }

    // The method that owner should call
    final public void fire() {
        for (final Listener<T> l : listeners) {
            l.hey(owner);
        }
    }

    // The method that owner should call
    // v2, better?
    final public void fire(T e) {
        for (final Listener<T> l : listeners) {
            l.hey(e);
        }
    }

}
