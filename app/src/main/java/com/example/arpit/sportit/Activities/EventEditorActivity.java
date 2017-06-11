package com.example.arpit.sportit.Activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Console;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static android.R.attr.data;
import static android.R.attr.y;
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
    private java.util.Calendar cal;
    int eventYear, eventMonth, eventDay;
    int hour;
    int min;
    private String[] localDateTime;
    int PLACE_PICKER_REQUEST = 1;
    private static String timeZoneID;
    private View eventType;
    private View eventSpinner;
    private EditText eventTypeEditText;
    private String sport;
    private Spinner spinner;
    private int imageID;

    /** Boolean flag that keeps track of whether the event has been edited (true) or not (false) */
    private boolean eventHasChanged = false;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            eventHasChanged = true;
            return false;
        }
    };

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing event.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!eventHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_editor);

        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = firebaseDatabase.getReference();

        cal = java.util.Calendar.getInstance();
        timeZoneID = cal.getTimeZone().getID();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cal.set(java.util.Calendar.YEAR, year);
                eventYear = year;
                cal.set(java.util.Calendar.MONTH,month);
                eventMonth = month;
                cal.set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth);
                eventDay = dayOfMonth;
                update();
            }
        };

        eventNameEditText = (EditText) findViewById(R.id.edit_event_name);
        eventDateEditText = (EditText) findViewById(R.id.edit_event_date);
        eventTimeEditText = (EditText) findViewById(R.id.edit_event_time);
        playersAttendingEditText = (EditText) findViewById(R.id.edit_players_attending);
        playersRequiredEditText = (EditText) findViewById(R.id.edit_players_required);
        eventTypeEditText = (EditText) findViewById(R.id.edit_event_type);
        placePickerButton = (Button) findViewById(R.id.place_picker);
        spinner = (Spinner) findViewById(R.id.sport_spinner);
        eventType = findViewById(R.id.event_type);
        eventSpinner = findViewById(R.id.event_spinner);

        eventNameEditText.setOnTouchListener(touchListener);
        eventDateEditText.setOnTouchListener(touchListener);
        eventTimeEditText.setOnTouchListener(touchListener);
        playersRequiredEditText.setOnTouchListener(touchListener);
        spinner.setOnTouchListener(touchListener);

        playersAttendingEditText.setEnabled(false);

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);

        eventDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(EventEditorActivity.this, date, cal.get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH), cal.get(java.util.Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        eventTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(EventEditorActivity.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        boolean isPM = (hourOfDay >= 12);
                        hour = hourOfDay;
                        min = minute;

                        eventTimeEditText.setText(String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM"));
                    }
                }, hour, min, false);
                timePickerDialog.show();
            }
        });

        // Create an ArrayAdapter using the string array and a default spinner layout

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sports_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {      //https://developer.android.com/guide/topics/ui/controls/spinner.html
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item
                sport = parent.getItemAtPosition(position).toString();
                Log.v("sport selected","sport is "+ sport);
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        Intent intent = getIntent();
        previousActivity = intent.getStringExtra("Caller Method");
        eventID = intent.getStringExtra("EventID");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    e = dataSnapshot.getValue(Event.class);
                    eventNameEditText.setText(e.getEventName());
                    playersRequiredEditText.setText("" + e.getPlayersRequired());
                    eventTypeEditText.setText(e.getEventType());
                    spinner.setSelection(adapter.getPosition(e.getEventType()));
                    localDateTime = utcToLocal(e.getDateTime());
                    eventDateEditText.setText(localDateTime[0]);
                    eventTimeEditText.setText(localDateTime[1]);
                    location = e.getPlace();
                    loc = location.split("[|]");
                    placePickerButton.setText(loc[0]);
                    lat = Double.parseDouble(loc[1]);
                    lon = Double.parseDouble(loc[2]);
                    playersAttendingEditText.setText("" + e.getPlayersAttending());
                    if (e.getPlayersRequired() == e.getPlayersAttending() &&
                            previousActivity.contentEquals("view all events")) {
                        button1.setVisibility(View.GONE);
                    }
                    if (e.getIsCancelled() &&
                            previousActivity.contentEquals("event details attending")){
                        button2.setVisibility(View.GONE);
                    }
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
            eventSpinner.setVisibility(View.VISIBLE);
            eventType.setVisibility(View.GONE);
            playersAttendingEditText.setVisibility(View.GONE);
            findViewById(R.id.label_playersAttending).setVisibility(View.GONE);
            enableEditing();
            invalidateOptionsMenu();
        }
        else if (previousActivity.contentEquals("event details")){
            setTitle("Event Details");
            button1.setVisibility(View.GONE);
            button2.setVisibility(View.VISIBLE);
            eventSpinner.setVisibility(View.GONE);
            eventType.setVisibility(View.VISIBLE);
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
            eventSpinner.setVisibility(View.GONE);
            eventType.setVisibility(View.VISIBLE);
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
            eventSpinner.setVisibility(View.GONE);
            eventType.setVisibility(View.VISIBLE);
            findViewById(R.id.label_playersAttending).setVisibility(View.VISIBLE);
            disableEditing();
            invalidateOptionsMenu();
        }
        else if (previousActivity.contentEquals("edit event")){
            setTitle("Edit Event");
            button1.setText("Save");
            button2.setVisibility(View.GONE);
            button1.setVisibility(View.VISIBLE);
            eventSpinner.setVisibility(View.VISIBLE);
            eventType.setVisibility(View.GONE);
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
                        finish();
                    }
                }
                else if (previousActivity.contentEquals("event add") ||
                        previousActivity.contentEquals("edit event")){
                    saveEventData();
                }
                else if (previousActivity.contentEquals("event details attending")){
                    leaveEvent(e.getPlayersAttending());
                    finish();
                }
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
                return true;
            case R.id.action_edit:
                startEditEvent();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveEventData(){
        //String userID = firebaseAuth.getCurrentUser().getUid();
        String userID = "RMBIva5WdIZyE7zcTbcQ8SPAGlZ2";
        if (!userID.isEmpty()) {
            boolean validData = true;
            String eventName = eventNameEditText.getText().toString().trim();
            String eventPlace = location;
            int playersRequired = -1;
            try {
                playersRequired = Integer.parseInt(playersRequiredEditText.getText().toString().trim());
                if(!isValidPlayersRequired(playersRequired)){
                    validData = false;
                    //Toast.makeText(this, "Number of players required cannot be greater than 50", Toast.LENGTH_SHORT).show();
                    playersRequiredEditText.setError("Number of players required cannot be greater than 50");
                }
            }
            catch (NumberFormatException e){
                validData = false;
                Log.v("input players","invalid input");
                playersRequiredEditText.setError("Please enter numeric values");
            }
            imageID = findImageID();

            Calendar newCal = Calendar.getInstance();
            newCal.set(eventYear,eventMonth,eventDay,hour,min);
            Date date = newCal.getTime();

            if (!isValidEventName(eventName)){
                validData = false;
                eventNameEditText.setError("Event name cannot be blank and should be less than 40 characters");
            }
            if (!isValidLocation(location)){
                validData = false;
                Toast.makeText(this, "Please select a valid location", Toast.LENGTH_SHORT).show();
            }
            if (!isValidDateTime(date)){
                validData = false;
                Toast.makeText(this, "Please select a future date and time.", Toast.LENGTH_SHORT).show();
            }


            if (validData) {
                String eventDateTime = localToUTC(date);

                if ((previousActivity.contentEquals("event add"))) {
                    Event event = new Event(eventName, eventPlace, eventDateTime, userID, sport, playersRequired, imageID);
                    databaseReference.child("events").push().setValue(event);
                } else if ((previousActivity.contentEquals("edit event"))) {

                    Map<String, Object> update = new HashMap<>();

                    update.put("events/" + eventID + "/eventName", eventName);
                    update.put("events/" + eventID + "/eventType", sport);
                    update.put("events/" + eventID + "/place", location);
                    update.put("events/" + eventID + "/dateTime", eventDateTime);
                    update.put("events/" + eventID + "/imageResourceId", imageID);
                    databaseReference.updateChildren(update);
                }
                finish();
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

    private void deleteEvent(){
        Map<String,Object> update = new HashMap<>();
        update.put("events/"+eventID+"/isCancelled",true);
        databaseReference.updateChildren(update);
        Log.v("players ", "players attending value " + e.getPlayersAttending());
        if (e.getPlayersAttending() == 0){
            databaseReference.child("events").child(eventID).removeValue();
        }
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the event.
                deleteEvent();
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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

    private void update(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        eventDateEditText.setText(sdf.format(cal.getTime()));
    }

    private static String localToUTC(Date date) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    private String[] utcToLocal(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");
        String[] localDateTime = new String[2];
        try {
            Date convertedDate = sdf.parse(date);

            Date local = new Date(convertedDate.getTime() + TimeZone.getTimeZone(timeZoneID).getOffset(convertedDate.getTime()));
            cal.setTime(local);
            eventYear = cal.get(Calendar.YEAR);
            eventMonth = cal.get(Calendar.MONTH);
            eventDay = cal.get(Calendar.DAY_OF_MONTH);
            hour = cal.get(Calendar.HOUR_OF_DAY);
            min = cal.get(Calendar.MINUTE);
            SimpleDateFormat sdfLocalDate = new SimpleDateFormat("dd MMM yyyy");
            localDateTime[0] = sdfLocalDate.format(local);
            SimpleDateFormat sdfLocalTime = new SimpleDateFormat("h:mm a");
            localDateTime[1] = sdfLocalTime.format(local);
            Log.v("local date","Local date : "+localDateTime[0]);
            Log.v("local time","Local Time : "+localDateTime[1]);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return localDateTime;
    }

    private int findImageID(){
        switch (sport){
            case "Cricket":
                return R.drawable.cricket;
            case "Football":
                return R.drawable.football;
            case "Tennis":
                return R.drawable.tennis;
            case "Badminton":
                return R.drawable.badminton;
            case "Rugby":
                return R.drawable.rugby;
            case "Basketball":
                return R.drawable.basketball;
            case "Volleyball":
                return R.drawable.volleyball;
            case "Baseball":
                return R.drawable.baseball;
        }
        return -1;
    }

    private boolean isValidEventName(String name){
        if (name != null && name.length() > 0 && name.length() <= 40){
            return true;
        }
        return false;
    }

    private boolean isValidLocation(String location){
        if (location != null && location.length() >0){
            return true;
        }
        return false;
    }

    private boolean isValidPlayersRequired(int players){
        if (players > 0 && players <= 50){
            return true;
        }
        return false;
    }

    private boolean isValidDateTime(Date date){
        //should be future date and time
        Date currentDateTime = Calendar.getInstance().getTime();
        Log.v("dateTime","ct " +currentDateTime);
        Log.v("dateTime","selected " +date);
        Log.v("value",""+ date.after(currentDateTime));
        if (date.after(currentDateTime)){
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(valueEventListener);
    }
}
