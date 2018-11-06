package com.example.nicosetiawan.runfit.Database;

import java.io.Serializable;



public class Runit implements Serializable {

    private String runId;
    private String runTitle;
    private String runDate;
    private String runStartTime;
    private String runStopTime;

    private String runDistance;
    private String runDuration;
    private String timestamp;
    private String runPos;

    public Runit(){

    }

    public Runit(String runId, String runTitle, String runDate,String runStartTime, String runStopTime, String runDistance, String runDuration, String Posisi) {
        this.runId = runId;
        this.runTitle = runTitle;
        this.runDate = runDate;
        this.runStartTime = runStartTime;
        this.runStopTime = runStopTime;
        this.runDistance= runDistance;
        this.runDuration = runDuration;
        this.runPos = Posisi;
    }



    public String getRunId() {
        return runId;
    }

    public String getRunTitle() {
        return runTitle;
    }

    public String getRunDate() {
        return runDate;
    }

    public String getRunDistance(){
        return runDistance;
    }
    public String getRunDuration(){
        return runDuration;
    }
    public String getRunStartTime(){
        return runStartTime;
    }
    public String getRunStopTime(){
        return runStopTime;
    }

    public String getRunPos(){
        return this.runPos;
    }
}
