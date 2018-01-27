package com.hack36.Services;

import android.os.Handler;

import java.util.ArrayList;

import com.hack36.Interfaces.MyTimerInterface;
import com.hack36.Interfaces.MyTimerObserver;

import static com.hack36.Utils.Utils.myLog;

public class MyTimer implements MyTimerInterface{
    private static MyTimer myTimerInstance;

    private Handler handler;
    private ArrayList<MyTimerObserver> mObservers;

    private long timerInterval = 60*1000; // Keeping it 1 day, 1 min now

    public static MyTimer getInstance() {
        if (myTimerInstance == null) {
            myTimerInstance = new MyTimer();
        }
        return myTimerInstance;
    }

    /**
     * Initializes timer
     */
    private MyTimer(){
        mObservers = new ArrayList<>();
        handler = new Handler();
        handler.post(runnableCode);
    }

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            myLog("timer ran");
            notifyObservers();
            handler.postDelayed(runnableCode, timerInterval);
        }
    };

    //interface methods
    @Override
    public void registerObserver(MyTimerObserver observer) {
        myLog("Timer Observer Added: "+observer);
        if(!mObservers.contains(observer)) {
            mObservers.add(observer);
        }
    }


    @Override
    public void removeObserver(MyTimerObserver observer) {
        if(mObservers.contains(observer)) {
            mObservers.remove(observer);
        }
    }

    @Override
    public void notifyObservers() {
        for (MyTimerObserver observer: mObservers) {
            observer.timerUpdated();
        }
    }

}
