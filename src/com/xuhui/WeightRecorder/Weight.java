package com.xuhui.WeightRecorder;

/**
 * Created by zhaox_000 on 2016-07-05.
 */
public class Weight {
    public String time;
    public double weight;
    public String ps;

    public Weight() {

    }

    public Weight(String time, double weight, String ps) {
        this.time = time;
        this.weight = weight;
        this.ps = ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getPs() {
        return ps;
    }

    public String getTime() {
        return time;
    }

    public double getWeight() {
        return weight;
    }

}
