package com.example.arpit.sportit.Activities;

import android.content.Intent;
import android.net.Uri;
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
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static java.security.AccessController.getContext;

public class EventEditorActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private FirebaseAuth firebaseAuth;
    private EditText eventNameEditText;
    private EditText eventDateEditText;
    private EditText eventTimeEditText;
    private EditText playersRequiredEditText;
    private EditText playersAttendingEditText;
    private Button placePickerButton;
    private String previousActivity;
    private Button button1;
    private Button button2;
    private String eventID;
    private String location;
    private Double lat;
    private Double lon;
    private Event e;
    private String[] loc;
    int PLACE_PICKER_REQUEST = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_editor);

        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = firebaseDatabase.getReference();

        eventNameEditText = (EditText) findViewById(R.id.edit_event_name);
        eventDateEditText = (EditText) findViewById(R.id.edit_event_date);
        eventTimeEditText = (EditText) findViewById(R.id.edit_event_time);
        playersAttendingEditText = (EditText) findViewById(R.id.edit_players_attending);
        playersRequiredEditText = (EditText) findViewById(R.id.edit_players_required);
        placePickerButton = (Button) findViewById(R.id.place_picker);
        playersAttendingEditText.setEnabled(false);

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);

        Intent intent = getIntent();
        previousActivity = intent.getStringExtra("Caller Method");
        eventID = intent.getStringExtra("EventID");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                e = dataSnapshot.getValue(Event.class);
                eventNameEditText.setText(e.getEventName());
                eventDateEditText.setText(e.getDate());
                eventTimeEditText.setText(e.getTime());
                playersRequiredEditText.setText(""+e.getPlayersRequired());
                location = e.getPlace();
                loc = location.split("[|]");
                placePickerButton.setText(loc[0]);
                lat = Double.parseDouble(loc[1]);
                lon = Double.parseDouble(loc[2]);
                playersAttendingEditText.setText(""+e.getPlayersAttending());
                if (e.getPlayersRequired() == e.getPlayersAttending() &&
                        previousActivity.contentEquals("view all events")) {
                    button1.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("Error","Database Error");
            }
        };

        if (eventID != null){
            databaseReference.child("events").child(eventID).addValueEventListener(valueEventListener);
        }

        if (previousActivity.contentEquals("event add")){
            setTitle("Add Event");
            button1.setText("Save");
            button2.setVisibility(View.GONE);
            button1.setVisibility(View.VISIBLE);
            playersAttendingEditText.setVisibility(View.GONE);
            findViewById(R.id.label_playersAttending).setVisibility(View.GONE);
            enableEditing();
            invalidateOptionsMenu();
        }
        else if (previousActivity.contentEquals("event details")){
            setTitle("Event Details");
            button1.setVisibility(View.GONE);
            button2.setVisibility(View.VISIBLE);
            playersAttendingEditText.setVisibility(View.VISIBLE);
            findViewById(R.id.label_playersAttending).setVisibility(View.VISIBLE);
            disableEditing();
            invalidateOptionsMenu();
        }
        else if  (previousActivity.contentEquals("event details attending")){
            setTitle("Event Details");
            button1.setText("Withdraw");
            button2.setVisibility(View.VISIBLE);
            button1.setVisibility(View.VISIBLE);
            playersAttendingEditText.setVisibility(View.VISIBLE);
            findViewById(R.id.label_playersAttending).setVisibility(View.VISIBLE);
            disableEditing();
            invalidateOptionsMenu();
        }
        else if (previousActivity.contentEquals("view all events")){
            setTitle("Event Details");
            button1.setText("Join");
            button2.setVisibility(View.VISIBLE);
            playersAttendingEditText.setVisibility(View.VISIBLE);
            findViewById(R.id.label_playersAttending).setVisibility(View.VISIBLE);
            disableEditing();
            invalidateOptionsMenu();
        }
        else if (previousActivity.contentEquals("edit event")){
            setTitle("Edit Event");
            button1.setText("Save");
            button2.setVisibility(View.GONE);
            button1.setVisibility(View.VISIBLE);
            playersAttendingEditText.setVisibility(View.GONE);
            findViewById(R.id.label_playersAttending).setVisibility(View.GONE);
            enableEditing();
            invalidateOptionsMenu();
        }


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((previousActivity.contentEquals("view all events"))){
                    if (e.getPlayersRequired() > e.getPlayersAttending()) {
                        joinEvent(e.getPlayersAttending());
                    }
                }
                else if (previousActivity.contentEquals("event add") ||
                        previousActivity.contentEquals("edit event")){
                    saveEventData();
                }
                else if (previousActivity.contentEquals("event details attending")){
                    leaveEvent(e.getPlayersAttending());
                }
                e = null;
                finish();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                sendIntent.setType("text/plain");
                //startActivity(sendIntent);
                startActivity(Intent.createChooser(sendIntent,"Select App"));
            }
        });

        placePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (previousActivity.contentEquals("event add") ||
                        previousActivity.contentEquals("edit event")) {
                    startPlacePickerActivity();
                }
                else{
                    startMapsActivity();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST){
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String placeName = place.getName().toString();
                LatLng latLng = place.getLatLng();
                Double l1 = latLng.latitude;
                Double l2 = latLng.longitude;
                location = placeName + "|" + l1.toString() + "|" + l2.toString();
                placePickerButton.setText(placeName);
            }
        }
    }

    private void startPlacePickerActivity(){
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            Intent intent = builder.build(this);
            startActivityForResult(intent,PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e1) {
            e1.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e1) {
            e1.printStackTrace();
        }
    }

    private void startMapsActivity(){
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(%s)", lat,lon,lat,lon,loc[0]);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (previousActivity.contentEquals("event add")) {
            menu.findItem(R.id.action_delete).setVisible(false);
            menu.findItem(R.id.action_edit).setVisible(false);
        }
        else if (previousActivity.contentEquals("event details")){
            menu.findItem(R.id.action_save).setVisible(false);
        }
        else if (previousActivity.contentEquals("edit event")){
            menu.findItem(R.id.action_delete).setVisible(false);
            menu.findItem(R.id.action_edit).setVisible(false);
        }
        else{
            menu.findItem(R.id.action_delete).setVisible(false);
            menu.findItem(R.id.action_edit).setVisible(false);
            menu.findItem(R.id.action_save).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_save:
                saveEventData();
                finish();
                return true;
            case R.id.action_edit:
                startEditEvent();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveEventData(){
        //String userID = firebaseAuth.getCurrentUser().getUid();
        String userID = "RMBIva5WdIZyE7zcTbcQ8SPAGlZ2";
        if (!userID.isEmpty()) {
            String eventName = eventNameEditText.getText().toString().trim();
            String eventDate = eventDateEditText.getText().toString().trim();
            String eventPlace = location;
            String eventTime = eventTimeEditText.getText().toString().trim();
            int playersRequired = Integer.parseInt(playersRequiredEditText.getText().toString().trim());

            if ((previousActivity.contentEquals("event add"))) {
                Event event = new Event(eventName, eventPlace, eventDate, eventTime, userID, playersRequired);
                databaseReference.child("events").push().setValue(event);
            }
            else if ((previousActivity.contentEquals("edit event"))){
                Log.v("in edit","in edit " + location);

                Map<String,Object> update = new HashMap<>();
                update.put("events/"+eventID+"/date",eventDate);
                update.put("events/"+eventID+"/eventName",eventName);
                update.put("events/"+eventID+"/place",location);
                update.put("events/"+eventID+"/time",eventTime);
                databaseReference.updateChildren(update);

            }
        }
        else{
            Log.v("Event entry", "user id not present");
        }
    }

    private void joinEvent(int players){

        Map<String,Object> update = new HashMap<>();
        update.put("events/"+eventID+"/usersAttending/"+"RMBIva5WdIZyE7zcTbcQ8SPAGlZ2",true);
        update.put("events/"+eventID+"/playersAttending",players+1);
        update.put("users/"+"RMBIva5WdIZyE7zcTbcQ8SPAGlZ2"+"/eventsAttending/"+eventID,true);

        databaseReference.updateChildren(update);
    }

    private void leaveEvent(int players){
        Map<String,Object> update = new HashMap<>();
        update.put("events/"+eventID+"/playersAttending",players-1);
        databaseReference.updateChildren(update);
        databaseReference.child("events").child(eventID).child("usersAttending").child("RMBIva5WdIZyE7zcTbcQ8SPAGlZ2").removeValue();
        databaseReference.child("users").child("RMBIva5WdIZyE7zcTbcQ8SPAGlZ2").child("eventsAttending").child(eventID).removeValue();
    }


    private void startEditEvent(){
        Intent intent = new Intent(this, EventEditorActivity.class);
        Log.v("event id", "event id : "+ eventID);
        intent.putExtra("EventID", eventID);
        intent.putExtra("Caller Method","edit event");
        startActivity(intent);
    }

    private void enableEditing(){
        eventNameEditText.setEnabled(true);
        eventDateEditText.setEnabled(true);
        eventTimeEditText.setEnabled(true);
        playersRequiredEditText.setEnabled(true);
    }

    private void disableEditing(){
        eventNameEditText.setEnabled(false);
        eventDateEditText.setEnabled(false);
        eventTimeEditText.setEnabled(false);
        playersRequiredEditText.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.child("events").child(eventID).removeEventListener(valueEventListener);
    }
}
