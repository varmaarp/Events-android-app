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
import android.widget.TextView;

import com.example.arpit.sportit.Activities.EventEditorActivity;
import com.example.arpit.sportit.DataClasses.Event;
import com.example.arpit.sportit.Adapters.EventAdaptor;
import com.example.arpit.sportit.R;
import com.google.firebase.auth.FirebaseAuth;
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
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private EventAdaptor myEventsAdaptor;
    private ValueEventListener valueEventListener;

    public MyEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.events_list,container,false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        final ArrayList<Event> events = new ArrayList<Event>();
        myEventsAdaptor = new EventAdaptor(getActivity(), events);

        final TextView emptyStateTextView = (TextView) rootView.findViewById(R.id.empty_view);

        final View loadingIndicator = rootView.findViewById(R.id.loading_indicator);

        ListView listView = (ListView) rootView.findViewById(R.id.list);
        listView.setAdapter(myEventsAdaptor);


        databaseReference = firebaseDatabase.getReference().child("events");


        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myEventsAdaptor.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Event e = postSnapshot.getValue(Event.class);
                    e.setEventID(postSnapshot.getKey());
                    events.add(e);
                    myEventsAdaptor.notifyDataSetChanged();

                    loadingIndicator.setVisibility(View.GONE);
                }
                if (myEventsAdaptor.isEmpty()){
                    loadingIndicator.setVisibility(View.GONE);
                    emptyStateTextView.setText("No Events Available. Create an Event");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        };

        databaseReference.orderByChild("createdBy").equalTo("otg6kfHTjgVKJ09iZgPdbx3roAM2").addValueEventListener(valueEventListener);
        //databaseReference.orderByChild("createdBy").equalTo(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(valueEventListener);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),EventEditorActivity.class);
                intent.putExtra("Caller Method","event add");
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event e = events.get(position);
                Intent intent = new Intent(getContext(), EventEditorActivity.class);
                intent.putExtra("EventID",e.getEventID());
                intent.putExtra("Caller Method","event details");
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myEventsAdaptor.clear();
        databaseReference.removeEventListener(valueEventListener);
    }


}
