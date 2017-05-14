package com.example.arpit.sportit.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.arpit.sportit.DataClasses.Event;
import com.example.arpit.sportit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class EventEditorActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private EditText eventNameEditText;
    private EditText eventDateEditText;
    private EditText eventTimeEditText;
    private EditText eventPlaceEditText;
    private String previousActivity;
    private String eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_editor);

        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = firebaseDatabase.getReference();

        eventNameEditText = (EditText) findViewById(R.id.edit_event_name);
        eventDateEditText = (EditText) findViewById(R.id.edit_event_date);
        eventPlaceEditText = (EditText) findViewById(R.id.edit_event_place);
        eventTimeEditText = (EditText) findViewById(R.id.edit_event_time);

        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);

        Intent intent = getIntent();
        previousActivity = intent.getStringExtra("Caller Method");
        eventID = intent.getStringExtra("EventID");

        if (previousActivity.contentEquals("event add")){
            setTitle("Add Event");
            button1.setText("Save");
            button2.setVisibility(View.GONE);
            button1.setVisibility(View.VISIBLE);
        }
        else if (previousActivity.contentEquals("event details")){
            setTitle("Event Details");
            button1.setVisibility(View.GONE);
            button2.setVisibility(View.VISIBLE);
        }
        else if  (previousActivity.contentEquals("event details attending")){
            setTitle("Event Details");
            button1.setText("Withdraw");
            button2.setVisibility(View.VISIBLE);
            button1.setVisibility(View.VISIBLE);
        }
        else if (previousActivity.contentEquals("view all events")){
            setTitle("Event Details");
            button1.setText("Join");
            button2.setVisibility(View.VISIBLE);
            button1.setVisibility(View.VISIBLE);
        }

        if (eventID != null){
            databaseReference.child("events").child(eventID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Event e = dataSnapshot.getValue(Event.class);
                    eventNameEditText.setText(e.getEventName());
                    eventNameEditText.setEnabled(false);
                    eventDateEditText.setText(e.getDate());
                    eventDateEditText.setEnabled(false);
                    eventTimeEditText.setText(e.getTime());
                    eventTimeEditText.setEnabled(false);
                    eventPlaceEditText.setText(e.getPlace());
                    eventPlaceEditText.setEnabled(false);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((previousActivity.contentEquals("view all events"))){
                    joinEvent();
                }
                else if (previousActivity.contentEquals("event add")){
                    saveEventData();
                }
                else if (previousActivity.contentEquals("event details attending")){
                    leaveEvent();
                }
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_save:
                // Do nothing for now
                saveEventData();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveEventData(){
        //String userID = firebaseAuth.getCurrentUser().getUid();
        String userID = "otg6kfHTjgVKJ09iZgPdbx3roAM2";
        if (!userID.isEmpty()) {
            String eventName = eventNameEditText.getText().toString().trim();
            String eventDate = eventDateEditText.getText().toString().trim();
            String eventPlace = eventPlaceEditText.getText().toString().trim();
            String eventTime = eventTimeEditText.getText().toString().trim();

            Event event = new Event(eventName, eventPlace, eventDate, eventTime,userID);

            databaseReference.child("events").push().setValue(event);
        }
        else{
            Log.v("Event entry", "user id not present");
        }
    }

    public void joinEvent(){

        Map<String,Object> update = new HashMap<>();
        update.put("events/"+eventID+"/usersAttending/"+"otg6kfHTjgVKJ09iZgPdbx3roAM2",true);
        update.put("users/"+"otg6kfHTjgVKJ09iZgPdbx3roAM2"+"/eventsAttending/"+eventID,true);

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
        dbref.updateChildren(update);
    }

    public void leaveEvent(){
        databaseReference.child("events").child(eventID).child("usersAttending").child("otg6kfHTjgVKJ09iZgPdbx3roAM2").removeValue();
        databaseReference.child("users").child("otg6kfHTjgVKJ09iZgPdbx3roAM2").child("eventsAttending").child(eventID).removeValue();
    }

}
