package com.example.hw1_cargame.msp_objects;

public class ScoreRecord {
    private int score;
    private String date;
    private double lat = 0.0;
    private double lon = 0.0;

    public ScoreRecord() { }

    public int getScore() {
        return score;
    }

    public ScoreRecord setScore(int score) {
        this.score = score;
        return this;
    }

    public String getDate() {
        return date;
    }

    public ScoreRecord setDate(String date) {
        this.date = date;
        return this;
    }

    public double getLat() {
        return lat;
    }

    public ScoreRecord setLat(double lat) {
        this.lat = lat;
        return this;
    }

    public double getLon() {
        return lon;
    }

    public ScoreRecord setLon(double lon) {
        this.lon = lon;
        return this;
    }

    @Override
    public String toString() {
        return "ScoreRecord{" +
                "score=" + score +
                ", date='" + date + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
