package com.example.arpit.sportit.DataClasses;

/**
 * Created by Arpit on 12-05-2017.
 */

public class User {

    private String userName;
    private String userID;

    public User(){}

    public User(String userID, String userName){
        this.userID = userID;
        this.userName = userName;
    }

    public String getUserName(){
        return userName;
    }

    public String getUserID(){
        return userID;
    }
}
