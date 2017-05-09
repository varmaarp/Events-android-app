package com.example.arpit.sportit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.widget.ListView;

import java.util.ArrayList;

public class MyEvents extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        final ArrayList<Event> events = new ArrayList<Event>();

        events.add(new Event("Football Match", "Uni Ground1", "10 May 2017", "09:30 AM"));
        events.add(new Event("Football Match2", "Uni Ground1", "10 May 2017", "09:30 AM"));
        events.add(new Event("Football Match3", "Uni Ground1", "10 May 2017", "09:30 AM"));
        events.add(new Event("Football Match4", "Uni Ground1", "10 May 2017", "09:30 AM"));

        EventAdaptor adaptor = new EventAdaptor(this, events);

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adaptor);

    }
}
