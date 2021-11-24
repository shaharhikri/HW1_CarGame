package com.example.hw1_cargame.score_objects;

public class ScoreRecord {
    private int score;
    private String date;
    private double lat = 0.0;
    private double lon = 0.0;

    public ScoreRecord() { }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
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
