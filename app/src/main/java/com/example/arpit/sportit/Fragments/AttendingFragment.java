package com.example.arpit.sportit.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.arpit.sportit.Activities.EventEditorActivity;
import com.example.arpit.sportit.DataClasses.Event;
import com.example.arpit.sportit.Adapters.EventAdaptor;
import com.example.arpit.sportit.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttendingFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private EventAdaptor myEventsAdaptor;
    private ChildEventListener childEventListener;

    public AttendingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.events_list,container,false);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        firebaseDatabase = FirebaseDatabase.getInstance();

        final ArrayList<Event> events = new ArrayList<Event>();

        myEventsAdaptor = new EventAdaptor(getActivity(), events);

        final View loadingIndicator = rootView.findViewById(R.id.loading_indicator);

        ListView listView = (ListView) rootView.findViewById(R.id.list);
        listView.setAdapter(myEventsAdaptor);

        databaseReference = firebaseDatabase.getReference().child("users").child("-Kjxy4VEdiAQ5UbpeCNT").child("eventsAttending");

        final DatabaseReference eventsReference = firebaseDatabase.getReference().child("events");

        childEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                eventsReference.child("-"+dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Event e = dataSnapshot.getValue(Event.class);
                        e.setEventID(dataSnapshot.getKey());
                        events.add(e);
                        myEventsAdaptor.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
                loadingIndicator.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };

        databaseReference.addChildEventListener(childEventListener);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event e = events.get(position);
                Intent intent = new Intent(getContext(), EventEditorActivity.class);
                intent.putExtra("EventID",e.getEventID());
                intent.putExtra("Caller Method","event details attending");
                startActivity(intent);
            }
        });

        return rootView;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myEventsAdaptor.clear();
        databaseReference.removeEventListener(childEventListener);
    }


}