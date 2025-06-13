package com.example.apz_pzpi_22_6_makohon_bohdan.model;

public class Sensor {


    private int id;
    private String location;
    private String sensorType;

    public Sensor(String location, String sensorType) {
        this.location = location;
        this.sensorType = sensorType;
    }

    public Sensor() {
    }

    public Sensor(int id, String location, String sensorType) {
        this.id = id;
        this.location = location;
        this.sensorType = sensorType;
    }

    public String getLocation() { return location; }
    public String getSensorType() { return sensorType; }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
