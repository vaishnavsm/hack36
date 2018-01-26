package com.hack36.Interfaces;

public interface MyTimerInterface {

    void registerObserver(MyTimerObserver observer);

    void removeObserver(MyTimerObserver observer);

    void notifyObservers();

}
