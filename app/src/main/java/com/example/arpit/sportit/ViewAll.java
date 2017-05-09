package com.example.arpit.sportit;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class ViewAll extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        final ArrayList<Event> events = new ArrayList<Event>();

        events.add(new Event("Football Match", "Uni Ground1", "10 May 2017", "09:30 AM"));
        events.add(new Event("Football Match2", "Uni Ground1", "10 May 2017", "09:30 AM"));
        events.add(new Event("Cricket Match3", "Uni Ground1", "10 May 2017", "09:30 AM"));
        events.add(new Event("Rugby Match4", "Uni Ground1", "10 May 2017", "09:30 AM"));
        events.add(new Event("Football Match3", "Uni Ground1", "10 May 2017", "09:30 AM"));
        events.add(new Event("Football Match4", "Uni Ground1", "10 May 2017", "09:30 AM"));

        EventAdaptor adaptor = new EventAdaptor(this, events);

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adaptor);
    }
}
