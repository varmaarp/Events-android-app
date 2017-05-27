package com.example.arpit.sportit.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.example.arpit.sportit.DataClasses.Event;
import com.example.arpit.sportit.R;

import java.util.ArrayList;

/**
 * Created by Arpit on 09-05-2017.
 */

public class EventAdaptor extends ArrayAdapter<Event> {

    static class ViewHolderItem{
        private TextView vEventName;
        private TextView vEventPlace;
        private TextView vEventDate;
    }

    public EventAdaptor(Context context, ArrayList<Event> events){
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolderItem holder;

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
            holder = new ViewHolderItem();
            holder.vEventName = (TextView) convertView.findViewById(R.id.event_name);
            holder.vEventPlace = (TextView) convertView.findViewById(R.id.event_place);
            holder.vEventDate = (TextView) convertView.findViewById(R.id.event_date);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolderItem) convertView.getTag();
        }

        Event currentEvent = getItem(position);

        holder.vEventName.setText(currentEvent.getEventName());
        if (currentEvent.getPlace().indexOf('|') != -1) {
            holder.vEventPlace.setText(currentEvent.getPlace().substring(0, currentEvent.getPlace().indexOf('|')));
        }else{
            holder.vEventPlace.setText(currentEvent.getPlace());
        }
        String dateTime = currentEvent.getDate() + ", " + currentEvent.getTime();
        holder.vEventDate.setText(dateTime);
        /*
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
        */

        return convertView;
    }


}
