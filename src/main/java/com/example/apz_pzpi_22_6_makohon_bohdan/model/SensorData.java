package com.example.apz_pzpi_22_6_makohon_bohdan.model;

public class SensorData {
    private Long id;
    private Sensor sensor;
    private Double value;
    private String timestamp; // Keep as String for parsing

    // Empty constructor for JSON parser
    public SensorData() {}

    public Long getId() { return id; }
    public Sensor getSensor() { return sensor; }
    public Double getValue() { return value; }
    public String getTimestamp() { return timestamp; }

    public void setId(Long id) { this.id = id; }
    public void setSensor(Sensor sensor) { this.sensor = sensor; }
    public void setValue(Double value) { this.value = value; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}

