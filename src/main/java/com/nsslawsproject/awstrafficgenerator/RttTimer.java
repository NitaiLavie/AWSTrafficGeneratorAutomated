package com.nsslawsproject.awstrafficgenerator;

/**
 * Created by nitai on 12/05/17.
 */

public class RttTimer {
    /**
     * measures time passed between start and stop in milliseconds in milliseconds
     * when start measuring sets time to 0
     */
    private long mStartTime;
    private long mStopTime;
    private long mTime;
   
    public RttTimer(){
        mStartTime = 0;
        mStopTime = 0;
        mTime = 0;
    }

    public void start(){
        mTime = 0;
        mStartTime = System.nanoTime();
    }

    public void stop(){
        mStopTime = System.nanoTime();
        mTime = mStopTime - mStartTime;
    }

    public long getTime(){
        return mTime;
    }

    @Override
    public String toString(){
        if(mTime < 1e3 ){
            return Long.toString(mTime) + "ns";
        } else if (mTime < 1e6) {
            return Double.toString(mTime/1e3) + "μs";
        } else if (mTime < 1e9){
            return Double.toString((mTime-(mTime%1e3))/1e6) + "ms";
        } else {
            return Double.toString((mTime-(mTime%1e6))/1e9) + "s";
        }
    }
}
