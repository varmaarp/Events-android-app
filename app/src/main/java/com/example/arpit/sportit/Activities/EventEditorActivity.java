package com.example.arpit.sportit.Activities;

import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EventEditorActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
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
        databaseReference = firebaseDatabase.getReference().child("events");

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

        if (eventID != null){
            databaseReference.child(eventID).addListenerForSingleValueEvent(new ValueEventListener() {
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
        String eventName = eventNameEditText.getText().toString().trim();
        String eventDate = eventDateEditText.getText().toString().trim();
        String eventPlace = eventPlaceEditText.getText().toString().trim();
        String eventTime = eventTimeEditText.getText().toString().trim();

        Event event = new Event(eventName,eventPlace,eventDate,eventTime);

        databaseReference.push().setValue(event);
    }
}
