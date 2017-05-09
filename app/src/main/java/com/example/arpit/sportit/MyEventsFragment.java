package com.example.arpit.sportit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyEventsFragment extends Fragment {


    public MyEventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.events_list,container,false);

        final ArrayList<Event> events = new ArrayList<Event>();

        events.add(new Event("Football Match", "Uni Ground1", "10 May 2017", "09:30 AM"));
        events.add(new Event("Football Match2", "Uni Ground1", "10 May 2017", "09:30 AM"));
        events.add(new Event("Football Match3", "Uni Ground1", "10 May 2017", "09:30 AM"));
        events.add(new Event("Football Match4", "Uni Ground1", "10 May 2017", "09:30 AM"));

        EventAdaptor adaptor = new EventAdaptor(getActivity(), events);

        ListView listView = (ListView) rootView.findViewById(R.id.list);
        listView.setAdapter(adaptor);

        return rootView;
    }

}
