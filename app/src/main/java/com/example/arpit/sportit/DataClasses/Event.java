package com.example.arpit.sportit.DataClasses;

import org.json.JSONObject;

/**
 * Created by Arpit on 09-05-2017.
 */

public class Event {

    public Event(){}

    public Event(String eventName, String place, String date, String time, String createdBy){
        this.eventName = eventName;
        this.place = place;
        this.date = date;
        this.time = time;
        this.createdBy = createdBy;
    };

    private String eventName;

    private String place;

    private String date;

    private String time;

    private String eventID;

    private String createdBy;

    //private JSONObject usersAttending;

    public String getEventName(){
        return eventName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getDate() {
        return date;
    }

    public String getPlace() {
        return place;
    }

    public String getTime() {
        return time;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    //public JSONObject getUsersAttending(){return usersAttending;}
}
