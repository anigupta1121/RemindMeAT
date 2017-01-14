package com.placepickexample;

/**
 * Created by anirudh on 25/10/16.
 */
public class Vars {
    String place;
    String note;
    double x,y;
    String pushId;


    public Vars(String place,String note,double x,double y,String pushId)
    {

        this.pushId=pushId;
        this.note=note;
        this.place=place;
        this.x=x;
        this.y=y;
    }

}
