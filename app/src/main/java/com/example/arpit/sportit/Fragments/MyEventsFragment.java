package com.example.arpit.sportit.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.arpit.sportit.DataClasses.Event;
import com.example.arpit.sportit.Adapters.EventAdaptor;
import com.example.arpit.sportit.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyEventsFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private EventAdaptor myEventsAdaptor;
    private ValueEventListener valueEventListener;

    public MyEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.events_list,container,false);

        firebaseDatabase = FirebaseDatabase.getInstance();

        final ArrayList<Event> events = new ArrayList<Event>();
        myEventsAdaptor = new EventAdaptor(getActivity(), events);

        ListView listView = (ListView) rootView.findViewById(R.id.list);
        listView.setAdapter(myEventsAdaptor);


        databaseReference = firebaseDatabase.getReference().child("events");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Event e = postSnapshot.getValue(Event.class);
                    events.add(e);
                    myEventsAdaptor.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        };

        databaseReference.orderByChild("createdBy").equalTo("Kjxy4VEdiAQ5UbpeCNT").addValueEventListener(valueEventListener);

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myEventsAdaptor.clear();
        databaseReference.removeEventListener(valueEventListener);
    }


}
