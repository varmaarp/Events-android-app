package com.example.arpit.sportit.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.arpit.sportit.Activities.EventEditorActivity;
import com.example.arpit.sportit.Adapters.EventAdaptor;
import com.example.arpit.sportit.DataClasses.Event;
import com.example.arpit.sportit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.arpit.sportit.R.id.fab;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewAllFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReferenceEvents;
    private DatabaseReference databaseReferenceUsers;
    private EventAdaptor myEventsAdaptor;
    private ValueEventListener valueEventListener;
    private ValueEventListener valueEventListener2;
    private String userID;


    public ViewAllFragment() {
        // Required empty public constructor
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.events_list,container,false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceEvents  = firebaseDatabase.getReference().child("events");
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        //databaseReferenceUsers = firebaseDatabase.getReference().child("users").child("RMBIva5WdIZyE7zcTbcQ8SPAGlZ2").child("eventsAttending");
        databaseReferenceUsers = firebaseDatabase.getReference().child("users").child(userID).child("eventsAttending");

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        final TextView emptyStateTextView = (TextView) rootView.findViewById(R.id.empty_view);

        final ArrayList<String> eventIds = new ArrayList<>();

        final ArrayList<Event> events = new ArrayList<Event>();
        myEventsAdaptor = new EventAdaptor(getActivity(), events);

        final View loadingIndicator = rootView.findViewById(R.id.loading_indicator);

        ListView listView = (ListView) rootView.findViewById(R.id.list);
        listView.setAdapter(myEventsAdaptor);

        valueEventListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventIds.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Log.v("event id", "ids from users : "+postSnapshot.getKey());
                    eventIds.add(postSnapshot.getKey());
                }

                databaseReferenceEvents.addValueEventListener(valueEventListener);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myEventsAdaptor.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Event e = postSnapshot.getValue(Event.class);
                    //if (!e.getCreatedBy().contentEquals("RMBIva5WdIZyE7zcTbcQ8SPAGlZ2") &&
                    if (!e.getCreatedBy().contentEquals(userID) &&
                            !eventIds.contains(postSnapshot.getKey())
                            && !e.getIsCancelled()) {
                            e.setEventID(postSnapshot.getKey());
                            events.add(e);
                            myEventsAdaptor.notifyDataSetChanged();
                            loadingIndicator.setVisibility(View.GONE);
                            emptyStateTextView.setVisibility(View.GONE);
                    }
                }
                if (myEventsAdaptor.isEmpty()){
                    loadingIndicator.setVisibility(View.GONE);
                    emptyStateTextView.setText("No Events Available");
                    emptyStateTextView.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReferenceUsers.addValueEventListener(valueEventListener2);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event e = events.get(position);
                Intent intent = new Intent(getContext(), EventEditorActivity.class);
                intent.putExtra("EventID",e.getEventID());
                intent.putExtra("Caller Method","view all events");
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu,menu);
        MenuItem item = menu.findItem(R.id.search);

        SearchView searchView = (SearchView)item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myEventsAdaptor.getFilter().filter(newText);
                return false;
            }
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        myEventsAdaptor.clear();
        databaseReferenceEvents.removeEventListener(valueEventListener);
        databaseReferenceEvents.removeEventListener(valueEventListener2);
    }


}
