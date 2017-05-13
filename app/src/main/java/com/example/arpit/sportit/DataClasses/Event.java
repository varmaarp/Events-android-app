package com.example.arpit.sportit.DataClasses;

/**
 * Created by Arpit on 09-05-2017.
 */

public class Event {

    public Event(){}

    public Event(String eventName, String place, String date, String time){
        this.eventName = eventName;
        this.place = place;
        this.date = date;
        this.time = time;
    };

    private String eventName;

    private String place;

    private String date;

    private String time;

    //private String createdBy;

    public String getEventName(){
        return eventName;
    }

    /**
    public String getCreatedBy() {
        return createdBy;
    }
     **/

    public String getDate() {
        return date;
    }

    public String getPlace() {
        return place;
    }

    public String getTime() {
        return time;
    }
}
