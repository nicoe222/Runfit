package com.example.nicosetiawan.runfit.Database;

/**
 * Created by rusmana on 10/09/2018.
 */

public class RunPosition {
    private String posId;
    private String runId;
    private String runLat;
    private String runLong;
    private String timestamp;

    public RunPosition(){

    }

    public RunPosition(String posId,String runId, String runLat, String runLong){
        this.posId = posId;
        this.runId = runId;
        this.runLat = runLat;
        this.runLong = runLong;
    }

    public void setRunLat(String lat){
        this.runLat = lat;
    }
    public void setRunLong(String lng){
        this.runLong = lng;
    }

    public String getPosId(){
        return this.posId;
    }
    public String getRunId(){
        return this.runId;
    }
    public String getRunLat(){
        return this.runLat;
    }
    public String getRunLong(){
        return this.runLong;
    }

}
