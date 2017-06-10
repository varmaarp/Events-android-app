package com.example.arpit.sportit.DataClasses;

import org.json.JSONObject;

/**
 * Created by Arpit on 09-05-2017.
 */

public class Event {

    public Event(){}

    public Event(String eventName, String place, String dateTime, String createdBy, String eventType, int playersRequired, int imageResourceId){
        this.eventName = eventName;
        this.place = place;
        this.dateTime = dateTime;
        this.createdBy = createdBy;
        this.playersRequired = playersRequired;
        this.eventType = eventType;
        this.imageResourceId = imageResourceId;
        this.playersAttending = 0;
        this.isCancelled = false;
    };

    private String eventName;

    private String place;

    private String eventID;

    private String eventType;

    private String createdBy;

    private String dateTime;

    private int playersRequired;

    private int playersAttending;

    private boolean isCancelled;

    private int imageResourceId;

    public String getEventName(){
        return eventName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getEventType() { return eventType; }

    public String getPlace() {
        return place;
    }

    public String getEventID() {
        return eventID;
    }

    public String getDateTime() { return dateTime; }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public int getPlayersRequired(){
        return playersRequired;
    }

    public int getPlayersAttending(){
        return playersAttending;
    }

    public boolean getIsCancelled(){ return isCancelled; }

    public int getImageResourceId() { return imageResourceId; }

}
