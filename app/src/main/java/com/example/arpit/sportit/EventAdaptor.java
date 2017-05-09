package com.example.arpit.sportit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.ArrayList;

/**
 * Created by Arpit on 09-05-2017.
 */

public class EventAdaptor extends ArrayAdapter<Event> {

    public EventAdaptor(Context context, ArrayList<Event> events){
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        Event currentEvent = getItem(position);

        TextView eventName = (TextView) listItemView.findViewById(R.id.event_name);
        eventName.setText(currentEvent.getEventName());

        String dateTime = currentEvent.getDate() + ", " + currentEvent.getTime();
        TextView eventDateTime = (TextView) listItemView.findViewById(R.id.event_date);
        eventDateTime.setText(dateTime);

        TextView eventPlace = (TextView) listItemView.findViewById(R.id.event_place);
        eventPlace.setText(currentEvent.getPlace());

        return listItemView;
    }


}
